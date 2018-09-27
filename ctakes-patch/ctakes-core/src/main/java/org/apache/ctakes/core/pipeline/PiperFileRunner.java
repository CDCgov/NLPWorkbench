package org.apache.ctakes.core.pipeline;


import com.lexicalscope.jewel.cli.CliFactory;
import org.apache.ctakes.core.config.ConfigParameterConstants;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAException;

import java.io.IOException;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 10/13/2016
 */
final public class PiperFileRunner {

   static private final Logger LOGGER = Logger.getLogger( "PiperFileRunner" );

   private PiperFileRunner() {
   }

   /**
    * @param args general run options
    */
   public static void main( final String... args ) {
      final CliOptionals options = CliFactory.parseArguments( CliOptionals.class, args );
      try {
         final PiperFileReader reader = new PiperFileReader();
         final PipelineBuilder builder = reader.getBuilder();
         // set the input directory parameter if needed
         final String inputDir = options.getInputDirectory();
         if ( !inputDir.isEmpty() ) {
            builder.set( ConfigParameterConstants.PARAM_INPUTDIR, inputDir );
         }
         // set the output directory parameter if needed
         final String outputDir = options.getOutputDirectory();
         // set the subdirectory parameter if needed
         final String subDir = options.getSubDirectory();
         if ( !subDir.isEmpty() ) {
            builder.set( ConfigParameterConstants.PARAM_SUBDIR, subDir );
         }
         // if xmi output directory is set but standard output directory is not, use xmi out as standard out
         final String xmiOutDir = options.getXmiOutDirectory();
         if ( !outputDir.isEmpty() ) {
            builder.set( ConfigParameterConstants.PARAM_OUTPUTDIR, outputDir );
         } else if ( !xmiOutDir.isEmpty() ) {
            builder.set( ConfigParameterConstants.PARAM_OUTPUTDIR, xmiOutDir );
         }
         // set the dictionary lookup descriptor xml
         final String lookupXml = options.getLookupXml();
         if ( !lookupXml.isEmpty() ) {
            builder.set( ConfigParameterConstants.PARAM_LOOKUP_XML, lookupXml );
         }
         // set the umls user and password parameters if needed
         final String umlsUser = options.getUmlsUserName();
         if ( !umlsUser.isEmpty() ) {
            builder.set( "umlsUser", umlsUser );
            builder.set( "ctakes.umlsuser", umlsUser );
         }
         final String umlsPass = options.getUmlsPassword();
         if ( !umlsPass.isEmpty() ) {
            builder.set( "umlsPass", umlsPass );
            builder.set( "ctakes.umlspw", umlsPass );
         }
         // load the piper file
         reader.setCliOptionals( options );
         reader.loadPipelineFile( options.getPiperPath() );
         // if an input directory was specified but the piper didn't add a collection reader, add the default reader
         if ( !inputDir.isEmpty() && builder.getReader() == null ) {
            builder.readFiles( inputDir );
         }
         // if an xmi output directory was specified but the piper didn't add the xmi writer, add the
         if ( !xmiOutDir.isEmpty() ) {
            if ( !builder.getAeNames().stream().map( String::toLowerCase )
                  .anyMatch( n -> n.contains( "xmiwriter" ) ) ) {
               builder.writeXMIs( xmiOutDir );
            }
         }
         // run the pipeline
         builder.run();
      } catch ( UIMAException | IOException multE ) {
         LOGGER.error( multE.getMessage() );
         System.exit( 1 );
      }
   }


}
