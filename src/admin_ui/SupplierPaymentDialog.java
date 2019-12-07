package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.Payment;
import model.Supplier;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import sell_ui.UI;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.NumberFormat;

public class SupplierPaymentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel paySupplierPanel;
    private JComboBox supplierComboBox2;
    private JFormattedTextField amountOwedFormattedTextField;
    private JFormattedTextField paymentAmountFormattedTextField;
    private JTextField paymentDescriptionTextField;
    private JComboBox paymentTypecomboBox;
    private static MySqlAccess mAcess;
    Double balance = 0.0;
    private static NumberFormat doubleFormat;

    public SupplierPaymentDialog(JFrame frame, EventList suppliersEventlist, String serverIp) {
        super(frame,true);
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Payments & Adjustment");
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);

        doubleFormat = NumberFormat.getNumberInstance();
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);

        paymentAmountFormattedTextField.setFormatterFactory(
                new DefaultFormatterFactory(doubleFormatter));

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(supplierComboBox2,suppliersEventlist, new ProductCategory.SupplierTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        amountOwedFormattedTextField.setEditable(false);

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
        supplierComboBox2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(supplierComboBox2.getSelectedIndex()!=-1) {
                    Supplier supplier = (Supplier) supplierComboBox2.getSelectedItem();
                    try {
                        balance = mAcess.getSupplierBalance(supplier.getId());
                        //amountOwedFormattedTextField.setText(balance.toString());
                        amountOwedFormattedTextField.setText(String.format("%,.0f", balance));
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        paymentAmountFormattedTextField.addKeyListener(new KeyAdapter() {
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
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    paymentAmountFormattedTextField.setValue(null);
                    paymentAmountFormattedTextField.setFocusable(false);
                    fm.getActiveWindow().requestFocusInWindow();
                }

                double cash = 0.0;

                if(!paymentAmountFormattedTextField.getText().isEmpty()) {
                    cash = (double) UI.getFormattedTextFieldValue(paymentAmountFormattedTextField);
                }

                if(cash>0.0) {
                    paymentAmountFormattedTextField.setText(String.format("%,.0f", cash));
                }
            }
        });
    }

    private void onOK() {

        try {
            Supplier supplier = (Supplier)supplierComboBox2.getSelectedItem();
            int supplierId = supplier.getId();
            Payment payment = new Payment();
            Double amount = (Double)UI.getFormattedTextFieldValue(paymentAmountFormattedTextField);
            if(balance<amount){
                JOptionPane.showMessageDialog(null,"Amount should not be more than balance",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }else {
                payment.setAmount(amount);
                payment.setDescription(paymentDescriptionTextField.getText());
                payment.setSupplier_id(supplierId);
//                String paymentType = paymentTypecomboBox.getSelectedItem().toString()=="Payment".toString()? "p" : "a";
                String paymentType ="";
                if(paymentTypecomboBox.getSelectedItem().toString()=="Payment".toString()){
                    paymentType = "p";
                }else if(paymentTypecomboBox.getSelectedItem().toString()=="reduce_balance".toString()){
                    paymentType="r";
                }else if(paymentTypecomboBox.getSelectedItem().toString()=="increase_balance".toString()){
                    paymentType="i";
                }
                payment.setPaymentType(paymentType);
                mAcess.submitPayment(payment);
                //resetFormFields(paySupplierPanel);
                amountOwedFormattedTextField.setText("");
                paymentAmountFormattedTextField.setText(null);
                paymentDescriptionTextField.setText("");
                //populateSupplyTransactionsTable();
                dispose();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public  void populateComboBox2(JComboBox comboBox, EventList eventList, TextFilterator textFilterator) throws InvocationTargetException {
        //eventList = GlazedLists.eventList(arrayList);
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
}
