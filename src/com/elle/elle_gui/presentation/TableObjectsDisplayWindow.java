package com.elle.elle_gui.presentation;

import com.elle.elle_gui.database.DataManager;
import com.elle.elle_gui.database.DataManagerFactory;
import com.elle.elle_gui.entities.TableObjects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author Wei, class for loading TableObjects into a table.
 * modified 8/15/16 by Corinne to use a dataManager instead of TableObjectsDAO
 */
public class TableObjectsDisplayWindow extends javax.swing.JFrame {
    /**
     * Creates new form for load table objects 
     */
    
    private ArrayList<TableObjects> accounts;
    private final DataManager dataManager;
    private Set<Integer> updatedRows;

    
    public TableObjectsDisplayWindow() {
        dataManager = DataManagerFactory.getDataManager();
        accounts = dataManager.getAllTableObjects();
        updatedRows = new HashSet(); //initialize the updaterows

        initComponents();

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        //Vector data = model.getDataVector();
        //System.out.println(data);
        
        for(TableObjects acct : accounts) {
            Vector rowData = new Vector(6);
            rowData.add(acct.getId());
            rowData.add(acct.getName());
            rowData.add(acct.getType());
            rowData.add(acct.getUsage());
            rowData.add(acct.getDescription());
            rowData.add(acct.getAuthor());
            model.addRow(rowData);
        }       
        
        TableColumn column = null;
        
        for (int i = 0; i < 6; i++) {
            column = jTable1.getColumnModel().getColumn(i);
            
            switch (i) {
                case 0:
                    column.setPreferredWidth(40);
                    break;
                case 1:
                    column.setPreferredWidth(160);
                    break;
                case 4:
                    column.setPreferredWidth(500);
                    break;
                default:
                    column.setPreferredWidth(90);
                    break;
            }
        }  
        
        jTable1.setPreferredScrollableViewportSize(jTable1.getPreferredSize());
        jTable1.setFillsViewportHeight(true);
        
        jTable1.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE)
                    updatedRows.add(e.getFirstRow());
                System.out.println("Row " + e.getFirstRow() + " is updated.");
            }

        });
        pack();
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        SaveButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "id", "name", "type", "usage", "description", "author"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        SaveButton.setText("Save");
        SaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 871, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(398, 398, 398)
                        .addComponent(SaveButton)))
                .addGap(6, 6, 6))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(SaveButton))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveButtonActionPerformed

        boolean changed = false;
        
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        for(Integer row: updatedRows) {
            String updatedValue;
            if ((String)model.getValueAt(row, 4) == null){
                updatedValue = "";
            } else {
                updatedValue = (String)model.getValueAt(row, 4);

            };
            String originalValue;
            if (accounts.get(row).getDescription() == null){
                originalValue = "";
            } else {
                originalValue = accounts.get(row).getDescription();
            }

            if(!originalValue.equals(updatedValue)) {
                accounts.get(row).setDescription(updatedValue);
                changed = dataManager.updateTableObjects(accounts.get(row));
            }
        }
        
        
        
        if (changed) {
            updatedRows = new HashSet();;
        }
    }//GEN-LAST:event_SaveButtonActionPerformed

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SaveButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
