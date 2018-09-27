package org.apache.ctakes.core.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Dot Logger Usable in try as resource blocks
 *
 * @author SPF , chip-nlp
 * @version %I%
 * @since 3/18/2016
 */
final public class DotLogger implements Closeable {

   static private final org.apache.log4j.Logger DOT_LOGGER = org.apache.log4j.Logger.getLogger( "ProgressAppender" );
   static private final org.apache.log4j.Logger EOL_LOGGER = org.apache.log4j.Logger.getLogger( "ProgressDone" );

   private final Timer _timer;

   /**
    * Starts the Dot Logging
    */
   public DotLogger() {
      _timer = new Timer();
      _timer.scheduleAtFixedRate( new DotPlotter(), 333, 333 );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close() throws IOException {
      _timer.cancel();
      EOL_LOGGER.error( "" );
   }

   static private class DotPlotter extends TimerTask {
      private int _count = 0;

      @Override
      public void run() {
         DOT_LOGGER.info( "." );
         _count++;
         if ( _count % 30 == 0 ) {
            EOL_LOGGER.info( " " + (_count / 3) );
         }
      }
   }

}
