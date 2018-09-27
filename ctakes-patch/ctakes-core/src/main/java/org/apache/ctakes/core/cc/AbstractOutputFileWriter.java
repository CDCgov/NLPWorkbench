package org.apache.ctakes.core.cc;


import org.apache.ctakes.core.config.ConfigParameterConstants;
import org.apache.ctakes.core.util.DocumentIDAnnotationUtil;
import org.apache.ctakes.typesystem.type.structured.DocumentIdPrefix;
import org.apache.ctakes.typesystem.type.structured.DocumentPath;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 9/16/2016
 */
abstract public class AbstractOutputFileWriter extends CasConsumer_ImplBase {

   static private final Logger LOGGER = Logger.getLogger( "AbstractOutputFileWriter" );


   /**
    * Name of configuration parameter that must be set to the path of a directory into which the
    * output files will be written.
    */
   @ConfigurationParameter(
         name = ConfigParameterConstants.PARAM_OUTPUTDIR,
         description = ConfigParameterConstants.DESC_OUTPUTDIR
   )
   private File _outputRootDir;

   @ConfigurationParameter(
         name = ConfigParameterConstants.PARAM_SUBDIR,
         description = ConfigParameterConstants.DESC_SUBDIR,
         mandatory = false,
         defaultValue = ""
   )
   private String _subDirectory;

   /**
    * {@inheritDoc}
    */
   @Override
   public void initialize( final UimaContext context ) throws ResourceInitializationException {
      super.initialize( context );
      if ( _subDirectory != null && !_subDirectory.isEmpty() ) {
         final File subDirectory = new File( _outputRootDir, _subDirectory );
         if ( !subDirectory.exists() ) {
            subDirectory.mkdirs();
         }
      } else if ( !_outputRootDir.exists() ) {
         _outputRootDir.mkdirs();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final CAS cas ) throws AnalysisEngineProcessException {
      JCas jcas;
      try {
         jcas = cas.getJCas();
      } catch ( CASException casE ) {
         throw new AnalysisEngineProcessException( casE );
      }
      final String documentId = DocumentIDAnnotationUtil.getDocumentIdForFile( jcas );
      final String outputDir = getOutputDirectory( jcas, _outputRootDir.getPath(), documentId );
      final String fileName = getSourceFileName( jcas, documentId );
      try {
         writeFile( jcas, outputDir, documentId, fileName );
      } catch ( IOException ioE ) {
         throw new AnalysisEngineProcessException( ioE );
      }
   }


   /**
    * Write information into a file named based upon the document id and located based upon the document id prefix.
    *
    * @param jCas       ye olde
    * @param outputDir  output directory
    * @param documentId some id for the cas document
    * @param fileName   name for the output file
    * @throws IOException if anything goes wrong
    */
   abstract public void writeFile( final JCas jCas,
                                   final String outputDir,
                                   final String documentId,
                                   final String fileName ) throws IOException;


   /**
    * @param jcas       ye olde
    * @param rootPath   the root path for all output subdirectories and files
    * @param documentId some id for the cas document
    * @return the full output path up to but not including the fileName
    */
   protected String getOutputDirectory( final JCas jcas, final String rootPath, final String documentId ) {
      String subDirectory = getSubdirectory( jcas, documentId );
      if ( subDirectory == null || subDirectory.isEmpty() ) {
         return rootPath;
      }
      final File outputDir = new File( rootPath + "/" + subDirectory );
      outputDir.mkdirs();
      return outputDir.getPath();
   }

   /**
    * @param jCas       ye olde
    * @param documentId some id for the cas document
    * @return a subdirectory based upon the {@link DocumentIdPrefix} stored in the cas, or none if none
    */
   protected String getSubdirectory( final JCas jCas, final String documentId ) {
      String subDirectory = "";
      String subSeparator = "";
      if ( _subDirectory != null && !_subDirectory.isEmpty() ) {
         subDirectory = _subDirectory;
         subSeparator = "/";
      }
      final Collection<DocumentIdPrefix> prefices = JCasUtil.select( jCas, DocumentIdPrefix.class );
      if ( prefices == null || prefices.isEmpty() ) {
         LOGGER.debug( "No subdirectory information for " + documentId );
         return subDirectory;
      }
      for ( DocumentIdPrefix prefix : prefices ) {
         String docSubDirectory = prefix.getDocumentIdPrefix();
         if ( docSubDirectory != null && !docSubDirectory.isEmpty() ) {
            return subDirectory + subSeparator + docSubDirectory;
         }
      }
      LOGGER.debug( "No subdirectory information for " + documentId );
      return subDirectory;
   }

   /**
    * @param jCas ye olde
    * @return the full path to the file containing the processed text, or an empty string ("") if unknown
    */
   protected String getSourceFilePath( final JCas jCas ) {
      final Collection<DocumentPath> documentPaths = JCasUtil.select( jCas, DocumentPath.class );
      if ( documentPaths == null || documentPaths.isEmpty() ) {
         return "";
      }
      for ( DocumentPath documentPath : documentPaths ) {
         final String path = documentPath.getDocumentPath();
         if ( path != null && !path.isEmpty() ) {
            return path;
         }
      }
      return "";
   }

   /**
    * @param jcas       ye olde
    * @param documentId some id for the cas document
    * @return a filename based upon the documentId
    */
   protected String getSourceFileName( final JCas jcas, final String documentId ) {
      final String path = getSourceFilePath( jcas );
      if ( path != null && !path.isEmpty() ) {
         return new File( path ).getName();
      }
      return documentId;
   }


}
