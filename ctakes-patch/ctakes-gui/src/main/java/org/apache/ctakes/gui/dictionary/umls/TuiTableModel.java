package org.apache.ctakes.gui.dictionary.umls;

import org.apache.log4j.Logger;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import static org.apache.ctakes.gui.dictionary.umls.Tui.*;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 12/10/2015
 */
final public class TuiTableModel implements TableModel {

   static private final Logger LOGGER = Logger.getLogger( "TuiTableModel" );

   static public final Tui[] CTAKES_ANAT = { T021, T022, T023, T024, T025, T026, T029, T030 };
   static private final Tui[] CTAKES_DISO = { T019, T020, T037, T047, T048, T049, T050, T190, T191 };
   static private final Tui[] CTAKES_FIND = { T033, T034, T040, T041, T042, T043, T044, T045, T046, T056, T057, T184 };
   static private final Tui[] CTAKES_PROC = { T059, T060, T061 };
   static public final Tui[] CTAKES_DRUG = { T109, T110, T114, T115, T116, T118, T119, T121, T122, T123, T124,
                                             T125, T126, T127, T129, T130, T131, T195, T196, T197, T200, T203 };

   static private final String[] COLUMN_NAMES = { "Use", "TUI", "Semantic Type" };
   static private final Class<?>[] COLUMN_CLASSES = { Boolean.class, String.class, String.class };

   private final EventListenerList _listenerList = new EventListenerList();
   private final Collection<Tui> _wantedTuis = EnumSet.noneOf( Tui.class );

   public TuiTableModel() {
      _wantedTuis.addAll( Arrays.asList( CTAKES_ANAT ) );
      _wantedTuis.addAll( Arrays.asList( CTAKES_DISO ) );
      _wantedTuis.addAll( Arrays.asList( CTAKES_FIND ) );
      _wantedTuis.addAll( Arrays.asList( CTAKES_PROC ) );
      _wantedTuis.addAll( Arrays.asList( CTAKES_DRUG ) );
   }

   public Collection<Tui> getWantedTuis() {
      return _wantedTuis;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getRowCount() {
      return Tui.values().length;
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
      return columnIndex == 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getValueAt( final int rowIndex, final int columnIndex ) {
      final Tui tui = Tui.values()[ rowIndex ];
      switch ( columnIndex ) {
         case 0:
            return isTuiEnabled( tui );
         case 1:
            return tui.name();
         case 2:
            return tui.getDescription();
      }
      return "ERROR";
   }

   private boolean isTuiEnabled( final Tui tui ) {
      return _wantedTuis.contains( tui );
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setValueAt( final Object aValue, final int rowIndex, final int columnIndex ) {
      if ( aValue instanceof Boolean && columnIndex == 0 ) {
         final Tui tui = Tui.values()[ rowIndex ];
         if ( (Boolean)aValue ) {
            _wantedTuis.add( tui );
         } else {
            _wantedTuis.remove( tui );
         }
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


}
