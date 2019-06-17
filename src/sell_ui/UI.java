package sell_ui;

import Utilities.SettingsParser;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import com.jgoodies.forms.layout.FormLayout;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import print.Header;
import print.Printer;
import print.ReceiptHeader;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UI extends JFrame implements KeyListener,TableModelListener {
    private JToolBar toolBar;
    private JButton buttonSubmit;
    private JButton cancelButton;
    private JButton deleteItemButton;
    private JButton pauseButton;
    private JButton shortcutsButton;
    private JButton customersButton;
    private JButton paymentButton;
    private JButton reprintButton;
    private JPanel centerPanel;
    private JPanel bottomPanel;
    private JPanel centerTopPanel;
    private JFormattedTextField totalFormattedTextField;
    private JPanel centerMiddlePanel;
    private JPanel centerBottomPanel;
    private JFormattedTextField changeformattedTextField;
    private JFormattedTextField cashformattedTextField;
    private JScrollPane tableScrollPane;
    private JTable itemsTable;
    private JPanel customerPanel;
    private JFormattedTextField customerFormattedTextField;
    private JCheckBox creditCheckBox;
    private JTextField invoiceTextField;
    private JPanel pausedTransactionsPanel;
    private JComboBox savedTransactionsComboBox;
    private JButton clearButton;
    private JPanel mainPanel;
    private JTextField receiptTextField;
    private JButton removeTransactionButton;
    private JPanel totalPanel;
    private JPanel topPanel;
    private JButton defineProductButton;

    private static MySqlAccess mAcess;
    private CustomerTransaction customerTransaction;
    private static String userName;
    private static Double grandTotal;
    private int rowNumber = 0;
    ProductDialog productsDialog;
    CustomerDialog customersDialog;
    ReprintDialog reprintDialog;

    private int customerId;

    private String barcode = "";
    private final ItemsTableModel itemsTableModel;
    private final EventList products;
    //private final EventList customers;
    private boolean toModify = true;
    private boolean readyForNextTransaction = false;
    //String cash = "";
    private ArrayList<ArrayList<Item>>savedTransactions = new ArrayList<>();
    //private DefaultListModel listModel;
    private DefaultComboBoxModel defaultComboBoxModel;
    private Header header;
    private ReceiptHeader receiptHeader; //TODO: remove this global definition; too many potential errors especially in submittransaction()
    private SettingsParser settingsParser;
    private String serverIP;


    private Color primaryLightColor;
    private Color tableEvenRowColor;
    private  Color primaryTextColor;
    private Color textColor;
    private Color lightgray;
    private Color accentColor;
    private static Color primaryColor;

    private  boolean DEBUG = true; //for debugging getFormattedEditor function
    NumberFormat doubleFormat;

    //private JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);

    private JFrame topFrame;

    private String cashstr = "";

    private String[] columnNames = {"No.",
            "Product",
            "Quantity",
            "Units",
            "Price",
            "Total Price",
            "product id"};

    private int lastrow = -1;

    public UI(String title) {

        super(title);

        ImageIcon cancelIcon = createImageIcon("/images/ic_close_black_18dp.png", "Cancel transaction");
        ImageIcon submitIcon = createImageIcon("/images/ic_done_black_18dp.png", "Submit transaction");
        ImageIcon removeIcon = createImageIcon("/images/ic_remove_black_18dp.png", "Remove Item");
        ImageIcon shortcutsIcon = createImageIcon("/images/ic_apps_black_18dp.png", "Short cuts");
        ImageIcon pauseIcon = createImageIcon("/images/ic_pause_black_18dp.png", "Pause Transaction");
        ImageIcon customersIcon = createImageIcon("/images/ic_people_outline_blue_18dp.png", "Customers");
        ImageIcon paymentIcon = createImageIcon("/images/ic_payment_red_18dp.png", "Payment");
        ImageIcon reprintIcon = createImageIcon("/images/ic_print_green_18dp.png", "Reprint receipt");
        ImageIcon defineProductIcon = createImageIcon("/images/ic_define_product.png", "Define product");



        ImageIcon logoIcon = createImageIcon("/images/ic_trending_up_16pt_3x.png", "Logo");

        java.net.URL imgURL = getClass().getResource("/images/ic_trending_up_white_24dp.png");
        Image logoImage = Toolkit.getDefaultToolkit().getImage(imgURL);

        setIconImage(getScaledImage(logoImage,30,30,Color.decode("#616161") ));

        topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);


        settingsParser = new SettingsParser("settings.xml");
        this.serverIP=settingsParser.getServerIp();

        //TODO: remove duplication of Header and ReceiptHeader Classes
        header = new Header();
        header.setBusinessName(settingsParser.getBusinessName());
        header.setLocation(settingsParser.getLocation());
        header.setTelephoneNumber1(settingsParser.getPhone1());
        header.setTelephoneNumber2(settingsParser.getPhone2());
        header.setTin(settingsParser.getTin());

        receiptHeader = new ReceiptHeader();
        receiptHeader.setBusinessName(settingsParser.getBusinessName());
        receiptHeader.setLocation(settingsParser.getLocation());
        receiptHeader.setTelephoneNumber1(settingsParser.getPhone1());
        receiptHeader.setTelephoneNumber2(settingsParser.getPhone2());
        receiptHeader.setTin(settingsParser.getTin());
        receiptHeader.setUserName(userName);

        grandTotal = 0.0;

        JRootPane rootPane = new JRootPane();
        rootPane.setDefaultButton(buttonSubmit);

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, settingsParser.getServerIp());
        customerTransaction = new CustomerTransaction(userName);

        //listModel = new DefaultListModel();
        defaultComboBoxModel = new DefaultComboBoxModel();

        products = new BasicEventList();
        try {
            products.addAll(mAcess.getAllProducts());
        } catch (Exception e) {
            e.printStackTrace();
        }


        customersDialog = new CustomerDialog(this, "Customers",serverIP);
        customersDialog.setSize(new Dimension(700,500));

        receiptTextField.setFocusable(false);
        changeformattedTextField.setFocusable(false);
        cashformattedTextField.setFocusable(false);
        buttonSubmit.setFocusable(false);
        cancelButton.setFocusable(false);
        deleteItemButton.setFocusable(false);
        shortcutsButton.setFocusable(false);
        savedTransactionsComboBox.setFocusable(false);
        pauseButton.setFocusable(false);
        removeTransactionButton.setFocusable(false);
        customersButton.setFocusable(false);
        customerFormattedTextField.setBackground(Color.WHITE);
        customerFormattedTextField.setFocusable(false);
        paymentButton.setFocusable(false);
        reprintButton.setFocusable(false);
        clearButton.setFocusable(false);
        creditCheckBox.setFocusable(false);
        invoiceTextField.setFocusable(false);
        defineProductButton.setFocusable(false);

        removeTransactionButton.setEnabled(false); //should not be active when there are no paused transactions

        //primaryColor = Color.decode("#9E9E9E");
        primaryColor = Color.decode("#00bcd4");
        primaryLightColor = Color.decode("#F5F5F5");
        //primaryLightColor = Color.decode("#b2ebf2");
        tableEvenRowColor = Color.decode("#F0F0F0");
        lightgray = Color.decode("#f2f2f2");
        accentColor = Color.decode("#ff5722");
        //accentColor = Color.ORANGE;
        Color backgroundColor = Color.decode("#FFFFFF");
        Color dividerColor = Color.decode("#BDBDBD");
        Color primaryDarkColor = Color.decode("#616161");
        Color panelBackgroundColor = new Color(235, 245, 251);


        //textColor = Color.decode("#FFFFFF");
        primaryTextColor = Color.decode("#212121");
        Color secondaryTextColor = Color.decode("#757575");

        customerPanel.setVisible(true);
        pausedTransactionsPanel.setVisible(true);
        pauseButton.setVisible(true);



