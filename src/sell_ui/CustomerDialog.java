package sell_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import model.Customer;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public  class  CustomerDialog extends JDialog {
    private Customer customer = null;
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private int customerId;
    private static MySqlAccess mAcess;
    private static  EventList customers;
    private JTable customersTable;

    public CustomerDialog(JFrame frame, String title,  String serverIp) {
        super(frame, title, true); // !!!!! made into a modal dialog
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);

        JLabel shortcutsLabel = new JLabel("Navigate to table: TAB" + " |  " + "Cancel: ALT+C" + " |  " + "OK: Enter");
        shortcutsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(shortcutsLabel);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JRootPane rootPane = new JRootPane();
        rootPane.setDefaultButton(okButton);

        customers = GlazedLists.eventList(getCustomersFromDb());

        JTextField filterField = new JTextField(25);
        TextFilterator customerFilterator = new TextFilterator() {
            public void getFilterStrings(List baseList, Object element) {
                Customer customer = (Customer) element;
                baseList.add(customer.getFirstname());
                baseList.add(customer.getLastname());
                baseList.add(customer.getSex());
                baseList.add(customer.getAddress());
                baseList.add(customer.getEmail());
                baseList.add(customer.getId());
                baseList.add(customer.getPhone1());
                baseList.add(customer.getPhone2());
                baseList.add(customer.getBarcode());
            }
        };
        TextComponentMatcherEditor matcherEditor = new TextComponentMatcherEditor(filterField, customerFilterator);
        FilterList filteredCustomers = new FilterList(customers, new ThreadedMatcherEditor(matcherEditor));

        JButton addCustomerButton = new JButton("Add customer:");
        addCustomerButton.setFocusable(false);
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(getContentPane());
                AddCustomerDialog addCustomerDialog = new AddCustomerDialog(dialog,customers,serverIp);
                addCustomerDialog.setVisible(true);
            }
        });

        // build a panel to hold the filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterField);
        filterPanel.add(addCustomerButton);

        String[] propertyNames = new String[] {"id","firstname","lastname", "sex", "phone1", "address"};
        String[] columnLabels = new String[] {"Id","First name", "Last Name", "sex", "Phone1", "Address"};
        TableFormat tableFormat = GlazedLists.tableFormat(Customer.class, propertyNames, columnLabels);
        customersTable = new JTable(new EventTableModel(filteredCustomers, tableFormat));
        customersTable.setRowSelectionAllowed(true);
        customersTable.setSelectionMode(SINGLE_SELECTION);
        customersTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        customersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        customersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        customersTable.getColumnModel().getColumn(3).setPreferredWidth(40);
        customersTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        customersTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        //Double click event on selected row
        customersTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    onOk();
                }
            }
        });


        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(customersTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        filterField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                customersTable.clearSelection();

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    customersTable.requestFocusInWindow();
                    customersTable.setRowSelectionInterval(0,0);
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        CancelAction cancelAction = new CancelAction("Cancel", new Integer(KeyEvent.VK_C), filterField,customersTable);
        cancelButton.setAction(cancelAction);

        OKAction okAction = new OKAction("OK", filterField, customersTable);
        okButton.setAction(okAction);

        //TODO: WHY are both ok and cancel buttons using the same key event
        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"ok");
        okButton.getActionMap().put("ok", okAction);

        EscAction escAction = new EscAction(filterField);

        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"cancel");
        cancelButton.getActionMap().put("cancel",cancelAction );

        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        customersTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "ok");

        //Return to filter field when Esc is pressed
        KeyStroke search = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        customersTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(search, "search");
        customersTable.getActionMap().put("search", escAction);
        customersTable.getActionMap().put("ok",okAction);

        pack();
        setLocationRelativeTo(frame);
    }

    public static void setCustomers(EventList customers) {
        CustomerDialog.customers = customers;
    }

    public static EventList getCustomers() {
        return customers;
    }

    public Customer getSelectedCustomer() {
        return customer;
    }

    public class CancelAction extends AbstractAction{
        private  JTextField textField;
        private JTable table;

        public CancelAction(String text, Integer mnemonic, JTextField textField, JTable table){
            super(text);
            this.textField = textField;
            this.table = table;
            putValue(MNEMONIC_KEY, mnemonic);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            customer = null;
            textField.setText("");
            textField.requestFocusInWindow();
            table.clearSelection();
            table.getSelectionModel().setLeadSelectionIndex(-1);
            table.getSelectionModel().setAnchorSelectionIndex(-1);
            table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(-1);
            table.getColumnModel().getSelectionModel().setLeadSelectionIndex(-1);
            dispose();
        }
    }

    public class OKAction extends AbstractAction{

        private  JTextField textField;
        private JTable table;

        public OKAction(String text, JTextField textField, JTable table){
            super(text);
            this.textField = textField;
            this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onOk();
        }
    }

    private ArrayList<Customer> getCustomersFromDb() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            customers = mAcess.getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    private void onOk() {
        int row = customersTable.getSelectedRow();
        customerId = (int) customersTable.getModel().getValueAt(row, 0);

        try {
            customer = mAcess.getCustomer(customerId);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

//            textField.setText("");
//            textField.requestFocusInWindow();
//            table.clearSelection();
//            table.getSelectionModel().setLeadSelectionIndex(-1);
//            table.getSelectionModel().setAnchorSelectionIndex(-1);
//            table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(-1);
//            table.getColumnModel().getSelectionModel().setLeadSelectionIndex(-1);
        dispose();
    }

    public class EscAction extends AbstractAction{
        private JTextField textField;

        public EscAction(JTextField textField){
            //super(text);
            this.textField = textField;
            //this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            customersTable.clearSelection();
            textField.requestFocusInWindow();
        }
    }
}
