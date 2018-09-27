/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ctakes.core.cr;

import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileResource;
import org.apache.ctakes.core.resource.JdbcConnectionResource;
import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Collection Reader that pulls documents to be processed from a database.
 * 
 * @author Mayo Clinic
 */
@PipeBitInfo(
      name = "JDBC Reader",
      description = "Reads document texts from database text fields.",
      role = PipeBitInfo.Role.READER,
      products = { PipeBitInfo.TypeProduct.DOCUMENT_ID }
)
public class JdbcCollectionReader extends CollectionReader_ImplBase
{

    // LOG4J logger based on class name
    private Logger logger = Logger.getLogger(getClass().getName());

    /**
     * SQL statement to retrieve the document.
     */
    public static final String PARAM_SQL = "SqlStatement";

    /**
     * Name of column from resultset that contains the document text. Supported
     * column types are CHAR, VARCHAR, and CLOB.
     */
    public static final String PARAM_DOCTEXT_COL = "DocTextColName";

    /**
     * Name of external resource for database connection.
     */
    public static final String PARAM_DB_CONN_RESRC = "DbConnResrcName";

    /**
     * Optional parameter. Specifies column names that will be used to form a
     * document ID.
     */
    public static final String PARAM_DOCID_COLS = "DocIdColNames";

    /**
     * Optional parameter. Specifies delimiter used when document ID is built.
     */
    public static final String PARAM_DOCID_DELIMITER = "DocIdDelimiter";

    /**
     * Optional parameter. Name of external resource for prepared statement
     * value file. Each line of this file represents prepared statement values
     * that will be used to substitute for the "?" placeholders. TAB character
     * \t is used to delimit the values on a single line. The prepared statement
     * will be called once for each line in this file.
     */
    public static final String PARAM_VALUE_FILE_RESRC = "ValueFileResrcName";

    private PreparedStatement queryPrepStmt;
    private ResultSet rs;

    private String docTextColName;
    private int docColType;
    private String docColTypeName;

    // optional, will remain null if not set
    private String[] docIdColNames = null;

    // default is underscore
    private String docIdDelimiter = "_";

    private int totalRowCount = 0;
    private int currRowCount = 0;

    // optional, will remain null if not set
    // Array of List objects. Each List objects represents a list of prepared
    // stmt values.
    private List<String>[] prepStmtValArr = null;
    private int prepStmtValArrIdx = 0;
    private boolean usePrepStmtVals = false;

   @Override
    public void initialize() throws ResourceInitializationException
    {
        try
        {
            String sql = (String) getConfigParameterValue(PARAM_SQL);
            docTextColName = (String) getConfigParameterValue(PARAM_DOCTEXT_COL);
            String resrcName = (String) getConfigParameterValue(PARAM_DB_CONN_RESRC);
            JdbcConnectionResource resrc = (JdbcConnectionResource) getUimaContext().getResourceObject(resrcName);

            docIdColNames = (String[]) getConfigParameterValue(PARAM_DOCID_COLS);
            if (getConfigParameterValue(PARAM_DOCID_DELIMITER) != null)
            {
                docIdDelimiter = (String) getConfigParameterValue(PARAM_DOCID_DELIMITER);
            }

            Connection conn = resrc.getConnection();
            queryPrepStmt = conn.prepareStatement(sql);

            String fileResrcName = (String) getConfigParameterValue(PARAM_VALUE_FILE_RESRC);
            if ((fileResrcName != null) && (fileResrcName.trim().length() > 0))
            {
                FileResource fileResrc = (FileResource) getUimaContext().getResourceObject(fileResrcName);
                if (fileResrc != null)
                {
                    loadValueFile(fileResrc.getFile());
                    usePrepStmtVals = true;
                } else
                {
                    throw new Exception("Failed to get " + fileResrcName
                            + " from ResourceManager");
                }
            }

            totalRowCount = getRowCount(conn, sql);
        } catch (Exception e)
        {
            throw new ResourceInitializationException(e);
        }
    }

    /**
     * Loads the prepared statement value file.
     * 
     * @param valueFile
     * @throws IOException
     */
    private void loadValueFile(File valueFile) throws IOException
    {
        List<String> lineList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader(valueFile));
        String line = br.readLine();
        while (line != null && line.trim().length()>0)
        {
            lineList.add(line);
            line = br.readLine();
        }
        br.close();

