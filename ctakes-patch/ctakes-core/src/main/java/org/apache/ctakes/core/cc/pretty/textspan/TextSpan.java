package org.apache.ctakes.core.cc.pretty.textspan;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 7/6/2015
 */
public interface TextSpan {

   /**
    * @return begin offset
    */
   int getBegin();

   /**
    * @return end offset
    */
   int getEnd();

   /**
    * @return width of the text span
    */
   int getWidth();

   /**
    * @param textSpan another text span
    * @return true if the text spans overlap
    */
   boolean overlaps( TextSpan textSpan );

}
