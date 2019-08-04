package admin_ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import model.Item;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class DeletedSalesView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable deletedSalesTable;
    private static MySqlAccess mAcess;


    public DeletedSalesView(String serverIp) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        setTitle("Deleted Sales");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        //setLocationRelativeTo(null);
        setLocation(new Point(300,30));
        populateDeletedSalesTable();

    }

    private void populateDeletedSalesTable() {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getDeletedSales();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
//        TextFilterator refundFilterator = new TextFilterator() {
//            @Override
//            public void getFilterStrings(List baselist, Object o) {
//                Refund refund = (Refund) o;
//                baselist.add(refund.getId());
//                baselist.add(refund.getAmount());
//                baselist.add(refund.getComment());
//                baselist.add(refund.getDatePaid());
//            }
//        };

        String[] deletedSalesTableColumnNames = {
                "No.","Product","Quantity","Price", "Total","margin","InvoiceId","Date Sold","Deleted by","Date Deleted"};

        String [] deletedSalesPropertyNames = {"transactionId","productName","quantity","price", "totalPrice",
                "margin","invoiceNumber","time","sellername","time_deleted"};

        boolean [] editable = {false,false, false,false,false, false,false,false,false,false};

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, deletedSalesPropertyNames, deletedSalesTableColumnNames,
                editable);


        EventList eventList = new BasicEventList();
        eventList.addAll(items);
        deletedSalesTable.setModel(new DefaultEventTableModel(eventList,tableFormat));
    }

    private void onOK() {
        // add your code here

        int n = JOptionPane.showConfirmDialog(null,
                "This Action can not be reversed. Do you want to continue?",
                "Warning", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            int[] selectedRows = deletedSalesTable.getSelectedRows();

            ArrayList<Item> items = new ArrayList<>();
            for(int i=0; i<selectedRows.length; i++){
                int itemId = (int) deletedSalesTable.getModel().getValueAt(selectedRows[i],0);
                Item item = new Item();
                item.setTransactionId(itemId);
                items.add(item);
            }
            try {
                mAcess.removeDeletedSales(items);
                populateDeletedSalesTable();
            } catch (Exception e1) {
                e1.printStackTrace();
            }finally {
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

}
