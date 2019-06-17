package sell_ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import print.Header;
import print.Printer2;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;

public class ReprintDialog extends JDialog {
    private Product product = null;
    private JButton okButton = new JButton("Print");
    private JButton cancelButton = new JButton("Cancel");
    private static MySqlAccess mAcess;

    private JTable receiptTable;
    private JTable invoiceTable;
    ArrayList<Item> items;
    private OKAction okAction;

    public ReprintDialog(JFrame frame, String title, Header header, String serverIp) {
        super(frame, title, true); // !!!!! made into a modal dialog
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);
        receiptTable = new JTable();
        invoiceTable = new JTable();

        populateReceiptTable();

        receiptTable.getSelectionModel().addListSelectionListener(new TransactionTableRowListener());

        JLabel shortcutsLabel = new JLabel("Cancel: CTRL+C" + " |  " + "OK: Enter");
        shortcutsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(shortcutsLabel);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JRootPane rootPane = new JRootPane();
        rootPane.setDefaultButton(okButton);

        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(receiptTable);
        scrollPane.setPreferredSize(new Dimension(700, 300));
        add(scrollPane, BorderLayout.NORTH);
        add(new JScrollPane(invoiceTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(frame);

        CancelAction cancelAction = new CancelAction("Cancel", new Integer(KeyEvent.VK_C));
        cancelButton.setAction(cancelAction);

        okAction = new OKAction("OK", header);
        okButton.setAction(okAction);

        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"ok");
        okButton.getActionMap().put("ok", okAction);

        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"cancel");
        cancelButton.getActionMap().put("cancel",cancelAction );
    }

    private void populateReceiptTable() {
        ArrayList<Receipt> receipts = getReceipts();

        EventList eventList = new BasicEventList();
        eventList.addAll(receipts);

        String [] receiptColumLabels = {"Receipt no.","Cash Received", "Change", "Date", "Invoice no.", "Balance" };
        String [] receiptPropertyNames = {"receiptId", "cashReceived", "change","date_created", "invoice_id", "balance"};
        boolean [] editable = {false,false, false,false,false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(Receipt.class, receiptPropertyNames, receiptColumLabels,
                editable);
        receiptTable.setModel(new EventTableModel(eventList,tableFormat));
        receiptTable.getColumnModel().removeColumn(receiptTable.getColumnModel().getColumn(4)); //hide invoice no
        receiptTable.getColumnModel().removeColumn(receiptTable.getColumnModel().getColumn(4)); //hide balance
    }

    private void populateInvoiceTable() {
        int row = receiptTable.getSelectedRow();
        int invoice_id = (int) receiptTable.getModel().getValueAt(row, 4);

        items = getInvoiceItems(invoice_id);

        String [] itemColumLabels = {"Product", "Quantity", "Units", "Price", "Total" };
        String [] itemPropertyNames = {"productName","quantity","units","price", "totalPrice"};
        boolean [] editable = {false,false, false, false, false};

        EventList eventList = new BasicEventList();
        eventList.addAll(items);

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, itemPropertyNames,
                itemColumLabels, editable);
        invoiceTable.setModel(new EventTableModel(eventList,tableFormat));
    }

    private ArrayList<Receipt> getReceipts() {
        ArrayList<Receipt> receipts = new ArrayList<>();
        try {
            receipts = mAcess.getReceipts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receipts;
    }

    private ArrayList<Item> getInvoiceItems(int invoiceId) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getInvoiceItems(invoiceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }


    public class CancelAction extends AbstractAction{
        private  JTextField textField;
        private JTable table;

        public CancelAction(String text, Integer mnemonic){
            super(text);
            this.textField = textField;
            this.table = table;
            putValue(MNEMONIC_KEY, mnemonic);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
        }
    }

    public class OKAction extends AbstractAction{

        private Header header;

        public OKAction(String text, Header header){
            super(text);
            this.header = header;
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int row = receiptTable.getSelectedRow();
                int receiptId = (int) receiptTable.getModel().getValueAt(row, 0);
                double cashReceived = (Double) receiptTable.getModel().getValueAt(row, 1);
                double change = (Double) receiptTable.getModel().getValueAt(row, 2);
                String dateCreated = (String) receiptTable.getModel().getValueAt(row, 3);
                int invoice_id = (int) receiptTable.getModel().getValueAt(row, 4);
                double balance = (Double) receiptTable.getModel().getValueAt(row, 5);

                Invoice invoice = mAcess.getInvoice(invoice_id);

                Receipt receipt = new Receipt(invoice_id);
                receipt.setCashReceived(cashReceived);
                receipt.setBalance(balance);
                receipt.setChange(change);
                receipt.setReceiptId(receiptId);
                receipt.setDate_created(dateCreated);

                PrinterJob job = PrinterJob.getPrinterJob();
                PageFormat pf = job.defaultPage();
                Paper paper = new Paper();
                double margin = 3.6; // 1 tenth of an inch
                paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                        - margin * 2);
                pf.setPaper(paper);

                job.setPrintable(new Printer2(invoice,receipt,header, DocType.RECEIPT),pf);

                //boolean ok = job.printDialog();
                if (true) {
                    try {
                        //throw(new PrinterException("Printer not connected!"));
                        job.print();

                    } catch (PrinterException ex) {
          /* The job did not successfully complete */
                        System.out.println(ex);
                    }
                }

            } catch (Exception e1) {
                e1.printStackTrace();
            }
            dispose();
        }
    }

    private class TransactionTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {
                populateInvoiceTable();
            }catch (ArrayIndexOutOfBoundsException e1){
                e1.printStackTrace();
            }catch (Exception e1){
                e1.printStackTrace();
            }


        }
    }
}
