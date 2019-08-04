package sell_ui;

import admin_ui.ProductCategory;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import print.Header;
import print.Printer2;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class PayDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox customerComboBox;
    private JComboBox invoiceComboBox;
    private JFormattedTextField cashFormattedTextField;
    private JFormattedTextField balanceFormattedTextField;
    private JButton goButton;
    private JLabel statusMessageLabel;
    private static MySqlAccess mAcess;
    private String userName;
    private Header header;
    private int tillNumber;
    //the amount owed: either the total balance for a customer or the balance on an specific invoice
    private double balance = 0.0; //TODO: be careful. global variable set for change computation, should only be local in invoiceComboBox.addActionListener

    private static boolean DEBUG = true; //for debugging getFormattedEditor function
    private static NumberFormat doubleFormat;


    public PayDialog(JFrame frame, Header header,String userName, int tillNumber, String serverIp) {
        super(frame,true);
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Payment");
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        this.header = header;
        this.userName=userName;
        this.tillNumber = tillNumber;

        doubleFormat = NumberFormat.getNumberInstance();
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);

        cashFormattedTextField.setFormatterFactory(
                new DefaultFormatterFactory(doubleFormatter));




        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(customerComboBox, getCustomers(),new ProductCategory.CustomerTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

//        customerComboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                try {
//                    Customer customer = (Customer)customerComboBox.getSelectedItem();
//                    int customerId = customer.getId();
//
//
//                    //get both partial and unpaid invoices
//
//                    invoiceComboBox.setModel(new DefaultComboBoxModel(mAcess.getUnpaidInvoices(customerId).toArray()));
//                    invoiceComboBox.insertItemAt("",0);
//                    invoiceComboBox.setSelectedIndex(0);
//                } catch (ClassNotFoundException e1) {
//                    e1.printStackTrace();
//                } catch (SQLException e1) {
//                    e1.printStackTrace();
//                }catch (Exception e1){
//                    e1.printStackTrace();
//                }
//            }
//        });

        invoiceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(invoiceComboBox.getSelectedIndex()!=0){ //set balance for specific invoice
                        Invoice invoice = (Invoice) invoiceComboBox.getSelectedItem();
                        //TODO: use enum for invoice status
                        balance = invoice.getStatus().equals("partial") ? mAcess.getInvoiceBalance(invoice.getId()) : invoice.getAmount();

                        //balanceFormattedTextField.setText(Double.toString(balance));
                        balanceFormattedTextField.setText(String.format("%,.0f", balance));
                    }
                    else { //set the total balance
                        Customer customer = (Customer)customerComboBox.getSelectedItem();
                        int customerId = customer.getId();
                        balance = mAcess.getCustomerBalance(customerId);
                        balanceFormattedTextField.setText(String.format("%,.0f", balance));
                    }
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    //e1.printStackTrace();
                    /* TODO this exception should be handled by the mAcess.getCustomerBalance(customerId) function so as not to expose the implementation of that class.
                      TODO the function should simply return the balance as zero if there is no customer record in statement table*/
                    System.out.println("Exception: no records for customer in customer statement table");
                    balanceFormattedTextField.setText(String.format("%,.0f", 0.0));
                } catch (Exception ex){
                    ex.printStackTrace(); //if first item which is an empty string object is selected
                }
            }
        });

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
        setLocationRelativeTo(null);
        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Customer customer = (Customer)customerComboBox.getSelectedItem();
                    int customerId = customer.getId();


                    //get both partial and unpaid invoices

                    invoiceComboBox.setModel(new DefaultComboBoxModel(mAcess.getUnpaidInvoices(customerId).toArray()));
                    invoiceComboBox.insertItemAt("",0);
                    invoiceComboBox.setSelectedIndex(0);
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }catch (Exception e1){
                    e1.printStackTrace();
                }

            }
        });
        customerComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                balanceFormattedTextField.setValue("");
                cashFormattedTextField.setValue(null);
                invoiceComboBox.setModel(new DefaultComboBoxModel());

            }
        });
        cashFormattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {

                //super.keyReleased(e);
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    cashFormattedTextField.setValue(null);
                    cashFormattedTextField.setFocusable(false);
                    fm.getActiveWindow().requestFocusInWindow();


                }

                double cash = 0.0;

                if(!cashFormattedTextField.getText().isEmpty()) {
                    cash = (double)getFormattedTextFieldValue(cashFormattedTextField);
                }

                if(cash>0.0) {
                    cashFormattedTextField.setText(String.format("%,.0f", cash));
                }
            }
        });
    }

    public static Object getFormattedTextFieldValue(JFormattedTextField ftf)  {

        try {
            ftf.commitEdit();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Object o = ftf.getValue();
        if (o instanceof Double) {
            return o;
        } else if (o instanceof Number) {
            return new Double(((Number) o).doubleValue());
        } else {
            if (DEBUG) {
                System.out.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return doubleFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }


    }

    private void onOK() {
        try {
            //Double cash = Double.parseDouble(cashFormattedTextField.getText());
            Double cash =(Double)getFormattedTextFieldValue(cashFormattedTextField);
            if(cash < 0.0){
                JOptionPane.showMessageDialog(null,"Cash can not be negative", "Invalid value"
                , JOptionPane.ERROR_MESSAGE);
                return;
            }
                if (invoiceComboBox.getSelectedIndex() != 0) {//clear balance for specific invoice

                    Invoice invoice = (Invoice) invoiceComboBox.getSelectedItem();
                    //double amountOwed =Double.parseDouble(balanceFormattedTextField.getText()); //TODO: balance before payment is made. This is creating exception because of comma but is better than the workaround below with global variabale. Fix this using Formatters
                    double amountOwed = balance; //The balance before payment is made

                    //the partial payment that was previously made
                    double partial_payment = invoice.getStatus().equals("partial") ? invoice.getAmount() - amountOwed : 0.0;

                    //The balance after payment is made. if cash received is less than the invoice amount, invoice will have a balance otherwise balance will be zero
                    double balance = amountOwed > cash ? amountOwed - cash : 0.0;

                    //double change = cash > invoice.getAmount() ? cash - invoice.getAmount() : 0.0;
                    double change = cash > amountOwed ? cash - amountOwed : 0.0;
                    Customer customer = (Customer) customerComboBox.getSelectedItem();
                    Receipt receipt = new Receipt(invoice.getId());
                    receipt.setCashReceived(cash);
                    receipt.setCustomerId(customer.getId());
                    receipt.setFirstname(customer.getFirstname());
                    receipt.setLastname(customer.getLastname());
                    receipt.setBalance(balance); //if partial payment is made, then this is the balance associated with the invoice
                    receipt.setPartialPayment(partial_payment); // this is the previous amount paid that will be indicated on the receipt printout
                    receipt.setChange(change);
                    receipt.setReceiptType(ReceiptType.arrears);
                    receipt.setCashierName(userName);
                    receipt.setTillnumber(tillNumber);

                    int receiptId = mAcess.generateReceipt(receipt);
                    receipt.setReceiptId(receiptId);
                    InvoiceStatus invoiceStatus = balance > 0 ? InvoiceStatus.partial : InvoiceStatus.paid;
                    mAcess.updateInvoiceStatus(invoice.getId(), invoiceStatus);

                    print(invoice, receipt, header);

                } else {//clear balance for several invoices
                    ArrayList<Printout> printouts = new ArrayList<>(); //to hold the invoice and receipt pairs
                    //Double cash = Double.parseDouble(cashFormattedTextField.getText());
                    Customer customer = (Customer) customerComboBox.getSelectedItem();
                    int customerId = customer.getId();

                    ArrayList<Invoice> invoices = mAcess.getUnpaidInvoices(customerId);
                    int numberOfInvoices = invoices.size();
                    int invoiceCount = 0;

                    for (Invoice invoice : invoices) {
                        double amountOwed = invoice.getStatus().equals("unpaid") ? invoice.getAmount() : mAcess.getInvoiceBalance(invoice.getId());

                        double partial_payment = invoice.getStatus().equals("partial") ? invoice.getAmount() - amountOwed : 0.0;

                        //The balance after payment is made
                        double balance = amountOwed > cash ? amountOwed - cash : 0.0;

                        Receipt receipt = new Receipt(invoice.getId());

                        double cash_rcvd = cash < amountOwed ? cash : amountOwed;
                        if (invoiceCount == numberOfInvoices - 1) {
                            cash_rcvd = cash;
                        }

                        if(cash_rcvd==0)
                            continue; // to prevent generating receipts when no cash is received


                        receipt.setCashReceived(cash_rcvd);

                        receipt.setCustomerId(customer.getId());
                        receipt.setFirstname(customer.getFirstname());
                        receipt.setLastname(customer.getLastname());
                        receipt.setCashierName(userName);
                        receipt.setTillnumber(tillNumber);
                        receipt.setBalance(balance);
                        receipt.setPartialPayment(partial_payment); // this is the previous amount paid that will be indicated on the receipt printout

                        double change = (amountOwed < cash) && (invoiceCount < numberOfInvoices - 1) ? 0.0 : cash - amountOwed;

                        if (change < 0.0) {// a negative change may arise out of the above conditional when cash is less than amount owed and you are at the last iteration of the loop
                            change = 0.0;
                        }

                        receipt.setChange(change);
                        receipt.setReceiptType(ReceiptType.arrears);

                        int receiptId = mAcess.generateReceipt(receipt);
                        receipt.setReceiptId(receiptId);


                        //todo: what if balance = 0, will the invoice status be updated
                        InvoiceStatus invoiceStatus = balance > 0 ? InvoiceStatus.partial : InvoiceStatus.paid;
                        mAcess.updateInvoiceStatus(invoice.getId(), invoiceStatus);

                        cash = cash - cash_rcvd;
                        invoiceCount++;

                        printouts.add(new Printout(invoice, receipt));
                    }

                    for (Printout printout : printouts) {
                        print(printout.getInvoice(), printout.getReceipt(), header);
                    }
                }

                dispose();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void print(Invoice invoice, Receipt receipt, Header header) {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        double margin = 3.6; // 1 tenth of an inch
        paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                - margin * 2);
        pf.setPaper(paper);

        job.setPrintable(new Printer2(invoice, receipt, header , DocType.RECEIPT),pf);

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
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public  void populateComboBox2(JComboBox comboBox, ArrayList arrayList, TextFilterator textFilterator) throws InvocationTargetException {
        EventList eventList = GlazedLists.eventList(arrayList);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    AutoCompleteSupport support = AutoCompleteSupport.install(comboBox,eventList, textFilterator);
                    support.setFilterMode(TextMatcherEditor.CONTAINS);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Customer> getCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            customers = mAcess.getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static class InvoiceTextFilterator implements TextFilterator<Invoice> {
        @Override
        public void getFilterStrings(List<String> baseList, Invoice invoice) {
            baseList.add(Integer.toString(invoice.getId()));
            baseList.add(Double.toString(invoice.getAmount()));
            baseList.add(invoice.getStatus());
            baseList.add(invoice.getUsername()); //name of the system user
            baseList.add(invoice.getFirstName());
        }

    }

}
