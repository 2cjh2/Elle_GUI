package com.elle.ellegui.presentation.filter;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;

public final class TableRowFilterSupport {

    private boolean searchable = false;
    private IListFilter searchFilter = CheckListFilterType.CONTAINS;
    private IObjectToStringTranslator translator;
    private final ITableFilter<?> filter;
    private boolean actionsVisible = true;
    private int filterIconPlacement = SwingConstants.LEADING;
    private boolean useTableRenderers = false;
    private TableRowFilterSupport( ITableFilter<?> filter ) {
        if ( filter == null ) throw new NullPointerException();
        this.filter = filter;
    }

    public static TableRowFilterSupport forTable( JTable table ) {
        return new TableRowFilterSupport(new JTableFilter(table));
    }

    public static TableRowFilterSupport forFilter( ITableFilter<?> filter ) {
        return new TableRowFilterSupport(filter);
    }

    /**
     * Additional actions visible in column filter list
     * @param visible
     * @return
     */
    public TableRowFilterSupport actions( boolean visible ) {
        this.actionsVisible = visible;
        return this;
    }

    /**
     * Set the placement of the filter icon with respect to the existing icon
     * in the column label.
     *
     * @param filterIconPlacement either SwingConstants.LEADING or
     *         SwingConstants.TRAILING.
     * @return this
     */
    public TableRowFilterSupport filterIconPlacement(int filterIconPlacement) {
        if (filterIconPlacement != SwingConstants.LEADING &&
                filterIconPlacement != SwingConstants.TRAILING) {
            throw new UnsupportedOperationException("The filter icon " +         
                    "placement can only take the values of " +                   
                    "SwingConstants.LEADING or SwingConstants.TRAILING");
        }
        this.filterIconPlacement = filterIconPlacement;
        return this;
    }

    /**
     * Comlumn filter list is searchable
     * @param searchable
     * @return
     */
    public TableRowFilterSupport searchable( boolean searchable ) {
        this.searchable = searchable;
        return this;
    }

    public TableRowFilterSupport searchFilter(IListFilter searchFilter) {
        this.searchFilter = searchFilter;
        return this;
    }

    public TableRowFilterSupport searchTransalator( IObjectToStringTranslator translator ) {
        this.translator = translator;
        return this;
    }

    public TableRowFilterSupport useTableRenderers( boolean value ) {
        this.useTableRenderers = value;
        return this;
    }

    public JTable apply() {

        final TableFilterColumnPopup filterPopup = new TableFilterColumnPopup(filter);
        filterPopup.setEnabled(true);
        filterPopup.setActionsVisible(actionsVisible);
        filterPopup.setSearchable(searchable);
        filterPopup.setSearchFilter(searchFilter);
        filterPopup.setSearchTranslator(translator);
        filterPopup.setUseTableRenderers( useTableRenderers );

        setupTableHeader();
        
        return filter.getTable();
    }
      public ITableFilter<?> applyFilter() {

        final TableFilterColumnPopup filterPopup = new TableFilterColumnPopup(filter);
        filterPopup.setEnabled(true);
        filterPopup.setActionsVisible(actionsVisible);
        filterPopup.setSearchable(searchable);
        filterPopup.setSearchFilter(searchFilter);
        filterPopup.setSearchTranslator(translator);
        filterPopup.setUseTableRenderers( useTableRenderers );

        setupTableHeader();
        return filter;
    }
    

    private void setupTableHeader() {

        final JTable table = filter.getTable();
        

        filter.addChangeListener(new IFilterChangeListener() {

            @Override
            public void filterChanged(ITableFilter<?> filter) {
                table.getTableHeader().repaint();
                table.getModel().getRowCount();
            }
        });
        
          setupHeaderRenderers(table.getModel(), true );
        // make sure that search component is reset after table model changes
    }

    private void setupHeaderRenderers( TableModel newModel, boolean fullSetup ) {

        JTable table =  filter.getTable();
        

        FilterTableHeaderRenderer headerRenderer =
                new FilterTableHeaderRenderer(filter, filterIconPlacement);
        filter.modelChanged( newModel );

        for( TableColumn c:  Collections.list( table.getColumnModel().getColumns()) ) {
            c.setHeaderRenderer((TableCellRenderer) headerRenderer);
        }

        if ( !fullSetup ) return;

        table.addPropertyChangeListener("model", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {
                setupHeaderRenderers( (TableModel) e.getNewValue(), false );
            }
            
        });

        

    }


}