//        centerPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,dividerColor));
//        bottomPanel.setBorder(BorderFactory.createMatteBorder(1,0,1,0,dividerColor));
//        centerBottomPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,dividerColor));

        centerPanel.setBorder(BorderFactory.createEmptyBorder());
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1,0,1,0,dividerColor));
        centerBottomPanel.setBorder(BorderFactory.createMatteBorder(1,0,0,0,dividerColor));
        topPanel.setBorder(BorderFactory.createEmptyBorder());
        customerPanel.setBorder(BorderFactory.createEmptyBorder());
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        centerMiddlePanel.setBorder(new TextBubbleBorder(dividerColor,1,10,0,
                new Insets(20,1,5,1)));
        //centerMiddlePanel.setOpaque(false);

        centerTopPanel.setBorder(new TextBubbleBorder(dividerColor,1,10,0,
                new Insets(0,0,0,0)));
//        totalPanel.setBorder(new TextBubbleBorder(Color.orange,1,0,0,
//                new Insets(0,0,0,0)));
        //totalPanel.setBorder(BorderFactory.createEmptyBorder());
        totalPanel.setBorder(BorderFactory.createMatteBorder(1,0,1,0,dividerColor));

        FormLayout formLayout = new FormLayout();

        centerTopPanel.setBackground(backgroundColor);
        totalPanel.setBackground(backgroundColor);

        toolBar.setBackground(primaryLightColor);
        centerPanel.setBackground(backgroundColor);
        customerPanel.setBackground(panelBackgroundColor);
        topPanel.setBackground(panelBackgroundColor);
        centerMiddlePanel.setBackground(new Color(21, 67, 96));
        //tableScrollPane.setBackground(backgroundColor);
        bottomPanel.setBackground(primaryLightColor);

        totalFormattedTextField.setBorder(BorderFactory.createEmptyBorder());

        itemsTable.getTableHeader().setBackground(primaryColor);
        totalFormattedTextField.setBackground(accentColor);
        totalFormattedTextField.setForeground(Color.WHITE);
        invoiceTextField.setForeground(textColor);

        removeTransactionButton.setBackground(accentColor);

        //Table settings
        itemsTable.setRowSelectionAllowed(true);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        itemsTable.setModel(new ItemsTableModel(columnNames));
        itemsTable.getModel().addTableModelListener(this);
        TableColumnModel columnModel = itemsTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(6)); // hide the productid
        itemsTable.getColumnModel().getColumn(2).setCellEditor(new DoubleEditor(0.0, 1000000.0));
        itemsTable.getColumnModel().getColumn(4).setCellEditor(new DoubleEditor(0.0, 1000000.0));
        itemsTableModel = (ItemsTableModel) itemsTable.getModel();
        itemsTable.setFocusable(false);
        totalFormattedTextField.setFocusable(false);

        centerPanel.setPreferredSize(new Dimension(1000, 400));
        alignTableCells();

        itemsTable.getTableHeader().setFont(new Font(null, Font.PLAIN, 15));

        mainPanel.setBackground(panelBackgroundColor);
        mainPanel.setBorder(BorderFactory.createMatteBorder(1,1,1,1,dividerColor));
        pausedTransactionsPanel.setBackground(panelBackgroundColor);

        itemsTable.setShowGrid(true);
        itemsTable.setGridColor(dividerColor);
        itemsTable.setRowHeight(25);
        itemsTable.setFont(new Font(null, Font.PLAIN, 15));
        itemsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                if (row % 2 == 0) {
                    setBackground(tableEvenRowColor);
                    setForeground(primaryTextColor);
                }
                else {
                    setBackground(backgroundColor);
                    setForeground(table.getForeground());
                }
                return this;
            }
        });

        doubleFormat = NumberFormat.getNumberInstance();
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);

        cashformattedTextField.setFormatterFactory(
                new DefaultFormatterFactory(doubleFormatter));

        SubmitAction submitAction = new SubmitAction("OK");
        buttonSubmit.setAction(submitAction);

        buttonSubmit.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                "submit");
        buttonSubmit.getActionMap().put("submit", submitAction);


        buttonSubmit.setIcon(submitIcon);
        buttonSubmit.setContentAreaFilled(false);
        buttonSubmit.setToolTipText("Submit transaction");

        cancelButton.setIcon(cancelIcon);
        cancelButton.setContentAreaFilled(false);
        cancelButton.setToolTipText("Cancel transaction");

        deleteItemButton.setIcon(removeIcon);
        deleteItemButton.setContentAreaFilled(false);
        deleteItemButton.setToolTipText("Remove Item");

        shortcutsButton.setIcon(shortcutsIcon);
        shortcutsButton.setContentAreaFilled(false);
        shortcutsButton.setToolTipText("Keyboard shortcuts");

        pauseButton.setIcon(pauseIcon);
        pauseButton.setContentAreaFilled(false);
        pauseButton.setToolTipText("Pause Transaction");

        customersButton.setIcon(customersIcon);
        customersButton.setContentAreaFilled(false);
        customersButton.setToolTipText("Customers");

        paymentButton.setIcon(paymentIcon);
        paymentButton.setContentAreaFilled(false);
        paymentButton.setToolTipText("Payment");

        reprintButton.setIcon(reprintIcon);
        reprintButton.setContentAreaFilled(false);
        reprintButton.setToolTipText("Reprint receipt");

        defineProductButton.setIcon(defineProductIcon);
        defineProductButton.setContentAreaFilled(false);
        defineProductButton.setToolTipText("Add Product");





        //populateProductsComboBox();


        cashformattedTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Double change = -1.0;
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                try {
                    change = (double)getFormattedTextFieldValue(cashformattedTextField) -
                            (Double) (totalFormattedTextField.getValue());

                    if(itemsTable.getRowCount()>0 && change>= 0) {//upfront payment
                        changeformattedTextField.setValue(change);
                        changeformattedTextField.setBackground(accentColor);
                        changeformattedTextField.setForeground(Color.WHITE);
                        submitTransaction(InvoiceStatus.paid);
                    }else if(itemsTable.getRowCount()==0 && change>=0){
                        changeformattedTextField.setValue(change);
                        JOptionPane.showMessageDialog(topFrame,"Transaction already submitted",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    else{//partial payment

                        int n = JOptionPane.showConfirmDialog(topFrame,
                                "Cash is less than Total. Do you want to continue?",
                                "Confirmation", JOptionPane.YES_NO_OPTION);
                        if (n == JOptionPane.YES_OPTION) {
                            changeformattedTextField.setValue(0.0);
                            changeformattedTextField.setBackground(accentColor);
                            changeformattedTextField.setForeground(Color.WHITE);
                            if(customerId==0){//propmt user to select a registered customer
                                customersDialog.setVisible(true);
                                Customer customer = customersDialog.getSelectedCustomer();
                                if(customer != null) {
                                    customerFormattedTextField.setText(customer.getFirstname() + " " + customer.getLastname());
                                    customerId = customer.getId();
                                    submitTransaction(InvoiceStatus.partial);
                                    //some changes here be careful
                                    //customerId=0; //reset customerId
                                }
                            }
                            else {//customer already selected so simply submit transaction

                                submitTransaction(InvoiceStatus.partial);
                                //some changes here be careful
                                //customerId=0; //reset customerId

                            }
                            barcode="";
                        }else{
                            //User should enter correct amount of cash
                        }
                    }

                    cashformattedTextField.setFocusable(false);

                    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    fm.getActiveWindow().requestFocusInWindow();
                    lastrow = -1;
                }catch (NumberFormatException e1){
                    System.out.println("Cash not entered or invalid number");
                }catch (Exception e1){
                    e1.printStackTrace();
                }finally {


                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearUi();

            }
        });
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowIndex = itemsTable.getSelectedRow();
                    Double totalPrice = (Double) itemsTableModel.getValueAt(rowIndex, 5);
                    itemsTableModel.removeRow(rowIndex);
                    grandTotal -= totalPrice;
                    if (grandTotal == 0) {
                        totalFormattedTextField.setText("");
                    } else {
                        totalFormattedTextField.setValue(grandTotal);
                    }

                    //re-number the table rows
                    int newRowIndex = 1;
                    for (Vector<Object> row : (Vector<Vector<Object>>) itemsTableModel.getData()) {
                        row.setElementAt(newRowIndex, 0);
                        newRowIndex++;
                    }
                    rowNumber = itemsTable.getRowCount();
                    if(rowNumber==0 && savedTransactionsComboBox.getItemCount()>0){
                        removeTransactionButton.setEnabled(true);
                    }
                }catch (ArrayIndexOutOfBoundsException e1){
                    System.out.println("No items in cart");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        shortcutsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Products List: CTRL+P\n" +
                        "Edit Quantity: CTRL+E\n" +
                        "Submit Transaction: CTRL+ENTER\n" +
                        "Move to cash field: CTRL+C\n" +
                        "Exit change calculator: ESC","Shortcut Keys", JOptionPane.PLAIN_MESSAGE);

            }
        });
        cashformattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {

                //super.keyTyped(e);
               // System.out.println("value after typed: " + cash);

                //cashstr += e.getKeyChar();


            }

            @Override
            public void keyPressed(KeyEvent e) {
                //cashformattedTextField.setText("");
                //super.keyTyped(e);

                //Integer.parseInt(Character.toString(e.getKeyChar()));
                //cashstr += e.getKeyChar();
                //cashformattedTextField.setValue(cashstr);
                //System.out.println("cashstr: " + cashstr);

                //if(!cashformattedTextField.getText().isEmpty()) {
                    //cash = (double)getFormattedTextFieldValue(cashformattedTextField);
                   // cash = Double.parseDouble(cashstr);
                //}

//                if(cash>0.0) {
//                    cashformattedTextField.setText(String.format("%,.0f", cash));
//                }



//                cashstr += e.getKeyChar();
//
//                System.out.println("value after pressed: " + cashstr);
//
//                cash = Double.parseDouble(cashstr);
//
//                System.out.println("parsed: " + cash);
//
//                //cashformattedTextField.setText(String.format("%,.0f", cash));
//                cashformattedTextField.setValue(cash);

            }

            @Override
            public void keyReleased(KeyEvent e) {
                //super.keyTyped(e);

                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    //cashformattedTextField.setValue(0);
                    cashformattedTextField.setValue(null);
                    cashformattedTextField.setFocusable(false);
                    fm.getActiveWindow().requestFocusInWindow();
                    barcode="";

                }

                //cashformattedTextField.setText("");




