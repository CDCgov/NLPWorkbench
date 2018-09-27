package org.apache.ctakes.core.cc.pretty.html;


import org.apache.ctakes.core.cc.pretty.SemanticGroup;
import org.apache.ctakes.core.cc.pretty.textspan.DefaultTextSpan;
import org.apache.ctakes.core.cc.pretty.textspan.TextSpan;
import org.apache.ctakes.core.pipeline.PipeBitInfo;
import org.apache.ctakes.core.resource.FileLocator;
import org.apache.ctakes.core.util.DocumentIDAnnotationUtil;
import org.apache.ctakes.core.util.OntologyConceptUtil;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.textsem.EventMention;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.ctakes.typesystem.type.textsem.TimeMention;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.log4j.Logger;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.CasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.ctakes.core.config.ConfigParameterConstants.DESC_OUTPUTDIR;
import static org.apache.ctakes.core.config.ConfigParameterConstants.PARAM_OUTPUTDIR;
import static org.apache.ctakes.core.pipeline.PipeBitInfo.TypeProduct.*;
import static org.apache.ctakes.core.pipeline.PipeBitInfo.TypeProduct.TEMPORAL_RELATION;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 9/8/2016
 */
@PipeBitInfo(
      name = "HTML Writer",
      description = "Writes html files with document text and simple markups (Semantic Group, CUI, Negation).",
      role = PipeBitInfo.Role.WRITER,
      dependencies = { DOCUMENT_ID, SENTENCE, BASE_TOKEN },
      usables = { DOCUMENT_ID_PREFIX, IDENTIFIED_ANNOTATION, EVENT, TIMEX, TEMPORAL_RELATION }
)
final public class HtmlTextWriter extends CasConsumer_ImplBase {

   static private final Logger LOGGER = Logger.getLogger( "HtmlTextWriter" );


   static private final String FILE_EXTENSION = ".pretty.html";

   @ConfigurationParameter(
         name = PARAM_OUTPUTDIR,
         mandatory = false,
         description = DESC_OUTPUTDIR,
         defaultValue = ""
   )
   private String _outputDirPath;

