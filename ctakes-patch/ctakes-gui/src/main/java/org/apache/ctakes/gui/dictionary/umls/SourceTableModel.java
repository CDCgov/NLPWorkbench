package org.apache.ctakes.gui.dictionary.umls;


import org.apache.log4j.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.*;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/10/2015
 */
final public class SourceTableModel implements TableModel {

   static private final Logger LOGGER = Logger.getLogger( "SourceTableModel" );

   static private final String[] COLUMN_NAMES = { "Source", "Target", "Vocabulary" };
   static private final Class<?>[] COLUMN_CLASSES = { Boolean.class, Boolean.class, String.class };

   static private final String[] CTAKES_SOURCES = { "SNOMEDCT_US", "RXNORM" };

   private final EventListenerList _listenerList = new EventListenerList();
   private final Collection<String> _wantedSources = new HashSet<>();
   private final Collection<String> _wantedTargets = new HashSet<>();
   private final List<String> _sources = new ArrayList<>();


   public void setSources( final Collection<String> sources ) {
      _sources.clear();
      _wantedSources.clear();
      _wantedTargets.clear();
      _sources.addAll( sources );
      Collections.sort( _sources );
      _wantedSources.addAll( Arrays.asList( CTAKES_SOURCES ) );
      _wantedTargets.addAll( Arrays.asList( CTAKES_SOURCES ) );
      fireTableChanged( new TableModelEvent( this ) );
   }

   public Collection<String> getWantedSources() {
      return _wantedSources;
   }

   public Collection<String> getWantedTargets() {
      return _wantedTargets;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getRowCount() {
      return _sources.size();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getColumnCount() {
      return 3;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getColumnName( final int columnIndex ) {
      return COLUMN_NAMES[ columnIndex ];
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Class<?> getColumnClass( final int columnIndex ) {
      return COLUMN_CLASSES[ columnIndex ];
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isCellEditable( final int rowIndex, final int columnIndex ) {
      return columnIndex == 0 || (columnIndex == 1 && (Boolean)getValueAt( rowIndex, 0 ));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getValueAt( final int rowIndex, final int columnIndex ) {
      final String source = _sources.get( rowIndex );
      switch ( columnIndex ) {
         case 0:
            return isSourceEnabled( source );
         case 1:
            return isTargetEnabled( source );
         case 2:
            return source;
      }
      return "ERROR";
   }

   private boolean isSourceEnabled( final String source ) {
      return _wantedSources.contains( source );
   }

   private boolean isTargetEnabled( final String source ) {
      return _wantedTargets.contains( source );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      if ( aValue instanceof Boolean ) {
         final String source = _sources.get( rowIndex );
         if ( columnIndex == 0 ) {
            selectWantedSource( source, (Boolean)aValue );
         } else if ( columnIndex == 1 ) {
            selectWantedTarget( source, (Boolean)aValue );
         }
      }
   }

   private void selectWantedSource( final String source, final boolean select ) {
      if ( select ) {
         _wantedSources.add( source );
      } else {
         _wantedSources.remove( source );
      }
   }

   private void selectWantedTarget( final String target, final boolean select ) {
      if ( select ) {
         _wantedTargets.add( target );
      } else {
         _wantedTargets.remove( target );
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addTableModelListener( final TableModelListener listener ) {
      _listenerList.add( TableModelListener.class, listener );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeTableModelListener( final TableModelListener listener ) {
      _listenerList.remove( TableModelListener.class, listener );
   }

   /**
    * Forwards the given notification event to all
    * <code>TableModelListeners</code> that registered
    * themselves as listeners for this table model.
    *
    * @param e the event to be forwarded
    * @see #addTableModelListener
    * @see TableModelEvent
    * @see EventListenerList
    */
   private void fireTableChanged( TableModelEvent e ) {
      // Guaranteed to return a non-null array
      Object[] listeners = _listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for ( int i = listeners.length - 2; i >= 0; i -= 2 ) {
         if ( listeners[ i ] == TableModelListener.class ) {
            ((TableModelListener)listeners[ i + 1 ]).tableChanged( e );
         }
      }
   }


}
