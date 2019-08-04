package sell_ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import model.Item;
import model.Refund;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ReturnsView extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel leftReturnsPanel;
    private JPanel returnsLeftTopPanel;
    private JTable refundsTable;
    private JButton refreshReturnsButton;
    private JTable returnDetailsTable;
    private static MySqlAccess mAcess;

    public ReturnsView(String serverIp) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        setTitle("Returned Items");



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

        populateRefundsTable();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void populateRefundsTable() {
        ArrayList<Refund> refunds = mAcess.getRefunds();
        TextFilterator refundFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Refund refund = (Refund) o;
                baselist.add(refund.getId());
                baselist.add(refund.getAmount());
                baselist.add(refund.getComment());
                baselist.add(refund.getDatePaid());
            }
        };

        String[] refundsTableColumnNames = {
                "No.","Cash Refund","receipt_no","Grand Total","cashier","Comment","Date"};

        String [] refundsPropertyNames = {"id","amount","receipt_no","grandTotal", "username","comment","datePaid"};

        boolean [] editable = {false,false, false,false,false, false,false};

        TableFormat tableFormat = GlazedLists.tableFormat(Refund.class, refundsPropertyNames, refundsTableColumnNames,
                editable);


        EventList eventList = new BasicEventList();
        eventList.addAll(refunds);
        refundsTable.setModel(new DefaultEventTableModel(eventList,tableFormat));
        refundsTable.getSelectionModel().addListSelectionListener(new ReturnsTableRowListener() );
    }

    private void populateReturnDetailsTable(int returnId) {
        ArrayList<Item> items = getReturnedItems(returnId);
        String [] itemColumLabels = {"No", "Product", "Quantity", "Price", "Total"};
        String [] itemPropertyNames = {"transactionId","productName", "quantity", "price", "totalPrice"};

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, itemPropertyNames, itemColumLabels);

        EventList eventList = new BasicEventList();
        eventList.addAll(items);
        returnDetailsTable.setModel(new DefaultEventTableModel(eventList,tableFormat));

    }

    private ArrayList<Item> getReturnedItems(int returnId) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getReturnDetails(returnId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private class ReturnsTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = refundsTable.getSelectedRow();
            try {
                //TODO: -1 is used to prevent arrayout of bounds exception when createItemSuppliedTableModelListener() calls the populateSupplyTransactionsTable();
                //TODO: the cleaner way to refresh the supplierTransactions table is to update the arraylist used to populate it.
                if(row !=-1) {
                    int returnId = (int) refundsTable.getModel().getValueAt(row, 0);
                    populateReturnDetailsTable(returnId);
                }
            }catch (ArrayIndexOutOfBoundsException e1){
                e1.printStackTrace();
            }catch (Exception e1){
                e1.printStackTrace();
            }finally {
                System.out.println(row);
            }
        }
    }
}
