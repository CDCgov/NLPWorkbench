package org.apache.ctakes.core.cc.pretty.html;


import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 10/15/2016
 */
final class CssWriter {

   static private final Logger LOGGER = Logger.getLogger( "CssWriter" );


   private CssWriter() {
   }

   static boolean writeCssFile( final String filePath ) {
      final File outputFile = new File( filePath );
      if ( outputFile.exists() ) {
         return false;
      }
      try ( final BufferedWriter writer = new BufferedWriter( new FileWriter( outputFile ) ) ) {
         writer.write( setBody() );
         writer.write( setUnderline( "affirmed", "green", "solid", "0.2" ) );
         writer.write( setUnderline( "uncertain", "gold", "dotted", "0.2" ) );
         writer.write( setUnderline( "negated", "red", "dashed", "0.2" ) );
         writer.write( setUnderline( "uncertainnegated", "orange", "dashed", "0.2" ) );
//         writer.write( setColor( "Anatomy", "gray" ) );
//         writer.write( setColor( "Disorder", "black" ) );
//         writer.write( setColor( "Finding", "magenta" ) );
//         writer.write( setColor( "Drug", "red" ) );
//         writer.write( setColor( "Procedure", "blue" ) );
         writer.write( getToolTipCss() );
      } catch ( IOException ioE ) {
         LOGGER.error( "Could not not write css file " + outputFile.getPath() );
         LOGGER.error( ioE.getMessage() );
      }
      return true;
   }


   static private String setBody() {
      return "\nbody {\n" +
             "  margin: 20px;\n" +
             "}\n" +
             "\ndiv {\n" +
             "  margin-bottom: 0.4em;\n" +
             "}\n";
   }

   // dashType is solid or dashed or double or dotted, try wavy      size is relative: 0.1 or 0.2 for 10%, 20%
   static private String setUnderline( final String className, final String color, final String dashType,
                                       final String size ) {
      return "\n." + className + " {\n" +
             "  position: relative;\n" +
             "  display: inline-block " + color + ";\n" +
             "  border-bottom: " + size + "em " + dashType + " " + color + ";\n" +
             "}\n";
   }

   static private String setColor( final String className, final String color ) {
      return "\n." + className + "::first-letter {\n" +
//             "  color: " + color + ";\n" +
             "  font-weight: bold;\n" +
             "}\n";
   }


   static private String setHighlight( final String idName, final String color ) {
      // PowderBlue
      return "#" + idName + "{\n  background-color: " + color + ";\n}\n";
   }

   static private String getToolTipCss() {
      return // position z
            "\n[data-tooltip] {\n" +
            "  position: relative;\n" +
            "  z-index: 2;\n" +
            "  cursor: pointer;\n" +
            "}\n" +
            // invisible
            "[data-tooltip]::before,\n" +
            "[data-tooltip]::after {\n" +
            "  visibility: hidden;\n" +
            "  -ms-filter: \"progid:DXImageTransform.Microsoft.Alpha(Opacity=0)\";\n" +
            "  filter: progid: DXImageTransform.Microsoft.Alpha(Opacity=0);\n" +
            "  opacity: 0;\n" +
            "  pointer-events: none;\n" +
            "}\n" +
            // position & sketch
            "[data-tooltip]::before {\n" +
            "  position: absolute;\n" +
            "  bottom: 50%;\n" +
            "  left: 50%;\n" +
            "  margin-bottom: 5px;\n" +
            "  padding: 7px;\n" +
            "  -webkit-border-radius: 3px;\n" +
            "  -moz-border-radius: 3px;\n" +
            "  border-radius: 3px;\n" +
            "  background-color: #000;\n" +
            "  background-color: hsla(0, 0%, 20%, 0.9);\n" +
            "  color: #fff;\n" +
            "  content: attr(data-tooltip);\n" +
            "  text-align: center;\n" +
            "  font-size: 14px;\n" +
            "  line-height: 1.2;\n" +
            "}\n" +
            // hover show
            "[data-tooltip]:hover::before,\n" +
            "[data-tooltip]:hover::after {\n" +
            "  visibility: visible;\n" +
            "  -ms-filter: \"progid:DXImageTransform.Microsoft.Alpha(Opacity=100)\";\n" +
            "  filter: progid: DXImageTransform.Microsoft.Alpha(Opacity=100);\n" +
            "  opacity: 1;\n" +
            "}\n";
   }


   /////  TODO drawing code for semantic type asterisk
//   [data-tooltip]:after {
//      position: absolute;
//      bottom: 150%;
//      left: 50%;
//      margin-left: -5px;
//      width: 0;
//      border-top: 5px solid #000;
//      border-top: 5px solid hsla(0, 0%, 20%, 0.9);
//      border-right: 5px solid transparent;
//      border-left: 5px solid transparent;
//      content: " ";
//      font-size: 0;
//      line-height: 0;
//   }


//   static private String getAsterisk( final String className, final String color, final String xOffset, final String yOffset ) {
//      return "\n." + className + " {\n" +
//             "  position: relative;\n" +
//             "  display: inline-block " + color + ";\n" +
//             "  border-bottom: " + size + "em " + dashType + " " + color + ";\n" +
//             "}\n";
//   }


}