        prepStmtValArr = new List[lineList.size()];
        for (int i = 0; i < lineList.size(); i++)
        {
           String currLine = lineList.get( i );
            List<String> valList = new ArrayList<String>();
            StringTokenizer st = new StringTokenizer(currLine, "\t");
            while (st.hasMoreTokens())
            {
                String token = st.nextToken().trim();
                valList.add(token);
            }
            prepStmtValArr[i] = valList;
        }
        logger.info("Loaded " + lineList.size() + " lines from value file: "
                + valueFile.getAbsolutePath());
    }

    /**
     * Slice up the query SQL and rebuild a SQL statement that gets a row count;
     * 
     * @param querySql
     * @return
     */
    private int getRowCount(Connection conn, String querySql)
            throws SQLException
    {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT COUNT(*) ");
        int idx = querySql.indexOf("FROM");
        sb.append(querySql.subSequence(idx, querySql.length()));
        PreparedStatement cntStmt = conn.prepareStatement(sb.toString());

        if (usePrepStmtVals)
        {
            int totalCnt = 0;
            for (int i = 0; i < prepStmtValArr.length; i++)
            {
                List<String> valList = prepStmtValArr[i];
                setPrepStmtValues(cntStmt, valList);
                ResultSet rs = cntStmt.executeQuery();
                rs.next();
                totalCnt += rs.getInt(1);
            }
            return totalCnt;
        } else
        {
            ResultSet rs = cntStmt.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    /**
     * Helper method that sets the prepared statement values.
     * 
     * @param prepStmt
     * @param valList
     */
    private void setPrepStmtValues(PreparedStatement prepStmt, List<String> valList)
            throws SQLException
    {
        prepStmt.clearParameters();
        for (int i = 0; i < valList.size(); i++)
        {
            Object valObj = valList.get(i);
            prepStmt.setObject(i + 1, valObj);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.uima.collection.CollectionReader#getNext(org.apache.uima.cas.CAS)
     */
    @Override
    public void getNext( CAS cas ) throws IOException, CollectionException
    {
        currRowCount++;
        try
        {
            // pull doc text from resultset
            String document = null;
            if ((docColType == Types.CHAR)
                    || (docColType == Types.VARCHAR))
            {
                document = rs.getString(docTextColName);
            } else if (docColType == Types.CLOB)
            {
                document = convertToString(rs.getClob(docTextColName));
            } else
            {
                throw new Exception("Unsupported document text column type: "
                        + docColTypeName);
            }

            try
            {
                // if there's a CAS Initializer, call it
                if (getCasInitializer() != null)
                {
                    Reader reader = new StringReader(document);
                    getCasInitializer().initializeCas(reader, cas);
                } else
                {
                    // No CAS Initiliazer, so set document text ourselves.
                    // put document in CAS (assume CAS)
                    cas.getJCas().setDocumentText(document);
                }

                DocumentID docIdAnnot = new DocumentID(cas
                        .getJCas());
                docIdAnnot.setDocumentID(getDocumentID(rs));
                docIdAnnot.addToIndexes();

                logger.info("Reading document with ID="
                        + docIdAnnot.getDocumentID());
            } catch (Exception e)
            {
                logger.error("CasInitializer failed to process document: ");
                logger.error(document);
                throw e;
            }
        } catch (Exception e)
        {
            throw new CollectionException(e);
        }
    }

    /**
     * Builds a document ID from one or more pieces of query data. If the query
     * data is not specified, the current row # is used.
     * 
     * @param rs
     * @return
     */
    private String getDocumentID(ResultSet rs) throws SQLException
    {
        if (docIdColNames != null)
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < docIdColNames.length; i++)
            {
                String val = rs.getObject(docIdColNames[i]).toString();
                sb.append(val);
                if (i != (docIdColNames.length - 1))
                {
                    sb.append(docIdDelimiter);
                }
            }
            return sb.toString();
        } else
        {
            // default is to return row num
            return String.valueOf(currRowCount);
        }
    }

    /**
     * Loads the clob data into a String object.
     * 
     * @param clob
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private String convertToString(Clob clob) throws SQLException, IOException
    {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(clob.getCharacterStream());
        String line = br.readLine();
        while (line != null)
        {
            sb.append(line);
            sb.append('\n');
            line = br.readLine();
        }
        br.close();
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#hasNext()
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException
    {
        try
        {

            if (rs == null)
            {
                if (usePrepStmtVals)
                {
                    List<String> valList = prepStmtValArr[prepStmtValArrIdx];
                    setPrepStmtValues(queryPrepStmt, valList);
                    prepStmtValArrIdx++;
                }

                rs = queryPrepStmt.executeQuery();

                // TODO only needs to be done once
                ResultSetMetaData rsMetaData = rs.getMetaData();
                int colIdx = rs.findColumn(docTextColName);
                docColType = rsMetaData.getColumnType(colIdx);
                docColTypeName = rsMetaData.getColumnTypeName(1);
            }

            boolean hasAnotherRow = rs.next();
            if (hasAnotherRow == false)
            {
                // it's important to close ResultSets as they can accumlate
                // in the JVM heap. Too many open result sets can inadvertently
                // cause the DB conn to be closed by the server.
                rs.close();
            }

            if (usePrepStmtVals)
            {
                if ((hasAnotherRow == false)
                        && (prepStmtValArrIdx < prepStmtValArr.length))
                {
                    // the results for the previous prepared statement execution
                    // have been exhausted, so the statement needs to be
                    // executed with the next set of values

                    // reset the rs instance variable to NULL
                    rs = null;
                    // re-invoke the hasNext() method so the prepared
                    // statement gets executed again with the next set of values
                    return this.hasNext();
                }
            }

            return hasAnotherRow;
        } catch (Exception e)
        {
            throw new CollectionException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#getProgress()
     */
    @Override
    public Progress[] getProgress()
    {
        Progress p = new ProgressImpl(currRowCount, totalRowCount,
                Progress.ENTITIES);
        return new Progress[] { p };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.uima.collection.base_cpm.BaseCollectionReader#close()
     */
    @Override
    public void close() throws IOException
    {
        try
        {
            queryPrepStmt.close();
        } catch (Exception e)
        {
            throw new IOException(e.getMessage());
        }
    }
}