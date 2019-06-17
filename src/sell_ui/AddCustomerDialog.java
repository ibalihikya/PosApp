package sell_ui;

import ca.odell.glazedlists.EventList;
import model.Customer;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.event.*;

public class AddCustomerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField phoneTextField;
    private JTextField addressLocationTextField;
    private JComboBox sexComboBox;
    private static MySqlAccess mAcess;

    public AddCustomerDialog(JDialog dialog, EventList<Customer> customers, String serverIp) {
        super(dialog,true);
        setTitle("Add Customer");
        setContentPane(contentPane);
        //setModal(true);


        getRootPane().setDefaultButton(buttonOK);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);

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
    }

    private void onOK() {
        try {
            Customer customer = new Customer();
            customer.setFirstname(firstNameTextField.getText());
            customer.setLastname(lastNameTextField.getText());
            customer.setSex((String)sexComboBox.getSelectedItem());
            customer.setAddress(addressLocationTextField.getText());
            customer.setPhone1(phoneTextField.getText());
            mAcess.addCustomer(customer);

            //update the CustomersDialog
            //TODO: update the customersDialog with only one record without having delete and fetch all customers data
            CustomerDialog.getCustomers().clear();
            CustomerDialog.getCustomers().addAll(mAcess.getCustomers());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


}