//                if(!cashformattedTextField.getText().isEmpty()) {
//                    cash = (double)getFormattedTextFieldValue(cashformattedTextField);
//                    if(cash>0.0)
//                        //cashformattedTextField.setValue(cash);
//                        try {
//                            cashformattedTextField.commitEdit();
//                        } catch (ParseException e1) {
//                            e1.printStackTrace();
//                        }
//                }
//
//                System.out.println("value after released: " + cash);

                double cash = 0.0;

                if(!cashformattedTextField.getText().isEmpty()) {
                    cash = (double)getFormattedTextFieldValue(cashformattedTextField);
                }

                if(cash>0.0) {
                    cashformattedTextField.setText(String.format("%,.0f", cash));
                }
            }
        });

        cashformattedTextField.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
               // System.out.println("value changed: " + cash);
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                savedTransactions.add(saveTransactionDetails());
                defaultComboBoxModel.removeAllElements();
                for(int i=0; i<savedTransactions.size();i++){
                    defaultComboBoxModel.addElement("Transaction " + (i+1));
                }
                savedTransactionsComboBox.setModel(defaultComboBoxModel);
                clearUi();
                removeTransactionButton.setEnabled(true);
            }
        });
        removeTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemsTable.getRowCount() == 0) {
                    try {
                        //int index = savedTransactionslist.getSelectedIndex();
                        int index = savedTransactionsComboBox.getSelectedIndex();
                        ArrayList<Item> transactionItems = savedTransactions.get(index);

                        for (Item item : transactionItems) {
                            rowNumber++;
                            Object[] r1 = {rowNumber, item.getProductName(), item.getQuantity(), item.getUnits(),
                                    item.getPrice(), item.getTotalPrice(), item.getProductId()};
                            itemsTableModel.addRow(r1);

                            grandTotal += item.getTotalPrice();
                            totalFormattedTextField.setValue(grandTotal);
                        }

                        //listModel.remove(index);
                        defaultComboBoxModel.removeElementAt(index);
                        savedTransactions.remove(index);
                        removeTransactionButton.setEnabled(false);
                        changeformattedTextField.setText("");
                        cashformattedTextField.setText("");
                        receiptTextField.setText("");
                        changeformattedTextField.setBackground(Color.WHITE);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        customersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customersDialog.setVisible(true);
                Customer customer = customersDialog.getSelectedCustomer();
                if(customer != null) {
                    customerFormattedTextField.setText(customer.getFirstname() + " " + customer.getLastname());
                    customerId = customer.getId();
                }
                barcode="";
            }
        });
        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                PayDialog payDialog = new PayDialog(frame,header,serverIP);
                payDialog.setVisible(true);
            }
        });
        reprintButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reprintDialog = new ReprintDialog(null,"Reprint Receipt",header,serverIP);
                reprintDialog.setSize(new Dimension(700,600));
                reprintDialog.setVisible(true);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                customerFormattedTextField.setText("");
                customerId=0;
                creditCheckBox.setSelected(false);
                invoiceTextField.setText("");
                invoiceTextField.setBackground(backgroundColor);

            }
        });

        defineProductButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                AddProductDialog addProductDialog = new AddProductDialog(topFrame,serverIP);
                addProductDialog.setVisible(true);
                products.clear();
                try {
                    products.addAll(mAcess.getAllProducts());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    //reset table and fields
    private void clearUi() {
        ((ItemsTableModel) itemsTable.getModel()).clearTable();

        itemsTable.getCellEditor(lastrow,2).cancelCellEditing(); //to eliminate error when user hits pause button when last row is still in editing mode
        //TODO: also cater for the case when other rows are in editing mode and the user pauses or cancels the transaction

        grandTotal = 0.0;
        totalFormattedTextField.setText("");
        cashformattedTextField.setText("");
        changeformattedTextField.setText("");
        rowNumber=0;

        if(savedTransactionsComboBox.getItemCount()>0){
            removeTransactionButton.setEnabled(true);
        }

        KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        fm.getActiveWindow().requestFocusInWindow();
    }

    private void alignTableCells() {

        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(350);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(200);

        //left align column header
        HeaderAlignmentRenderer headerAlignmentRenderer = new HeaderAlignmentRenderer(SwingConstants.LEFT);
        itemsTable.getColumnModel().getColumn(0).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(1).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(2).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(3).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(4).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(5).setHeaderRenderer(headerAlignmentRenderer);

        //Renderer for text cells: product and units in columns 1 and 3
        DefaultTableCellRenderer leftCellRenderer = new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                //super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    setBackground(tableEvenRowColor);
                    setForeground(primaryTextColor);
                    //itemsTable.setSelectionBackground(Color.red);
                }
                else {
                    setBackground(Color.white);
                    setForeground(table.getForeground());
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        };
        leftCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        itemsTable.getColumnModel().getColumn(1).setCellRenderer(leftCellRenderer);
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(leftCellRenderer);

        itemsTable.getColumnModel().getColumn(0).setCellRenderer(new NumberTableCellRenderer());
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(new NumberTableCellRenderer());

        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new NumberTableCellRenderer());
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(new NumberTableCellRenderer());
    }


    //TODO: REMOVE 1,000,000 limit in price and quantity renderer in table


    @Override
    public void keyTyped(KeyEvent e) {
        //if(e.getKeyChar() != '\n' ) {
        try{
            Integer.parseInt(Character.toString(e.getKeyChar()));
            barcode += e.getKeyChar();
        }catch (NumberFormatException e1){
            System.out.println("Not a number");
            if(barcode.length()<=3){
                barcode="";
            }

        } catch (Exception e1){
            e1.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    //TODO: Use key bindings instead of keylisteners
    public void keyReleased(KeyEvent e) {
        //Add a product to the cart based on the scanned barcode

        if(e.getKeyCode() == KeyEvent.VK_ENTER && barcode.length()>3) {
            Product product = null;
            try {
                if(readyForNextTransaction) { // A new transaction, so clear the customerTextField
                    //customerFormattedTextField.setText("");
                    customerFormattedTextField.setText(null);
                }
                product = mAcess.getProduct(barcode);
                toModify = true; // troubleshoot this could be causing hanging issue
                addItemToCart(product);
                removeTransactionButton.setEnabled(false); // troubleshoot this could be causing hanging issue
            } catch (SQLException e1) {
                e1.printStackTrace();


               int n =  JOptionPane.showConfirmDialog(topFrame,
                        "Invalid barcode or product does not exist.\n Would you like to add it?",
                        "Invalid barcode", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

               if(n == JOptionPane.YES_OPTION){
                   AddProductDialog addProductDialog = new AddProductDialog(this,serverIP);
                   addProductDialog.setVisible(true);
                   products.clear();
                   try {
                       products.addAll(mAcess.getAllProducts());
                   } catch (Exception ex) {
                       ex.printStackTrace();
                   }

               }
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }finally {
                barcode="";
            }

        }

        else if(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_DOWN_MASK) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)){
            //Edit quantity field
            itemsTable.setFocusable(true);
            itemsTable.requestFocusInWindow();
            int lastrow = itemsTable.getRowCount()-1;
            itemsTable.editCellAt(lastrow,2);
            //System.out.println("recent " + getFocusOwner());
            itemsTable.setFocusable(false);
            //System.out.println("recent " + getFocusOwner());
            itemsTable.transferFocus();
            //System.out.println("focusowner " + getFocusOwner());


            barcode="";
        }
        else if((KeyStroke.getKeyStroke(KeyEvent.VK_C,InputEvent.CTRL_DOWN_MASK ) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)) && itemsTable.getRowCount()==0){
            //shift focus to cashformatted textfield so that change can be computed
            cashformattedTextField.setFocusable(true);
            cashformattedTextField.requestFocusInWindow();
            barcode="";
        }else if(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_DOWN_MASK) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)){
            barcode="";
            if(readyForNextTransaction && customerId==0) { // A new transaction, so clear the customerTextField
                customerFormattedTextField.setText("");
                cashformattedTextField.setValue(null);
                changeformattedTextField.setText("");
                changeformattedTextField.setBackground(Color.white);
                invoiceTextField.setBackground(Color.white);
                receiptTextField.setText("");
                totalFormattedTextField.setText("");
                creditCheckBox.setSelected(false);
                invoiceTextField.setText("");
                readyForNextTransaction=false;
            }else if(readyForNextTransaction && customerId!=0){// do not reset the customertextfield
                cashformattedTextField.setValue(null);
                changeformattedTextField.setText("");
                changeformattedTextField.setBackground(Color.white);
                invoiceTextField.setBackground(Color.white);
                receiptTextField.setText("");
                totalFormattedTextField.setText("");
                creditCheckBox.setSelected(false);
                invoiceTextField.setText("");
                readyForNextTransaction=false;
            }

            productsDialog = new ProductDialog(this, "Products", products,serverIP);
            productsDialog.setSize(new Dimension(700,500));
            productsDialog.setVisible(true);

            Product product = productsDialog.getSelectedProduct();
            //int lastrow = -1;
            lastrow = -1;
            if(product != null) {
                lastrow = addItemToCart(product);
                removeTransactionButton.setEnabled(false);
            }

            itemsTable.setFocusable(true);
            itemsTable.requestFocusInWindow();
            itemsTable.editCellAt(lastrow,2);

            JFormattedTextField ftf = (JFormattedTextField) itemsTable.getCellEditor(lastrow,2).getTableCellEditorComponent(itemsTable,1,false,lastrow,2);
            ftf.setText("");

            itemsTable.setFocusable(false);
            itemsTable.transferFocus();
            barcode="";

        } else {

        }
    }

    private int addItemToCart(Product product) {
        try {
            if(grandTotal==0){
                //new transaction: reset totalFormattedTextField
                receiptTextField.setText("");
                cashformattedTextField.setText("");
                changeformattedTextField.setText("");
                changeformattedTextField.setBackground(Color.WHITE);
                changeformattedTextField.setForeground(Color.BLACK);
            }
            int productId = product.getProductId();
            rowNumber++;
            Double quantity = 1.0;
            Double totalPrice = quantity*product.getPrice();
            Object[] r1 = {rowNumber, product.getProductName(), quantity, product.getUnits(),
                    product.getPrice(),totalPrice ,productId};
            itemsTableModel.addRow(r1);
            grandTotal += totalPrice;
            totalFormattedTextField.setValue(grandTotal);
        }catch (NullPointerException ex){
            System.out.println("NullPointerException: product not selected!");

        }catch (ClassCastException ex){
            System.out.println("Unknown product!");
        } finally {
            //clearFields();
            //barcode="";
        }
        return  rowNumber-1;
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column != -1 && column !=5 && toModify){//-1 to avoid updating non existent column when adding first row;
            // -5 to prevent cyclic updates when total column is updated (leading to stackoverflow error)
            ItemsTableModel model = (ItemsTableModel)e.getSource();
            //int quantity = (int) model.getValueAt(row, 2);
            Double quantity = (Double) model.getValueAt(row, 2);
            Double price = (Double) model.getValueAt(row,4);
            Double newPrice = 0.0;
            Double newTotal = 0.0;

            try {
                int productId = (int)model.getValueAt(row,6);
                Product product = mAcess.getProduct(productId);
                if(quantity < 1) {
                    if(column !=4) {
                        newPrice = quantity * product.getPrice();
                    }else{
                        newPrice = (Double) model.getValueAt(row,4);
                    }
                    toModify = false;
                    newTotal = newPrice;
                    model.setValueAt(newPrice, row, 4);
                }else {
                    toModify = false;
                    if(column !=4) {
                        //model.setValueAt(product.getPrice(), row, 4);
                        //newTotal = quantity * product.getPrice();
                        double pricex = (Double) model.getValueAt(row, 4);
                        newTotal = quantity * pricex;
                    }else {
                        //model.setValueAt(newPrice,row,4);
                        newTotal = quantity * price;
                    }

                }

                model.setValueAt(newTotal,row,5);
                grandTotal = computeGrandTotal();
                totalFormattedTextField.setValue(grandTotal);

            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }finally {
                KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                fm.getActiveWindow().requestFocusInWindow();
            }
//            //TODO: if there is an exception what happens to this block
//            model.setValueAt(newTotal,row,5);
//            grandTotal = computeGrandTotal();
//            totalFormattedTextField.setValue(grandTotal);
        }
        toModify = true;
    }

    private Double computeGrandTotal(){
        Double gtotal = 0.0;
        for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
            gtotal += (Double) row.elementAt(5);
        }
        return  gtotal;
    }

    public static final class ProductsTextFilterator implements TextFilterator<Product> {


        @Override
        public void getFilterStrings(List<String> baseList, Product product) {
            final String productName = product.getProductName();
            baseList.add(productName);
        }
    }

    public class SubmitAction extends AbstractAction {

        public SubmitAction(String text){
            //super(text);
            //putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            //if(itemsTable.getRowCount()>0 && barcode=="" && !creditCheckBox.isSelected()){
            if(itemsTable.getRowCount()>0 && barcode.length()<=3 && !creditCheckBox.isSelected()){
                //shift focus to cashformatted textfield so that change can be computed
                cashformattedTextField.setFocusable(true);
                cashformattedTextField.requestFocusInWindow();

                cashformattedTextField.setText("");
                //changeformattedTextField.setText("");
                barcode="";
            }else if(itemsTable.getRowCount()>0 && barcode=="" && creditCheckBox.isSelected()){ //Item sold on credit
                submitTransaction(InvoiceStatus.unpaid);
                barcode="";
            }
        }
    }

    private ArrayList<Item>  saveTransactionDetails(){
        ArrayList<Item> items = new ArrayList<>();
        for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
            Item item = new Item();
            item.setProductName((String)row.elementAt(1));
            item.setQuantity((Double)row.elementAt(2));
            item.setUnits((String)row.elementAt(3));
            item.setPrice((Double) row.elementAt(4));
            item.setProductId((int)row.elementAt(6));
            item.computeTotalPrice();
            items.add(item);
        }
        return items;
    }

    private void submitTransaction(InvoiceStatus invoiceStatus) {
        JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
        try {


            for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
                Item item = new Item();
                item.setProductName((String)row.elementAt(1));
                item.setQuantity((Double)row.elementAt(2));
                item.setUnits((String)row.elementAt(3));
                item.setPrice((Double) row.elementAt(4));
                item.setProductId((int)row.elementAt(6));
                //item.setTotalPrice((Double)row.elementAt(5));
                item.computeTotalPrice();
                customerTransaction.addItem(item);
            }

            double amount = (Double)totalFormattedTextField.getValue();

            customerTransaction.setCustomerId(customerId);
            customerTransaction.setAmount(amount);

            if(customerId !=0) { //TODO: This is to prevent null pointer exception if customer is not selected. Need to improve this conditional
                Customer customer = mAcess.getCustomer(customerId);
                receiptHeader.setFirstname(customer.getFirstname());
                receiptHeader.setLastname(customer.getLastname());
            }
            DocType docType;

            if(!creditCheckBox.isSelected()) {//A cash transaction. It May also be a partial payment
                double cash_received = (double)getFormattedTextFieldValue(cashformattedTextField);
                double change = (Double)changeformattedTextField.getValue();


                int invoiceId = mAcess.generateInvoice(customerTransaction,invoiceStatus);

                Receipt receipt = new Receipt();
                receipt.setCashReceived(cash_received);
                receipt.setChange(change);



                receiptHeader.setCash(cash_received);
                receiptHeader.setChange(change);

                double balance = cash_received < amount ?  amount-cash_received : 0.0;
                //TODO: resolve this duplication of Receipt and ReceiptHeader. Also should balance be placed in CustomerTransaction
                receipt.setBalance(balance);

                receipt.setInvoice_id(invoiceId);

                //TODO: retest this as 'current' is not being used
                //receipt.setReceiptType(ReceiptType.current);
                receipt.setReceiptType(ReceiptType.arrears);

                receipt.setCustomerId(customerId);
                int receiptId = mAcess.generateReceipt(receipt);
                receiptHeader.setReceiptNumber(receiptId);
                receiptHeader.setBalance(balance);
                docType = DocType.RECEIPT;
                receiptTextField.setText(Integer.toString(receiptId));




            }else{//Sold on credit, print only invoice

                int n = JOptionPane.showConfirmDialog(topFrame,
                        "Do you want to continue with this transaction?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);

                docType = DocType.INVOICE;
                if(n==JOptionPane.YES_OPTION) {
                    int invoiceId = mAcess.generateInvoice(customerTransaction, invoiceStatus);
                    receiptHeader.setInvoiceNumber(invoiceId);
                    invoiceTextField.setText(Integer.toString(invoiceId));
                    invoiceTextField.setBackground(accentColor);
                }else{
                    return;
                }
            }

            ((ItemsTableModel) itemsTable.getModel()).clearTable();

            PrinterJob job = PrinterJob.getPrinterJob();
            PageFormat pf = job.defaultPage();
            Paper paper = new Paper();
            double margin = 3.6; // 1 tenth of an inch
            paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                    - margin * 2);
            pf.setPaper(paper);

            job.setPrintable(new Printer(customerTransaction, receiptHeader,docType),pf);

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
            //reset variables for next transaction
            grandTotal = 0.0;
            customerTransaction = new CustomerTransaction(userName);
            rowNumber=0;
            customerId=0; //reset customerid
            //TODO: remove these resets after removing the global definition of receiptHeader
            receiptHeader.setFirstname(null);
            receiptHeader.setLastname(null);
            receiptHeader.setChange(0.0);
            receiptHeader.setCash(0.0);
            receiptHeader.setBalance(0.0);
            readyForNextTransaction=true;
            if(savedTransactionsComboBox.getModel().getSize()>0){
                removeTransactionButton.setEnabled(true);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    class HeaderAlignmentRenderer implements TableCellRenderer {
        private int horizontalAlignment = SwingConstants.LEFT;
        public HeaderAlignmentRenderer(int horizontalAlignment) {
            this.horizontalAlignment = horizontalAlignment;
        }
        @Override public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            TableCellRenderer r = table.getTableHeader().getDefaultRenderer();
            JLabel l = (JLabel) r.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            l.setPreferredSize(new Dimension(0, 40));
            l.setHorizontalAlignment(horizontalAlignment);
            return l;
        }
    }

    public class NumberTableCellRenderer extends DefaultTableCellRenderer {

        public NumberTableCellRenderer() {
            setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = NumberFormat.getNumberInstance().format(value);
            }
            if (row % 2 == 0) {
                setBackground(tableEvenRowColor);
                setForeground(primaryTextColor);
            }
            else {
                setBackground(Color.white);
                setForeground(table.getForeground());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    public static void main(String[] args)  {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        userName = args[0];
        UI frame = new UI("Sales Partner" + " - " + userName);
        frame.setContentPane(frame.mainPanel);
        frame.addKeyListener(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(200,400));
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h, Color color){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, color, null);
        g2.dispose();
        return resizedImg;
    }

    private class ListSelectionHandler implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (e.getValueIsAdjusting()) {
                return;
            }

            removeTransactionButton.setEnabled(true);
        }
    }

    private ArrayList<Customer> getCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            customers = mAcess.getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }
    //https://stackoverflow.com/questions/15025092/border-with-rounded-corners-transparency
    class TextBubbleBorder extends AbstractBorder {

        private Color color;
        private int thickness = 4;
        private int radii = 8;
        private int pointerSize = 7;
        private Insets insets = null;
        private BasicStroke stroke = null;
        private int strokePad;
        private int pointerPad = 4;
        private boolean left = true;
        RenderingHints hints;

//        TextBubbleBorder(
//                Color color) {
//            this(color, 4, 8, 7);
//        }

        TextBubbleBorder(
                Color color, int thickness, int radii, int pointerSize, Insets insets) {
            this.thickness = thickness;
            this.radii = radii;
            this.pointerSize = pointerSize;
            this.color = color;

            stroke = new BasicStroke(thickness);
            strokePad = thickness / 2;

            hints = new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int pad = radii + strokePad;
            int bottomPad = pad + pointerSize + strokePad;
            //insets = new Insets(pad, pad, bottomPad, pad);
            //insets = new Insets(20, 1, 5, 1);
            this.insets = insets;
        }

//        TextBubbleBorder(
//                Color color, int thickness, int radii, int pointerSize, boolean left) {
//            this(color, thickness, radii, pointerSize);
//            this.left = left;
//        }

        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }



        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            return getBorderInsets(c);
        }


        @Override
        public void paintBorder(
                Component c,
                Graphics g,
                int x, int y,
                int width, int height) {

            Graphics2D g2 = (Graphics2D) g;

            int bottomLineY = height - thickness - pointerSize;

            RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(
                    0 + strokePad,
                    0 + strokePad,
                    width - thickness,
                    bottomLineY,
                    radii,
                    radii);

            Polygon pointer = new Polygon();

            if (left) {
                // left point
                pointer.addPoint(
                        strokePad + radii + pointerPad,
                        bottomLineY);
                // right point
                pointer.addPoint(
                        strokePad + radii + pointerPad + pointerSize,
                        bottomLineY);
                // bottom point
                pointer.addPoint(
                        strokePad + radii + pointerPad + (pointerSize / 2),
                        height - strokePad);
            } else {
                // left point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad),
                        bottomLineY);
                // right point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad + pointerSize),
                        bottomLineY);
                // bottom point
                pointer.addPoint(
                        width - (strokePad + radii + pointerPad + (pointerSize / 2)),
                        height - strokePad);
            }

            Area area = new Area(bubble);
            area.add(new Area(pointer));

            g2.setRenderingHints(hints);

            // Paint the BG color of the parent, everywhere outside the clip
            // of the text bubble.
            Component parent = c.getParent();
            if (parent != null) {
                Color bg = parent.getBackground();
                Rectangle rect = new Rectangle(0, 0, width, height);
                Area borderRegion = new Area(rect);
                borderRegion.subtract(area);
                g2.setClip(borderRegion);
                g2.setColor(bg);
                g2.fillRect(0, 0, width, height);
                g2.setClip(null);
            }

            g2.setColor(color);
            g2.setStroke(stroke);
            g2.draw(area);
        }
    }

    public Object getFormattedTextFieldValue(JFormattedTextField ftf)  {

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



    }
