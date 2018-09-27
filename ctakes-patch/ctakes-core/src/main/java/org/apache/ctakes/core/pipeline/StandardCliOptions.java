package org.apache.ctakes.core.pipeline;

import com.lexicalscope.jewel.cli.Option;

/**
 * Standard Options for ctakes.  For instance; -p PiperFile, -i InputDir, -o OutputDir.
 *
 * @author SPF , chip-nlp
 * @version %I%
 * @since 1/7/2017
 */
interface StandardCliOptions {
   @Option(
         shortName = "p",
         longName = "piper",
         description = "path to the piper file containing commands and parameters for pipeline configuration." )
   String getPiperPath();

   @Option(
         shortName = "i",
         longName = "inputDir",
         description = "path to the directory containing the clinical notes to be processed.",
         defaultValue = "" )
   String getInputDirectory();

   @Option(
         shortName = "o",
         longName = "outputDir",
         description = "path to the directory where the output files are to be written.",
         defaultValue = "" )
   String getOutputDirectory();

   @Option(
         shortName = "s",
         longName = "subDir",
         description = "path to a subdirectory for input and/or output.",
         defaultValue = "" )
   String getSubDirectory();

   @Option(
         longName = "xmiOut",
         description = "path to the directory where xmi files are to be written.  Adds XmiWriter to pipeline.",
         defaultValue = "" )
   String getXmiOutDirectory();

   @Option(
         shortName = "l",
         longName = "lookupXml",
         description = "path to the xml file containing information for dictionary lookup configuration.",
         defaultValue = "" )
   String getLookupXml();

   @Option(
         longName = "user",
         description = "UMLS username.",
         defaultValue = "" )
   String getUmlsUserName();

   @Option(
         longName = "pass",
         description = "UMLS user password.",
         defaultValue = "" )
   String getUmlsPassword();

   @Option(
         shortName = "?",
         longName = "help",
         description = "print usage.",
         helpRequest = true )
   boolean isHelpWanted();
}