   /**
    * @param outputDirectoryPath may be empty or null, in which case the current working directory is used
    * @throws IllegalArgumentException if the provided path points to a File and not a Directory
    * @throws SecurityException        if the File System has issues
    */
   public void setOutputDirectory( final String outputDirectoryPath ) throws IllegalArgumentException,
                                                                             SecurityException {
      // If no outputDir is specified (null or empty) the current working directory will be used.  Else check path.
      if ( outputDirectoryPath == null || outputDirectoryPath.isEmpty() ) {
         LOGGER.debug( "No Output Directory Path specified, using current working directory "
                       + System.getProperty( "user.dir" ) );
         _outputDirPath = System.getProperty( "user.dir" );
         return;
      }
      String fullDirPath;
      try {
         fullDirPath = FileLocator.getFullPath( outputDirectoryPath );
         final File outputDir = new File( fullDirPath );
         if ( !outputDir.exists() ) {
            outputDir.mkdirs();
         }
         if ( !outputDir.isDirectory() ) {
            throw new IllegalArgumentException( outputDirectoryPath + " is not a valid directory path" );
         }
      } catch ( FileNotFoundException fnfE ) {
         throw new IllegalArgumentException( outputDirectoryPath + " is not a valid directory path" );
      }
      _outputDirPath = fullDirPath;
      LOGGER.debug( "Output Directory Path set to " + _outputDirPath );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void process( final CAS aCAS ) throws AnalysisEngineProcessException {
      try {
         final JCas jcas = aCAS.getJCas();
         process( jcas );
      } catch ( CASException casE ) {
         throw new AnalysisEngineProcessException( casE );
      }
   }

   /**
    * Process the jcas and write pretty sentences to file.  Filename is based upon the document id stored in the cas
    *
    * @param jcas ye olde ...
    */
   public void process( final JCas jcas ) {
      LOGGER.info( "Starting processing" );
      final String docId = DocumentIDAnnotationUtil.getDocumentIdForFile( jcas );
      final File outputFile = new File( _outputDirPath + "/" + docId + FILE_EXTENSION );
      final String cssPath = _outputDirPath + "/ctakes.pretty.css";
      try ( final BufferedWriter writer = new BufferedWriter( new FileWriter( outputFile ) ) ) {
         writer.write( getHeader() );
         writer.write( getCssLink( "ctakes.pretty.css" ) );

         final Collection<Sentence> sentences = JCasUtil.select( jcas, Sentence.class );
         for ( Sentence sentence : sentences ) {
            writeSentence( jcas, sentence, writer );
         }

         writer.write( getFooter() );
      } catch ( IOException ioE ) {
         LOGGER.error( "Could not not write html file " + outputFile.getPath() );
         LOGGER.error( ioE.getMessage() );
      }
      CssWriter.writeCssFile( cssPath );
      LOGGER.info( "Finished processing" );
   }


   /**
    * Write a paragraph from the document text
    *
    * @param jcas      ye olde ...
    * @param paragraph annotation containing the paragraph
    * @param writer    writer to which pretty html for the paragraph should be written
    * @throws IOException if the writer has issues
    */
   static private void writeParagraph( final JCas jcas,
                                       final AnnotationFS paragraph,
                                       final BufferedWriter writer ) throws IOException {
      final String sentenceText = paragraph.getCoveredText().trim();
      if ( sentenceText.isEmpty() ) {
         return;
      }
      final Map<TextSpan, String> baseTokenMap = createBaseTokenMap( jcas, paragraph );
      if ( baseTokenMap.isEmpty() ) {
         return;
      }
      final Map<TextSpan, Collection<IdentifiedAnnotation>> annotationMap = createAnnotationMap( jcas, paragraph );
      final Map<Integer, String> tags = createTags( annotationMap );
      final StringBuilder sb = new StringBuilder();
      for ( Map.Entry<TextSpan, String> entry : baseTokenMap.entrySet() ) {
         final String beginTag = tags.get( entry.getKey().getBegin() );
         if ( beginTag != null ) {
            sb.append( beginTag );
         }
         sb.append( entry.getValue() );
         final String endTag = tags.get( entry.getKey().getEnd() );
         if ( endTag != null ) {
            sb.append( endTag );
         }
         sb.append( " " );
      }
      writer.write( "<p>\n" + sb.toString() + "\n</p>\n" );
   }


   /**
    * Write a sentence from the document text
    *
    * @param jcas     ye olde ...
    * @param sentence annotation containing the sentence
    * @param writer   writer to which pretty text for the sentence should be written
    * @throws IOException if the writer has issues
    */
   static private void writeSentence( final JCas jcas,
                                      final AnnotationFS sentence,
                                      final BufferedWriter writer ) throws IOException {
      final String sentenceText = sentence.getCoveredText().trim();
      if ( sentenceText.isEmpty() ) {
         return;
      }
      // Map of TextSpans to their covered text
      final Map<TextSpan, String> baseTokenMap = createBaseTokenMap( jcas, sentence );
      if ( baseTokenMap.isEmpty() ) {
         return;
      }
      // Map of TextSpans to their covered annotations
      final Map<TextSpan, Collection<IdentifiedAnnotation>> annotationMap = createAnnotationMap( jcas, sentence );
      final Map<Integer, String> tags = createTags( annotationMap );
      final StringBuilder sb = new StringBuilder();
      int previousEndIndex = -1;
      boolean annotation;
      for ( Map.Entry<TextSpan, String> entry : baseTokenMap.entrySet() ) {
         annotation = false;
         final TextSpan textSpan = entry.getKey();
         if ( textSpan.getBegin() != previousEndIndex ) {
            // If the previous end index was this begin index then the tag was already written
            final String beginTag = tags.get( textSpan.getBegin() );
            if ( beginTag != null ) {
               sb.append( beginTag );
               annotation = true;
            }
         }
//         if ( annotation ) {
//            sb.append( "<b>" ).append( entry.getValue().charAt( 0 ) ).append( "</b>" ).append( entry.getValue().substring( 1 ) );
//         } else {
         sb.append( entry.getValue() );
//         }
         final String endTag = tags.get( textSpan.getEnd() );
         if ( endTag != null ) {
            sb.append( endTag );
         }
         sb.append( " " );
         previousEndIndex = textSpan.getEnd();
      }
      writer.write( "<div>\n" + sb.toString() + "\n<br></div>\n" );
   }


   static private Map<TextSpan, String> createBaseTokenMap( final JCas jcas, final AnnotationFS sentence ) {
      final int sentenceBegin = sentence.getBegin();
      final Collection<BaseToken> baseTokens = JCasUtil.selectCovered( jcas, BaseToken.class, sentence );
      final Map<TextSpan, String> baseItemMap = new LinkedHashMap<>();
      for ( BaseToken baseToken : baseTokens ) {
         final TextSpan textSpan = new DefaultTextSpan( baseToken, sentenceBegin );
         if ( textSpan.getWidth() == 0 ) {
            continue;
         }
         if ( baseToken instanceof NewlineToken ) {
            baseItemMap.put( textSpan, " " );
            continue;
         }
         baseItemMap.put( textSpan, baseToken.getCoveredText() );
      }
      return baseItemMap;
   }

   static private Map<TextSpan, Collection<IdentifiedAnnotation>> createAnnotationMap( final JCas jcas,
                                                                                       final AnnotationFS sentence ) {
      final Map<TextSpan, Collection<IdentifiedAnnotation>> annotationMap = new HashMap<>();
      final int sentenceBegin = sentence.getBegin();
      final Collection<IdentifiedAnnotation> identifiedAnnotations
            = JCasUtil.selectCovered( jcas, IdentifiedAnnotation.class, sentence );
      for ( IdentifiedAnnotation annotation : identifiedAnnotations ) {
         final TextSpan textSpan = new DefaultTextSpan( annotation, sentenceBegin );
         if ( textSpan.getWidth() == 0 ) {
            continue;
         }
         final Collection<String> semanticNames = getSemanticNames( annotation );
         if ( !semanticNames.isEmpty() || annotation instanceof TimeMention || annotation instanceof EventMention ) {
            Collection<IdentifiedAnnotation> annotations = annotationMap.get( textSpan );
            if ( annotations == null ) {
               annotations = new ArrayList<>();
               annotationMap.put( textSpan, annotations );
            }
            annotations.add( annotation );
         }
      }
      return annotationMap;
   }


   static private Collection<String> getSemanticNames( final IdentifiedAnnotation identifiedAnnotation ) {
      final Collection<UmlsConcept> umlsConcepts = OntologyConceptUtil.getUmlsConcepts( identifiedAnnotation );
      if ( umlsConcepts == null || umlsConcepts.isEmpty() ) {
         return Collections.emptyList();
      }
      final Collection<String> semanticNames = new HashSet<>();
      for ( UmlsConcept umlsConcept : umlsConcepts ) {
         final String tui = umlsConcept.getTui();
         String semanticName = SemanticGroup.getSemanticName( tui );
         if ( semanticName.equals( "Unknown" ) ) {
            semanticName = identifiedAnnotation.getClass().getSimpleName();
         }
         semanticNames.add( semanticName );
      }
      final List<String> semanticList = new ArrayList<>( semanticNames );
      Collections.sort( semanticList );
      return semanticList;
   }


   static private Map<Integer, String> createTags(
         final Map<TextSpan, Collection<IdentifiedAnnotation>> annotationMap ) {
      if ( annotationMap.isEmpty() ) {
         return Collections.emptyMap();
      }
      final Collection<Integer> indices = new HashSet<>();
      final Map<Integer, Collection<String>> polarities = new HashMap<>();
      final Map<Integer, Collection<String>> beginClasses = new HashMap<>();
      final Map<Integer, Collection<String>> endClasses = new HashMap<>();
      for ( Map.Entry<TextSpan, Collection<IdentifiedAnnotation>> entry : annotationMap.entrySet() ) {
         final Collection<String> tagClasses = createClasses( entry.getValue() );
         if ( tagClasses.isEmpty() ) {
            continue;
         }
         final TextSpan textSpan = entry.getKey();
         indices.add( textSpan.getBegin() );
         indices.add( textSpan.getEnd() );
         // add all class tags that begin at this textSpan
         addAll( beginClasses, textSpan.getBegin(), tagClasses );
         // add all class tags that end at this text span
         addAll( endClasses, textSpan.getEnd(), tagClasses );
         final Collection<String> polarity = createPolarity( entry.getValue() );
         addAll( polarities, textSpan.getBegin(), polarity );
         addAll( polarities, textSpan.getEnd(), polarity );
      }
      if ( indices.isEmpty() ) {
         return Collections.emptyMap();
      }
      final List<Integer> indexList = new ArrayList<>( indices );
      Collections.sort( indexList );
      final Map<Integer, String> tagMap = new HashMap<>();
      final Collection<String> currentClasses = new HashSet<>();
      String currentTag = "";
      for ( Integer index : indexList ) {
         currentTag = currentClasses.isEmpty() ? "" : "</span>";
         final Collection<String> enders = endClasses.get( index );
         if ( enders != null ) {
            // remove all of the classes that end here
            currentClasses.removeAll( enders );
            if ( currentClasses.isEmpty() ) {
               // all annotations have ended, go to the next index
               if ( !currentTag.isEmpty() ) {
                  tagMap.put( index, currentTag );
               }
               continue;
            }
            final Collection<String> currentPolarity = polarities.get( index );
            final String polarClasses = String.join( " ", currentPolarity ) + " " + String.join( " ", currentClasses );
            final String toolTip = currentClasses.isEmpty() ? "" : " data-tooltip=\"" + polarClasses + "\"";
            // tag for the classes that continue into the next textspan
            currentTag += "<span class=\"" + polarClasses + "\"" + toolTip + ">";
            tagMap.put( index, currentTag );
            continue;
         }
         final Collection<String> beginners = beginClasses.get( index );
         if ( beginners != null ) {
            int size = currentClasses.size();
            currentClasses.addAll( beginners );
            if ( currentClasses.size() != size ) {
               final Collection<String> currentPolarity = polarities.get( index );
               final String polarClasses = String.join( " ", currentPolarity ) + " " +
                                           String.join( " ", currentClasses );
               final String toolTip = currentClasses.isEmpty() ? "" : " data-tooltip=\"" + polarClasses + "\"";
               // tag for the classes that continue and begin the next textspan
               currentTag += "<span class=\"" + polarClasses + "\"" + toolTip + ">";
               tagMap.put( index, currentTag );
            }
         }
      }
      if ( !currentTag.endsWith( "</span>" ) ) {
         tagMap.put( indexList.get( indexList.size() - 1 ), currentTag + "</span>" );
      }
      return tagMap;
   }


   static private void addAll( final Map<Integer, Collection<String>> map, final Integer index,
                               final Collection<String> values ) {
      // add all class tags that end at this text span
      Collection<String> set = map.get( index );
      if ( set == null ) {
         set = new HashSet<>();
         map.put( index, set );
      }
      set.addAll( values );
   }


   static private Collection<String> createPolarity( final Collection<IdentifiedAnnotation> annotations ) {
      return annotations.stream()
            .map( HtmlTextWriter::createPolarity )
            .flatMap( Collection::stream )
            .collect( Collectors.toSet() );
   }

   static private Collection<String> createPolarity( final IdentifiedAnnotation annotation ) {
      final Collection<String> tags = new ArrayList<>();
      if ( annotation.getPolarity() < 0 ) {
         if ( annotation.getUncertainty() > 0 ) {
            tags.add( "uncertainnegated" );
         } else {
            tags.add( "negated" );
         }
      } else if ( annotation.getUncertainty() > 0 ) {
         tags.add( "uncertain" );
      } else {
         tags.add( "affirmed" );
      }
      return tags;
   }

   static private Collection<String> createClasses( final Collection<IdentifiedAnnotation> annotations ) {
      return annotations.stream()
            .map( HtmlTextWriter::getSemanticNames )
            .flatMap( Collection::stream )
            .distinct()
            .sorted()
            .collect( Collectors.toList() );
   }


   //   // Can do something like
//   //    Patient has a <a class="affirmed finding" id="finding1" href="#site9">rash</a> on his <textspan class="anatomy" id="site9">elbow</textspan>.
//   //  #site9:target { font-weight: bold; }
//
//   //  Can change background of b when hovering over a  :
//   // #a:hover ~ #b {  background: #ccc  }
//   //   iff b is after a.  Can not change a before b by hovering over b


   static private String addBackgroundLink( final String idName, final String color ) {
      return "onmouseover=\"linkBg(" + idName + "," + color + ")\" onmouseout=\"linkBg(" + idName + ",white)\"";
   }


   static private String getCssLink( final String filePath ) {
      return "<link rel=\"stylesheet\" href=\"" + filePath + "\" type=\"text/css\" media=\"screen\">";
   }


   static private String getHeader() {
      return "<!DOCTYPE html>\n" +
             "<html>\n" +
             "<body>\n";
   }

   static private String getFooter() {
      return "</body>\n" +
             "</html>\n";
   }


   // CSS


   // javascript

   static private String startJavascript() {
      return "<script type=\"text/javascript\">";
   }

   static private String endJavascript() {
      return "</script>";
   }

   static private String getLinkBackgrounds() {
      return "  function linkBg(id,color) {\n" +
             "    document.getElementById(id).style.backgroundColor = color;\n" +
             "  }\n";
   }


}
