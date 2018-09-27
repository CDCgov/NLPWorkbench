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
package org.apache.ctakes.core.util;

import org.apache.ctakes.typesystem.type.structured.DocumentID;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JFSIndexRepository;
import org.apache.uima.jcas.cas.TOP;

import java.util.regex.Pattern;

/**
 * Utility class for fetching document id
 */
final public class DocumentIDAnnotationUtil {

   // Added for CTAKES-365
   static public final String NO_DOCUMENT_ID = "UnknownDocument";

   static private final Logger LOGGER = Logger.getLogger( "DocumentIDAnnotationUtil" );

   static private final Pattern FILE_FIX_PATTERN = Pattern.compile( "[^A-Za-z0-9\\.]" );

   static private long _noDocIdIndex = 1;

   // Utility classes should be final and have only a private constructor
   private DocumentIDAnnotationUtil() {
   }

   /**
    * Check the jcas for a document id.  Unlike {@link #getDeepDocumentId(JCas)},
    * this method does not progress into deeper jcas layers/views.
    *
    * @param jcas ye olde ...
    * @return the document id contained in the type "DocumentID" or {@link #NO_DOCUMENT_ID}
    */
   public static String getDocumentID( final JCas jcas ) {
      if ( jcas == null ) {
         // could throw an IllegalArgumentException,
         // but a caller might be providing a null view, so a graceful handling is better
         LOGGER.debug( "NULL CAS" );
         return NO_DOCUMENT_ID;
      }
      // todo - improve the FS handling
      final JFSIndexRepository indexes = jcas.getJFSIndexRepository();
      final FSIterator<TOP> documentIDIterator = indexes.getAllIndexedFS( DocumentID.type );
      if ( documentIDIterator == null || !documentIDIterator.hasNext() ) {
         LOGGER.warn( "Unable to find DocumentIDAnnotation" );
         return createDocumentId( jcas );
      }
      final DocumentID documentIDAnnotation = (DocumentID)documentIDIterator.next();
      try {
         return documentIDAnnotation.getDocumentID();
      } catch ( CASRuntimeException casRTE ) {
         final String newId = NO_DOCUMENT_ID + _noDocIdIndex;
         _noDocIdIndex++;
         LOGGER.warn( "document Id Annotation does not have the id feature set, setting to " + newId, casRTE );
         documentIDAnnotation.setDocumentID( newId );
         return newId;
      }
   }


   /**
    * Gets the document Id by progressing through 3 layers until an Id is found: starting JCas, Initial View, Plaintext View
    *
    * @param startingJcas initial JCas to start the checking
    * @return Document Id from the starting JCas, the Initial View, the Plaintext View, or {@link #NO_DOCUMENT_ID}
    */
   static public String getDeepDocumentId( final JCas startingJcas ) {
      String documentID = getDocumentID( startingJcas );
      if ( documentID == null || documentID.equals( NO_DOCUMENT_ID ) ) {
         LOGGER.debug( "Checking document Id for initial view" );
         try {
            final JCas viewJcas = startingJcas.getView( "_InitialView" );
            documentID = DocumentIDAnnotationUtil.getDocumentID( viewJcas );
         } catch ( CASException | CASRuntimeException casE ) {
            LOGGER.warn( casE.getMessage() );
            documentID = NO_DOCUMENT_ID;
         }
         if ( documentID == null || documentID.equals( NO_DOCUMENT_ID ) ) {
            LOGGER.debug( "Checking document Id for plaintext view" );
            try {
               final JCas viewJcas = startingJcas.getView( "plaintext" );
               documentID = DocumentIDAnnotationUtil.getDocumentID( viewJcas );
            } catch ( CASException | CASRuntimeException casE ) {
               LOGGER.warn( casE.getMessage() );
               documentID = NO_DOCUMENT_ID;
            }
            if ( documentID == null || documentID.equals( NO_DOCUMENT_ID ) ) {
               LOGGER.warn( "Unable to find DocumentIDAnnotation" );
               return createDocumentId( startingJcas );
            }
         }
      }
      return documentID;
   }

   /**
    * Create a unique id for the document that can be used for an output filename or url.
    * Will be the source document file name if possible,
    * otherwise the first 10 characters of the text plus text hashcode,
    * or "Unknown_" and the current millis if there is no text.
    * Non-alphanumeric characters are replaced with '_'.
    *
    * @param jcas -
    * @return an ok document id
    */
   static public String getDocumentIdForFile( final JCas jcas ) {
      String docId = getDeepDocumentId( jcas );
      if ( docId == null || docId.isEmpty() ) {
         String casDocText = jcas.getDocumentText();
         if ( casDocText != null ) {
            casDocText = casDocText.trim();
            if ( !casDocText.isEmpty() ) {
               docId = casDocText.substring( 0, Math.min( casDocText.length(), 10 ) ) + "_" + casDocText.hashCode();
            }
         }
      }
      if ( docId == null || docId.isEmpty() ) {
         docId = "Unknown_" + System.currentTimeMillis();
      }
      return FILE_FIX_PATTERN.matcher( docId ).replaceAll( "_" );
   }

   /**
    * @param jCas -
    * @return {@link #NO_DOCUMENT_ID} plus an index based upon the number of documents without IDs fetched with this class.
    * This may lead to documents having ids indexed out of order with respect to the order in which they were run.
    */
   static private String createDocumentId( final JCas jCas ) {
      final String newId = NO_DOCUMENT_ID + _noDocIdIndex;
      _noDocIdIndex++;
      LOGGER.debug( "Creating document ID " + newId );
      final DocumentID documentIDAnnotation = new DocumentID( jCas );
      documentIDAnnotation.setDocumentID( newId );
      documentIDAnnotation.addToIndexes();
      return newId;
   }

}
