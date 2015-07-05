package com.elle.ellegui;

import com.elle.ellegui.presentation.filter.ITableFilter;
import com.elle.ellegui.presentation.filter.TableRowFilterSupport;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CreateTables extends JPanel {

    public JTable table;
    public JTable table_2;
    public JScrollPane scrollPane;
    public JScrollPane scrollPane_2;
    public TableRowSorter sorter;

    public int getTableNum() {
        return tableNum;
    }

    private int tableNum;
    // New variables
    private boolean isFiltering = false;
    private boolean tableStatus = false;    // if original createTable is set, then true
    private JTable originalTable;
    private JTable filteredTable;
    private String tableName;               // i.e. trades
    private String tableCommand;            // i.e. ShowDV_Trades
    private JTableHeader header;

    //    // For excel fiter
//    private static int mColumnIndex = -1;
//    private static ITableFilter<?> filter;
//    private static CheckList<DistinctColumnItem> filterList = new CheckList.Builder().build();
    public CreateTables(int num) {
        super(new GridLayout(1, 0));
        table = new JTable(new RowHeaderTable(num));
        table.getColumn("#").setMaxWidth(30);
        table.getColumn("#").setMinWidth(30);
        table.getTableHeader().setReorderingAllowed(false);
    }

    public CreateTables() {
        // currently useless
        table = new JTable();
        table.setPreferredScrollableViewportSize(new Dimension(2000, 800));
        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);
        scrollPane
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(false);
        this.add(scrollPane);
    }

    public CreateTables(Connection connect) {
        super(new GridLayout(2, 0));

        TableColumnModel cm = new CreateTableColumnModel();
        TableColumnModel cm_2 = new CreateTableColumnModel();

        TableColumnModel rowHeaderModel = new CreateTableColumnModel_RowHeader();
        TableColumnModel rowHeaderModel_2 = new CreateTableColumnModel_RowHeader();

        JTable headColumn, headColumn_2;

        table = new JTable(new MyTableModel_PositionDefault(connect), cm);
        table_2 = new JTable(new MyTableModel_TradesDefault(connect), cm_2);
        headColumn = new JTable(new MyTableModel_PositionDefault(connect), rowHeaderModel);
        headColumn_2 = new JTable(new MyTableModel_TradesDefault(connect), rowHeaderModel_2);

        table.createDefaultColumnsFromModel();
        headColumn.createDefaultColumnsFromModel();
        table.setSelectionModel(headColumn.getSelectionModel());
        JViewport viewPort = new JViewport();
        viewPort.setView(headColumn);
        viewPort.setPreferredSize(headColumn.getPreferredSize());

        table_2.createDefaultColumnsFromModel();
        headColumn_2.createDefaultColumnsFromModel();
        table_2.setSelectionModel(headColumn_2.getSelectionModel());
        JViewport viewPort_2 = new JViewport();
        viewPort_2.setView(headColumn_2);
        viewPort_2.setPreferredSize(headColumn_2.getPreferredSize());

        table.setBackground(Color.WHITE);
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFillsViewportHeight(true);

        table_2.setBackground(Color.WHITE);
        table_2.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table_2.setFillsViewportHeight(true);

        TableRowFilterSupport.forTable(table).searchable(true).actions(true)
                .apply();
        TableRowFilterSupport.forTable(table_2).searchable(true).actions(true)
                .apply();

        setRowSelected(table);
        setRowSelected(table_2);
        table.getTableHeader().setReorderingAllowed(false);
        table_2.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table
                .setDefaultRenderer(String.class, centerRenderer);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table_2.setDefaultRenderer(String.class, centerRenderer);
        table_2.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        scrollPane = new JScrollPane(table);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setRowHeader(viewPort);

        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
                headColumn.getTableHeader());

        scrollPane_2 = new JScrollPane(table_2);

        scrollPane_2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane_2.setRowHeader(viewPort_2);

        scrollPane_2.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
                headColumn_2.getTableHeader());

        this.add(scrollPane);

        this.add(scrollPane_2);
    }

    public CreateTables(String str, Connection connect) {
        super(new GridLayout(1, 0));

        TableColumnModel cm = new CreateTableColumnModel();
        TableColumnModel rowHeaderModel = new CreateTableColumnModel_RowHeader();
        JTable headColumn;

        this.tableCommand = str;

        if (str.equals("ShowTextFile")) {
            table = new JTable(new MyTableModel_IB_9048_File(), cm);
            headColumn = new JTable(new MyTableModel_IB_9048_File(), rowHeaderModel);
            tableNum = 1;

        } else if (str.equals("ShowDV_Trades")) {
            table = new JTable(new MyTableModel_TradesDefault(connect), cm);
            headColumn = new JTable(new MyTableModel_TradesDefault(connect), rowHeaderModel);
            tableNum = 2;

        } else if (str.startsWith("#")) {
            table = new JTable(new MyTableModel_bySymbol(str, connect), cm);
            headColumn = new JTable(new MyTableModel_bySymbol(str, connect),
                    rowHeaderModel);
            tableNum = 3;
            // createTable.setBackground(Color.getHSBColor((float) 0.15, (float) 0.5,
            // (float) 1.0));
        } else if (str.startsWith("*")) {
            table = new JTable(new MyTableModel_byDateRange(str, connect), cm);
            headColumn = new JTable(new MyTableModel_byDateRange(str, connect),
                    rowHeaderModel);
            tableNum = 4;
            // createTable.setBackground(Color.getHSBColor((float) 0.15, (float) 0.5,
            // (float) 1.0));
        } else if (str.equals("ShowAllPositions")) {
            table = new JTable(new MyTableModel_AllPosition(connect), cm);
            headColumn = new JTable(new MyTableModel_AllPosition(connect), rowHeaderModel);
            tableNum = 5;
        } else if (str.equals("ShowAllTrades")) {
            table = new JTable(new MyTableModel_AllTrades(connect), cm);
            headColumn = new JTable(new MyTableModel_AllTrades(connect), rowHeaderModel);
            tableNum = 6;
        } else if (str.equals("ShowAllIB_8949")) {
            table = new JTable(new MyTableModel_IB_8949(connect), cm);
            headColumn = new JTable(new MyTableModel_IB_8949(connect), rowHeaderModel);
            tableNum = 11;
        } else if (str.equals("ShowAllTL_8949")) {
            table = new JTable(new MyTableModel_TL_8949(connect), cm);
            headColumn = new JTable(new MyTableModel_TL_8949(connect), rowHeaderModel);
            tableNum = 12;
        } else if (str.equals("ShowDV_IB_8949")) {
            table = new JTable(new MyTableModel_TradesView_IB_8949(connect), cm);
            headColumn = new JTable(new MyTableModel_TradesView_IB_8949(connect), rowHeaderModel);
            tableNum = 13;
        } else if (str.equals("ShowDV_TL_8949")) {
            table = new JTable(new MyTableModel_TradesView_TL_8949(connect), cm);
            headColumn = new JTable(new MyTableModel_TradesView_TL_8949(connect), rowHeaderModel);
            tableNum = 14;
        } else if (str.equals("ShowDV_Positions")) {
            table = new JTable(new MyTableModel_PositionDefault(connect), cm);
            headColumn = new JTable(new MyTableModel_PositionDefault(connect), rowHeaderModel);
            tableNum = 7;
        } else if (str.equals("ShowLoadsTable")) {
            table = new JTable(new MyTableModel_Loads(connect), cm);
            headColumn = new JTable(new MyTableModel_Loads(connect), rowHeaderModel);
            tableNum = 8;
        } else if (str.equals("Reconcile")) {
            table = new JTable(new MyTableModel_Reconcile_8949(connect), cm);
            headColumn = new JTable(new MyTableModel_Reconcile_8949(connect), rowHeaderModel);
            tableNum = 9;
        } else {
            table = new JTable(new MyTableModel_template(str, connect), cm);
            headColumn = new JTable(new MyTableModel_template(str, connect),
                    rowHeaderModel);
            tableNum = 10;
            // createTable.setBackground(Color.getHSBColor((float) 0.15, (float) 0.5,
            // (float) 1.0));
        }
        table.setBackground(Color.WHITE);
        // createTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.setPreferredScrollableViewportSize(new Dimension(500, 200));
        table.setFillsViewportHeight(true);

        setRowSelected(table);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(String.class, centerRenderer);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getTableHeader().setReorderingAllowed(false);
        switch (tableNum) {
            case 1:
                sorter = new TableRowSorter<MyTableModel_IB_9048_File>(
                        (MyTableModel_IB_9048_File) table.getModel());
                break;
            case 2:
                sorter = new TableRowSorter<MyTableModel_TradesDefault>(
                        (MyTableModel_TradesDefault) table.getModel());
                break;
            case 3:
                sorter = new TableRowSorter<MyTableModel_bySymbol>(
                        (MyTableModel_bySymbol) table.getModel());
                break;
            case 4:
                sorter = new TableRowSorter<MyTableModel_byDateRange>(
                        (MyTableModel_byDateRange) table.getModel());
                break;
            case 5:
                sorter = new TableRowSorter<MyTableModel_AllPosition>(
                        (MyTableModel_AllPosition) table.getModel());
                break;
            case 6:
                sorter = new TableRowSorter<MyTableModel_AllTrades>(
                        (MyTableModel_AllTrades) table.getModel());
                break;
            case 7:
                sorter = new TableRowSorter<MyTableModel_PositionDefault>(
                        (MyTableModel_PositionDefault) table.getModel());
                break;
            case 8:
                sorter = new TableRowSorter<MyTableModel_Loads>(
                        (MyTableModel_Loads) table.getModel());
                break;
            case 9:
                sorter = new TableRowSorter<MyTableModel_Reconcile_8949>(
                        (MyTableModel_Reconcile_8949) table.getModel());
                break;
            case 10:
                sorter = new TableRowSorter<MyTableModel_template>(
                        (MyTableModel_template) table.getModel());
                break;
            case 11:
                sorter = new TableRowSorter<MyTableModel_IB_8949>(
                        (MyTableModel_IB_8949) table.getModel());
                break;
            case 12:
                sorter = new TableRowSorter<MyTableModel_TL_8949>(
                        (MyTableModel_TL_8949) table.getModel());
                break;
            case 13:
                sorter = new TableRowSorter<MyTableModel_TradesView_IB_8949>(
                        (MyTableModel_TradesView_IB_8949) table.getModel());
                break;
            case 14:
                sorter = new TableRowSorter<MyTableModel_TradesView_TL_8949>(
                        (MyTableModel_TradesView_TL_8949) table.getModel());
                break;
        }
        table.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) { // <-- mouse click event listener here!
                if (e.getClickCount() == 2) {
                    if (isFiltering) {
                        GUI.monitorTableChange(-1);
                        int column = table.getSelectedColumn();
                        String columnName = table.getColumnName(column);
                        setMyRowFilter(e, sorter);      // <-- set row filter here!
                        table.setRowSorter(sorter);
                        int index = table.getColumnModel()
                                .getColumnIndex(columnName);
                        GUI.monitorTableChange(index);
                    } else {
                        int column = table.getSelectedColumn();
                        String columnName = table.getColumnName(column);
                        setMyRowFilter(e, sorter);      // <-- set row filter here!
                        table.setRowSorter(sorter);
                        int index = table.getColumnModel()
                                .getColumnIndex(columnName);
                        GUI.monitorTableChange(index);
                    }
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            public void clearFilter(TableRowSorter rowSorter) {
                try {
                    RowFilter rf;
                    int rowIndex = table.getSelectedRow();
                    int columnIndex = table.getColumnModel().getColumnIndex(
                            "filecode");
                    String str = (String) table.getValueAt(rowIndex,
                            columnIndex);

                    rf = RowFilter.regexFilter("^" + str);
                    rowSorter.setRowFilter(rf);

                } catch (Exception ex) {

                }
            }

            public void setMyRowFilter(MouseEvent e, TableRowSorter rowSorter) {
                RowFilter rf;
                int rowIndex = table.getSelectedRow();
                int[] columnIndex = table.getColumnModel().getSelectedColumns();//Get Column selected
                Object selectedRow = table.getValueAt(rowIndex, columnIndex[0]);
                try {
                    if (selectedRow instanceof Date) {//Filter by Date
                        Date lst = (Date) selectedRow;
                        rf = RowFilter.dateFilter(RowFilter.ComparisonType.EQUAL, lst);
                    } else if (selectedRow instanceof Integer) {//Filter by numbers
                        Integer lst = (Integer) selectedRow;
                        rf = RowFilter.numberFilter(RowFilter.ComparisonType.EQUAL, lst);
                    } else {                                                            //Filter by String
                        String[] lst = ((String) selectedRow).split(" ");
                        rf = RowFilter.regexFilter("^" + lst[0]);
                    }
                    rowSorter.setRowFilter(rf);
                } catch (Exception ex) {
                    System.out.println("error from SetMyRowFilter" + ex.getMessage());
                }
            }
        });

        header = table.getTableHeader(); // Listener in the header to do DoubleClick and clean filter
        if (header != null) {
            header.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        if (isFiltering) {
                            clearFilterDoubleClick(e, table);
                        }
                    }

                }
            });
        }

        // TableRowFilterSupport is for pop up window and filter methods
        TableRowFilterSupport.forTable(table).searchable(true).actions(true).apply();

        table.createDefaultColumnsFromModel();
        headColumn.createDefaultColumnsFromModel();
        table.setSelectionModel(headColumn.getSelectionModel());
        JViewport viewPort = new JViewport();
        viewPort.setView(headColumn);
        viewPort.setPreferredSize(headColumn.getPreferredSize());

        scrollPane = new JScrollPane(table);
        scrollPane
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setRowHeader(viewPort);
        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER,
                headColumn.getTableHeader());

        for (int i = 0; i < table.getColumnCount(); i++) {
            int stringLength = 0;
            for (int j = 0; j < table.getRowCount(); j++) {
                String temp = table.getValueAt(j, i).toString();
                if (temp.length() > stringLength) {
                    stringLength = temp.length();
                }
            }
            table.getColumnModel().getColumn(i)
                    .setMinWidth(stringLength * 7 + 40);
            table.getColumnModel().getColumn(i)
                    .setPreferredWidth(stringLength * 7 + 40);            // Set width of each columns with length of String that is inside of each columns
        }
        for (int i = 0; i <=2; i++) {                                    // Set last 2 columns to width to hide from the table view
            int stringLength = 0;
            for (int j = 0; j < table.getRowCount(); j++) {
                String temp = table.getValueAt(j, i).toString();
                if (temp.length() > stringLength) {
                    stringLength = temp.length();
                }
            }
            table.getColumnModel().getColumn((table.getColumnCount()-3)+i)
                    .setMinWidth(0);
            table.getColumnModel().getColumn((table.getColumnCount()-3)+i)
                    .setPreferredWidth(0);
        }



        this.add(scrollPane);
    }

    private void clearFilterDoubleClick(MouseEvent e, JTable table) {
        TableRowFilterSupport.forTable(table).searchable(true).actions(true)
                .apply();
        GUI.monitorTableChange(-1);// clean green background

    }

    public void setRowSelected(JTable table) {
        table.setRowSelectionAllowed(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Table methods for filtering
     *
     * @param rowSorter
     * @param symbol
     */
    public void setSymbolFilter(TableRowSorter rowSorter, String symbol) {  // <-- filter codes for buttons
        try {
            RowFilter rf;

            rf = RowFilter.regexFilter("^" + symbol.toUpperCase());
            rowSorter.setRowFilter(rf);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not find " + symbol.toUpperCase() + ".");
        }
    }

    public void setDateFilter(TableRowSorter rowSorter,
                              String date1, String date2) {

        LocalDate StartDate = LocalDate.parse(date1);
        LocalDate EndDate = LocalDate.parse(date2);

        if (StartDate.isAfter(EndDate)) {
            JOptionPane.showMessageDialog(null, "The end date is before the start date.",
                    "Date Error", JOptionPane.ERROR_MESSAGE);
        } else {
            List<String> totalDates = new ArrayList<>();
            while (!StartDate.isAfter(EndDate)) {   // loop the dates between start and end dates
                totalDates.add(StartDate.toString());
                StartDate = StartDate.plusDays(1);
            }
            String[] lst = totalDates.toArray(new String[totalDates.size()]);

            try {
                List<RowFilter<Object, Object>> filters
                        = new ArrayList<>();
                RowFilter<Object, Object> rf;
                int i;

                for (i = 0; i < lst.length; i++) {
                    filters.add(RowFilter.regexFilter("^" + lst[i]));
                }
                rf = RowFilter.orFilter(filters);
                rowSorter.setRowFilter(rf);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Could not find records between these two dates");
            }
        }
    }

//    public void clearFilter(TableRowSorter rowSorter) {
//        try {
//            RowFilter rf;
//
//            String str = "40627N";  // <- generalize this
//
//            rf = RowFilter.regexFilter("^" + str);
//            rowSorter.setRowFilter(rf);
//
//        } catch (Exception ex) {
//
//        }
//    }

    public void setFilteringStatus(boolean a) {
        isFiltering = a;
    }

    public JTable getTable() {
        return originalTable;
    }

    public void setTable(JTable a) {
        originalTable = a;
        tableStatus = true;
    }

    public JTable getFilteredTable() {
        return filteredTable;
    }

    public void setFilteredTable(JTable a) {
        filteredTable = a;
        isFiltering = true;
    }

    public boolean getFilterStatus() {
        return isFiltering;
    }

    public boolean getTableStatus() {
        return tableStatus;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String a) {
        tableName = a;
    }

    public String getTableCommand() {
        return tableCommand;
    }

    public void setTableCommand(String a) {
        tableCommand = a;
    }

    public String getTable(String tableName) {
        switch (tableName) {
            case "ShowDV_Positions":
                return "positions";
            case "ShowDV_Trades":
                return "trades";
            default:
                return "trades"; // <-- need to be updated to match all tables in database
        }
    }

    public String getDateColumn(String tableName) {
        switch (tableName) {
            case "ShowDV_Positions":
                return "Lot_Time";
            case "ShowDV_Trades":
                return "Trade_Time";
            default:
                return "Trade_Time"; // <-- need to be updated to match all tables in database
        }
    }

//    public void applyDistinctColumnItems(int col, Object selectField) { //Create Collection from selected fields
//        Collection<DistinctColumnItem> item = new ArrayList<>();
//        DistinctColumnItem distinctColumnItem = new DistinctColumnItem(selectField, col);
//        item.add(distinctColumnItem);
////        return apply(col, item);
//    }

    private class RowHeaderTable extends AbstractTableModel {

        private String[] columnNames = new String[1];
        private Object[][] data;

        public RowHeaderTable(int num) {
            columnNames[0] = "#";
            data = new Object[num][1];
            for (int i = 1; i <= num; i++) {
                data[i - 1][0] = i;
            }
        }

        @Override
        public int getColumnCount() {
            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }
    }

    private class MyTableModel_IB_9048_File extends AbstractTableModel {

        private String[] columnNames;
        private Object[][] data;

        public MyTableModel_IB_9048_File() {
            String textFile = "D:/MySQL File/IB 9048/IB 9048 AS 2013-06H.csv";
            BufferedReader br;
            String line;
            String splitSign = ",";
            int i = 0;
            try {
                br = new BufferedReader(new FileReader(textFile));
                while (br.readLine() != null) {
                    i++;
                }
                br.close();
                data = new Object[i - 1][];
                i = 0;
                br = new BufferedReader(new FileReader(textFile));
                line = br.readLine();
                columnNames = line.split(splitSign);
                String[] rowNumber = {"#"};
                System.arraycopy(rowNumber, 0, columnNames, 0, 1);
                line = br.readLine();
                while (line != null) {
                    data[i] = new Object[line.split(splitSign).length + 1];
                    data[i][0] = i + 1;
                    for (int j = 1; j < data[i].length; j++) {
                        data[i][j] = line.split(splitSign)[j - 1];
                    }
                    i++;
                    line = br.readLine();
                }
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(table, ex.getMessage());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(table, ex.getMessage());
            }
        }

        @Override
        public int getColumnCount() {
            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        @Override
        public Class getColumnClass(int c) {
            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }
    }

    public class MyTableModel_TradesDefault extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;
        public int column_index_date;


        private boolean checkBoxDateRange;
        private boolean checkBoxSymbol;
        private String selectedSymbol;
        private String dateInit;
        private String dateEnd;
        private  ITableFilter filterBySymbol;
        private  ITableFilter filterByDate;


        /**
         * Setter and Getter for ALL values to use in filter main windows.
         * @return
         */

        public boolean isCheckBoxDateRange() {
            return checkBoxDateRange;
        }

        public void setCheckBoxDateRange(boolean checkBoxDateRange) {
            this.checkBoxDateRange = checkBoxDateRange;
        }

        public boolean isCheckBoxSymbol() {
            return checkBoxSymbol;
        }

        public void setCheckBoxSymbol(boolean checkBoxSymbol) {
            this.checkBoxSymbol = checkBoxSymbol;
        }

        public String getDateEnd() {
            return dateEnd;
        }

        public void setDateEnd(String dateEnd) {
            this.dateEnd = dateEnd;
        }

        public String getDateInit() {
            return dateInit;
        }

        public void setDateInit(String dateInit) {
            this.dateInit = dateInit;
        }

        public ITableFilter getFilterByDate() {
            return filterByDate;
        }

        public void setFilterByDate(ITableFilter filterByDate) {
            this.filterByDate = filterByDate;
        }

        public String getSelectedSymbol() {
            return selectedSymbol;
        }

        public void setSelectedSymbol(String selectedSymbol) {
            this.selectedSymbol = selectedSymbol;
        }


        public ITableFilter getFilterBySymbol() {
            return filterBySymbol;
        }

        public void setFilterBySymbol(ITableFilter filterBySymbol) {
            this.filterBySymbol = filterBySymbol;
        }


        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_TradesDefault(Connection connect) {
            ResultSet rs;
            String sql1, sql2, sql3;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE trades SET Expiry = NULL WHERE Expiry = '0000-00-00';";
            sql3 = "SELECT * FROM " + TradesView.setDefaultView();
            SimpleDateFormat df_short = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat df_complete = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.addBatch(sql2);
                state.executeBatch();
                rs = state.executeQuery(sql3);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 3];     //Add 2 more column for date-only which the are going to be use in filter popup construction
                rs.close();
                rs = state.executeQuery(sql3);
                columnNames = new String[columnNum + 3];
                columnNames[0] = "#";
                columnNames[columnNum + 1] = "Trade_Date";
                columnNames[columnNum + 2] = "Lot_Date";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;

                setDateColumns(rs, columnNum, row, df_short, df_complete);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
            }
        }

        /**
         * Set format yyyy-MM-dd HH:mm:ss date to Time columns and yyyy-MM-dd for auxiliary columns to be use in filter creation.
         *
         * @param rs
         * @param columnNum
         * @param row
         * @param df_short
         * @throws SQLException
         */
        private void setDateColumns(ResultSet rs, int columnNum, int row, SimpleDateFormat df_short, SimpleDateFormat df_complete) throws SQLException {
            String title;
            while (rs.next()) {
                data[row][0] = row + 1;
                for (int column = 1; column <= columnNum; column++) {
                    title = columnNames[column];
                    if (title.equals("Trade_Time")) {      // create Trade_Tine date column
                        try {
                            Date value = rs.getTimestamp(column);
                            data[row][column] = df_complete.format(rs.getTimestamp(column));
                            data[row][columnNum + 1] = df_short.format(value);
                            column_index_date=column;

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } else if (title.equals("Lot_Time")) {      // create Trade_Tine date column
                        try {
                            Date value = rs.getDate(column);
                            if (value != null) {
                                data[row][column] = df_complete.format(rs.getTimestamp(column));
                                data[row][columnNum + 2] = df_short.format(value);

                            } else {
                                data[row][column] = "NULL";
                                data[row][columnNum + 2] = "NULL";
                            }

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }

                    } else if (rs.getString(column) != null) {
                        data[row][column] = rs.getString(column);

                    } else {
                        data[row][column] = "NULL";
                    }
                }

                row++;
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }


    }

    public class MyTableModel_bySymbol extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_bySymbol(String str, Connection connect) {

            ResultSet rs;
            int columnNum;
            int rowNum = 0;
            String[] tempStr = str.split("#");
            String currentTable = getTable(tempStr[2]), symbol = tempStr[1];

            try {
                state = connect.createStatement();
                rs = state.executeQuery("SELECT * FROM " + currentTable
                        + " WHERE Symbol = " + "'" + symbol + "'");
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery("SELECT * FROM " + currentTable
                        + " WHERE Symbol = " + "'" + symbol + "'");
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
                System.out.println(this.getValueAt(0, c).getClass());
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_byDateRange extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_byDateRange(String str, Connection connect) {

            ResultSet rs;
            int columnNum;
            int rowNum = 0;
            String[] date = str.split("\\*"); // * has special meaning in regular expressions. escape it by adding \\
            String currentTable = getTable(date[3]);
            String dateColumnTitle = getDateColumn(date[3]);

            try {
                state = connect.createStatement();
                rs = state.executeQuery("SELECT * FROM " + currentTable
                        + " WHERE " + dateColumnTitle + " BETWEEN " + "'"
                        + date[1] + "'" + " AND " + "'" + date[2]
                        + "'");
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery("SELECT * FROM " + currentTable
                        + " WHERE " + dateColumnTitle + " BETWEEN " + "'"
                        + date[0] + "'" + " AND " + "'" + date[2]
                        + "'");
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
//                JOptionPane.showMessageDialog(createTable, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
                JOptionPane.showMessageDialog(table, "Records not found.");
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_IB_8949 extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_IB_8949(Connection connect) {

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE IB_8949 SET Expiry = NULL WHERE Expiry = '0000-00-00';";
//			sql3 = "UPDATE IB_8949 SET Lot_Time = NULL WHERE Lot_Time = '0000-00-00 00:00:00';";
//			sql4 = "UPDATE IB_8949 SET OCE_Time = NULL WHERE OCE_Time = '0000-00-00 00:00:00';";
            sql5 = "SELECT * FROM IB_8949";
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.addBatch(sql2);
//				state.addBatch(sql3);
//				state.addBatch(sql4);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_TL_8949 extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        public MyTableModel_TL_8949(Connection connect) {

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
//			sql2 = "UPDATE TL_8949 SET Expiry = NULL WHERE Expiry = '0000-00-00';";
//			sql3 = "UPDATE TL_8949 SET Lot_Time = NULL WHERE Lot_Time = '0000-00-00 00:00:00';";
//			sql4 = "UPDATE TL_8949 SET OCE_Time = NULL WHERE OCE_Time = '0000-00-00 00:00:00';";
            sql5 = "SELECT * FROM TL_8949";
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
//				state.addBatch(sql2);
//				state.addBatch(sql3);
//				state.addBatch(sql4);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_AllPosition extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        public MyTableModel_AllPosition(Connection connect) {  // Position TableModel

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE positions SET Expiry = NULL WHERE Expiry = '0000-00-00';";
            sql3 = "UPDATE positions SET Lot_Time = NULL WHERE Lot_Time = '0000-00-00 00:00:00';";
            sql4 = "UPDATE positions SET OCE_Time = NULL WHERE OCE_Time = '0000-00-00 00:00:00';";
            sql5 = "SELECT * FROM positions";
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.addBatch(sql2);
                state.addBatch(sql3);
                state.addBatch(sql4);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_AllTrades extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_AllTrades(Connection connect) {  //TableModel for Trades

            ResultSet rs;
            String sql1, sql2, sql3;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE trades SET Expiry = NULL WHERE Expiry = '0000-00-00';";
            sql3 = "SELECT * FROM trades";
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.addBatch(sql2);
                state.executeBatch();
                rs = state.executeQuery(sql3);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql3);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_PositionDefault extends AbstractTableModel {    // <-- positions

        private Statement state;
        private String[] columnNames;
        private Object[][] data;
        private boolean checkBoxDateRange;
        private boolean checkBoxSymbol;
        private String selectedSymbol;
        private String dateInit;
        private String dateEnd;
        private  ITableFilter filterBySymbol;
        private  ITableFilter filterByDate;

        public boolean isCheckBoxDateRange() {
            return checkBoxDateRange;
        }

        public void setCheckBoxDateRange(boolean checkBoxDateRange) {
            this.checkBoxDateRange = checkBoxDateRange;
        }

        public boolean isCheckBoxSymbol() {
            return checkBoxSymbol;
        }

        public void setCheckBoxSymbol(boolean checkBoxSymbol) {
            this.checkBoxSymbol = checkBoxSymbol;
        }

        public String getDateInit() {
            return dateInit;
        }

        public void setDateInit(String dateInit) {
            this.dateInit = dateInit;
        }

        public String getDateEnd() {
            return dateEnd;
        }

        public void setDateEnd(String dateEnd) {
            this.dateEnd = dateEnd;
        }

        public ITableFilter getFilterByDate() {
            return filterByDate;
        }

        public void setFilterByDate(ITableFilter filterByDate) {
            this.filterByDate = filterByDate;
        }

        public ITableFilter getFilterBySymbol() {
            return filterBySymbol;
        }

        public void setFilterBySymbol(ITableFilter filterBySymbol) {
            this.filterBySymbol = filterBySymbol;
        }

        public String getSelectedSymbol() {
            return selectedSymbol;
        }

        public void setSelectedSymbol(String selectedSymbol) {
            this.selectedSymbol = selectedSymbol;
        }

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         *
         * @param connect
         */
        public MyTableModel_PositionDefault(Connection connect) {  //TableModel for Position table which shows in GUI

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE positions SET Expiry = NULL WHERE Expiry = '0000-00-00';";
            sql3 = "UPDATE positions SET Lot_Time = NULL WHERE Lot_Time = '0000-00-00 00:00:00';";
            sql4 = "UPDATE positions SET OCE_Time = NULL WHERE OCE_Time = '0000-00-00 00:00:00';";
            sql5 = "SELECT * FROM " + TradesView.setDefViewOfPositions();
            try {
                state = connect.createStatement();  // make sure connect is built successfully
                state.addBatch(sql1);
                state.addBatch(sql2);
                state.addBatch(sql3);
                state.addBatch(sql4);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 3];   // add 2 columns for date-only columns
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 3];    // add 2 columns for date-only columns
                columnNames[0] = "#";
                columnNames[columnNum + 1] = "Lot_Date";
                columnNames[columnNum + 2] = "OCE_Date";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }

                int row = 0;

                SimpleDateFormat df_short = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat df_complete = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
                setDateColumns(rs, columnNum, row, df_short, df_complete);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (NumberFormatException | HeadlessException e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
                GUI.status = false;
            }
        }

        private void setDateColumns(ResultSet rs, int columnNum, int row, SimpleDateFormat df_short, SimpleDateFormat df_complete) throws SQLException {
            String title;
            while (rs.next()) {
                data[row][0] = row + 1;
                for (int column = 1; column <= columnNum; column++) {
                    title = columnNames[column];
                    if (rs.getString(column) == null) {
                        data[row][column] = "NULL";
                    } else if (title.equals("inputLine")) {     // Turn inputLine contents into integer
                        data[row][column] = Integer.parseInt(rs.getString(column));
                    } else if (title.equals("Lot_Time")) {      // create Lot_Time date column
                        try {
                            Date value = rs.getTimestamp(column);
                            data[row][column] = df_complete.format(value);
                            data[row][columnNum + 1] = df_short.format(value);

                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } else if (title.equals("OCE_Time")) {     // create OCE_Time date column with only date yyyy-mm-dd
                        try {
                            Date value = rs.getDate(column);
                            data[row][column] = df_complete.format(value);
                            data[row][columnNum + 2] = df_short.format(value);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, e.getMessage());
                        }
                    } else {
                        data[row][column] = rs.getString(column);
                    }
                }
                row++;
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_TradesView_IB_8949 extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        public MyTableModel_TradesView_IB_8949(Connection connect) {

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql2 = "UPDATE IB_8949 SET Expiry = NULL WHERE Expiry = '0000-00-00';";
            sql5 = "SELECT * FROM " + TradesView.setDefViewOfIB_8949();
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.addBatch(sql2);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {
            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_TradesView_TL_8949 extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        public MyTableModel_TradesView_TL_8949(Connection connect) {

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SET SQL_SAFE_UPDATES = 0;";
            sql5 = "SELECT * FROM " + TradesView.setDefViewOfTL_8949();
            try {
                state = connect.createStatement();
                state.addBatch(sql1);
                state.executeBatch();
                rs = state.executeQuery(sql5);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql5);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_Loads extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_Loads(Connection connect) {

            ResultSet rs;
            String sql1, sql2, sql3, sql4, sql5;
            int columnNum;
            int rowNum = 0;
            sql1 = "SELECT * FROM LOADS";
            try {
                state = connect.createStatement();
                rs = state.executeQuery(sql1);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql1);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_Reconcile_8949 extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_Reconcile_8949(Connection connect) {

            ResultSet rs;
            String sql1;
            int columnNum;
            int rowNum = 0;
            sql1 = (new Reconcile_8949()).query;
            try {
                state = connect.createStatement();
                rs = state.executeQuery(sql1);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(sql1);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

    public class MyTableModel_template extends AbstractTableModel {

        private Statement state;
        private String[] columnNames;
        private Object[][] data;

        /**
         * constructor of this class in order to query the data in database, we
         * have to register the JDBC driver, then connect to the database
         */
        public MyTableModel_template(String str, Connection connect) {

            ResultSet rs;
            int columnNum;
            int rowNum = 0;

            try {
                state = connect.createStatement();
                rs = state.executeQuery(str);
                columnNum = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    rowNum++;
                }
                data = new Object[rowNum][columnNum + 1];
                rs.close();
                rs = state.executeQuery(str);
                columnNames = new String[columnNum + 1];
                columnNames[0] = "#";
                for (int column = 1; column <= columnNum; column++) {
                    columnNames[column] = rs.getMetaData()
                            .getColumnName(column);
                }
                int row = 0;
                while (rs.next()) {
                    data[row][0] = row + 1;
                    for (int column = 1; column <= columnNum; column++) {
                        if (rs.getString(column) == null) {
                            data[row][column] = "NULL";
                        } else {
                            data[row][column] = rs.getString(column);
                        }
                    }
                    row++;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(table, e.getMessage());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(table, "You have not yet logged in");
//                JOptionPane.showMessageDialog(createTable, e.getMessage());
            }
        }

        @Override
        public int getColumnCount() {

            if (columnNames == null) {
                return 0;
            }
            return columnNames.length;
        }

        @Override
        public int getRowCount() {

            if (data == null) {
                return 0;
            }
            return data.length;
        }

        @Override
        public String getColumnName(int col) {

            if (columnNames == null) {
                return null;
            }
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {

            try {
                Object ob = data[row][col];
            } catch (NullPointerException e) {
                return null;
            }
            return data[row][col];
        }

        /**
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        @Override
        public Class getColumnClass(int c) {

            try {
                this.getValueAt(0, c).getClass();
            } catch (NullPointerException e) {
                return null;
            }
            return this.getValueAt(0, c).getClass();
        }

        /**
         * don't need to implement this method unless your createTable's
         * editable.
         */
        @Override
        public boolean isCellEditable(int row, int col) {

            // note that the data/cell address is constant,
            // no matter where the cell appears on screen.
            return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {

            data[row][col] = value;
            fireTableDataChanged();
        }

    }

}
