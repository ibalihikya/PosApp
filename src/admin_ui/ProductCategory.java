package admin_ui;

import Utilities.SettingsParser;
import authentication.SaltedMD5;
import ca.odell.glazedlists.*;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.postgresql.util.PSQLException;
import print.ReceiptHeader;
import print.StatementPrinter;
import print.SupplierStatementPrinter;
import sell_ui.ItemsTableModel;
import sell_ui.ProductDialog;
import validation.ComboNotSelectedValidator;
import validation.NotEmptyNumberValidator;
import validation.NotEmptyValidator;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class ProductCategory extends JFrame implements TableModelListener{
    //private final ItemsTableModel itemsSuppliedTableModel;
    private JPanel productCategoryPanel;
    private JTabbedPane tabbedPane;
    private JTextField categoryNametextField;
    private JTextArea  descriptiontextArea;
    private JButton    cancelButton;
    private JButton    submitButton;
    private JTextField productNametextField;
    private JTextField descriptionTextField;
    private JTextField priceTextField;
    private JTextField barcodeTextField;
    private JTextArea  commentTextArea;
    private JComboBox  categoryComboBox;
    private JComboBox  unitsComboBox;
    private JButton  submitProductAddButton;
    private JButton  cancelProductAddButton;
    private JTable categoriesTable;
    private JTable productsTable;
    private JButton refreshProductsButton;
    private JPanel productPanel;
    private JPanel categoryPanel;
    private JButton refreshCategoriesButton;
    private JPanel stockPanel;
    private JComboBox productsComboBox;
    private JTextField quantityTextField;
    private JTextArea stockCommentsTextArea;
    private JButton submitStockButton;
    private JButton cancelStockButton;
    private JTable stockTable;
    private JPanel addProductPanel;
    private JPanel salesReportsPanel;
    private JPanel leftPanel;
    private JPanel startDatePanel;
    private JPanel stopDatePanel;
    private JPanel radiobuttonPanel;
    private JRadioButton detailedRadioButton;
    private JRadioButton chartRadioButton;
    private JRadioButton summaryRadioButton;
    private JPanel chartPanel;
    private JRadioButton revenueVsProductBarRadioButton;
    private JRadioButton revenueVsProductPieRadioButton;
    private JRadioButton revenueLineRadioButton;
    private JRadioButton revenueByProductLineRadioButton;
    private JRadioButton quantityByProductLineRadioButton;
    private JList listProducts;
    private JButton searchButton;
    private JPanel totalPane;
    private JFormattedTextField totalSalesformattedTextField;
    private JPanel salesPanel;
    private JTable stockStatusTable;
    private JTextField thresholdtextField;
    private JButton deleteButton;
    private JTextField costPriceTextField;
    private JFormattedTextField marginformattedTextField;
    private JList categoryList;
    private JButton deleteCategoryButton;
    private JPanel supplierPanel;
    private JTextField supplierNameTextField;
    private JTextField addressTextField;
    private JFormattedTextField phone1FormattedTextField;
    private JFormattedTextField phone2FormattedTextField;
    private JFormattedTextField emailFormattedTextField;
    private JTextField bankTextField;
    private JTextField accountNumberTextField;
    private JPanel buttonPanel;
    private JButton addSupplierButton;
    private JButton cancelButton1;
    private JTextField invoiceTextField;
    private JTable additemsTable;
    private JButton submitInvoiceButton;
    private JButton cancelRegistrationButton;
    private JButton addItemButton;
    private JPanel addSupplierPanel;
    private JTable suppliersTable;
    private JComboBox supplierComboBox2;
    private JFormattedTextField amountOwedFormattedTextField;
    private JFormattedTextField paymentAmountFormattedTextField;
    private JButton payButton;
    private JButton cancelPaymentButton;
    private JTable itemsSuppliedTable;
    private JPanel itemsSuppliedPanel;
    private JScrollPane deliveriesTableScrollPane;
    private JTextField paymentDescriptionTextField;
    private JPanel rightPanel;
    private JComboBox productsSalesComboBox;
    private JButton deleteSalesButton;
    private JButton clearTableSelectionButton;
    private JTextField filterTextField;
    private JPanel productsDisplayPanel;
    private JPanel leftxPanel;
    private JPanel rightxPanel;
    private JPanel filterPanel;
    private JPanel rightStockPanel;
    private JButton deleteStockAddButton;
    private JTextField filterStockTextField;
    private JTextField filterDeliveriesTextField;
    private JPanel topRightPanel;
    private JPanel leftCustomerPanel;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField birthdayTextField;
    private JTextField emailTextField;
    private JTextField phone1TextField;
    private JTextField phone2TextField;
    private JTextField addressLocationTextField;
    private JComboBox sexComboBox;
    private JButton addCustomerButton;
    private JButton cancelButton4;
    private JTextField filterCustomerTextField;
    private JTable customersTable;
    private JTextField transactionsFilterTextField;
    private JComboBox customerComboBox;
    private JTable customerStatementTable;
    private JPanel rightCustomerPanel;
    private JPanel transactionsPanel;
    private JPanel transactionsDetailPanel;
    private JTable transactionsDetailTable;
    private JPanel leftStockPanel;
    private JTable distributionTable;
    private JTextField distributionTextField;
    private JButton printStatementButton;
    private JPanel addCustomerPanel;
    private JPanel supplierTransactionsPanel;
    private JTable supplierTransactionsTable;
    private JTextField supplyTransactionsFiltertextField;
    private JTextField statementFilterTextField;
    private JTable invoiceTable;
    private JButton receiveGoodsButton;
    private JButton adjustStockButton;
    private JButton paymentsButton;
    private JButton deleteCustomerButton;
    private JButton deleteItemButton;
    private JButton deleteTransactionButton;
    private JButton deleteSupplierButton;
    private JTextField supplierFilterTextField;
    private JButton unpackButton;
    private JPanel register_panel;
    private JTextField userNameTextField;
    private JCheckBox adminCheckBox;
    private JButton registerButton;
    private JPasswordField passwordField;
    private JTextField user_lastNameTextField;
    private JTextField user_firstNameTextField;
    private JTextField userFilterTextField;
    private JTable usersTable;
    private JButton viewRefundsButton;
    private JTable refundsTable;
    private JButton addUnitsButton;
    private JPasswordField confirmpasswordField;
    private JLabel errorLabel;
    private JButton changePasswordButton;
    private JPanel userAccountsPanel;
    private JPanel rightTopPanel;
    private JPanel leftTopStockPanel;
    private JPanel stockFilterPanel;
    private JPanel stockTransactionsPanel;
    private JPanel leftBottomStockPanel;
    private JPanel leftSupplierPanel;
    private JPanel supplierListPanel;
    private JScrollPane suppliersScrollPane;
    private JPanel settingsPanel;
    private JPanel leftSettingsPanel;
    private JTable settingsTable;
    private JButton refreshButton;
    private JPanel locationPanel;
    private JTextField locationTextField;
    private JButton addLoctionButton;
    private JPanel locationsListPanel;
    private JList locationsList;
    private JButton removeLocationButton;
    private JPanel fromDatePanel;
    private JPanel toDatePanel;
    private JButton searchStockTransactions;
    private JPanel receiptsTopLeftPanel;
    private JComboBox cashierComboBox;
    private JComboBox tillComboBox;
    private JButton searchReceiptsButton;
    private JPanel receiptsRightPanel;
    private JTable receiptsTable;
    private JPanel startDateReceiptsPanel;
    private JPanel endDateReceiptsPanel;
    private JFormattedTextField cashFormattedTextField;
    private JPanel receiptsLeftPanel;
    private JPanel searchPanel;
    private JComboBox supplierComboBox1;
    private JButton printSupplierStatementButton;
    private JPanel startDateSupStatementPanel;
    private JPanel stopDateSupStatementPanel;
    private JPanel printPanel;
    private JPanel leftReturnsPanel;
    private JTable returnDetailsTable;
    private JPanel returnsLeftTopPanel;
    private JPanel distributionfilterPanel;
    private JButton refreshButton1;
    private JButton refreshReturnsButton;
    private JButton viewDeletedSalesButton;
    private JFormattedTextField deletedSalesFormattedTextField;
    private JLabel keyLabel1;
    private JLabel keyLabel2;
    private JCheckBox vatCheckBox;
    private JPanel costPanel;
    private JPanel unitsPanel;
    private JButton applyVATButton;
    private JFormattedTextField vatFormattedTextField;
    private JButton deleteReceiptButton;
    private JPanel startDateCustStatementPanel;
    private JPanel stopDateCustStatementPanel;
    private JButton exportButton;
    private JButton deleteUserButton;
    private JButton editCustomerButton;
    private JScrollPane productsScrollPane;
    private JDialog parent;
    private static MySqlAccess mAcess;
    //TODO: fix concurrency workaround
   // private  MySqlAccess mAcess2; //tentaive solution to concurrency issue on populating comboboxes with getproducts()
    //private static  AutoCompleteSupport support;
    private int productId;
    private int categoryId;
    ProductDialog productsDialog;
    private final EventList products;
    private static EventList itemsEventList;
    private int rowNumber = 0;
    private Transaction transaction;
    private int stockTransactionId;
    private JTable productsTable2;
    private static EventList<Product> eventListProducts;
    private static EventList<Supplier> eventListSuppliers;
    private static EventList<Customer> eventListCustomers;
    private static EventList<StockItem> stockItemsEventList;
    private SettingsParser settingsParser;
    private String serverIp ="";
    private static String loggedInUser;
    //EventList<Product> products2 = new BasicEventList();


    private boolean tab1Loaded = false;
    private boolean tab2Loaded = false;
    private boolean tab3Loaded = false;
    private boolean tab4Loaded = false;
    private boolean tab5Loaded = false;
    private boolean tab6Loaded = false;
    private boolean tab7Loaded = false;
    private boolean tab8Loaded = false;




    private String[] productsTableColumnNames = {
            "ProductId",
            "Product Name",
            "Additional Info.",
            "Category",
            "Cost price",
            "Sell Price",
            "Mark up",
            "Units",
            "Threshold",
            "Barcode",
            "Date Created",
            "Last Modified Date",
            "Comments"
    };
    private String[] categoryTableColumnNames = {
            "Category Name",
            "Description"
    };

    private String[] stockTableColumnNames = {
        "Product Id",
        "Product Name",
        "Quantity Added",
        "Date Added",
        "Date Modified",
            "Transaction Id",
        "Comment"
    };


    //Sales Report variables
    private static JFXPanel datePickerFXPanel;
    private static JFXPanel stopDatePickerFXPanel;
    private static JFXPanel startDatePickerSupStatementFXPanel;
    private static JFXPanel stopDatePickerSupStatementFXPanel;
    private static JFXPanel startDatePickerCustStatementFXPanel;
    private static JFXPanel stopDatePickerCustStatementFXPanel;
    private static JFXPanel chartFxPanel;
    private static DatePicker startDatePicker;
    private static DatePicker stopDatePicker;
    private static DatePicker startDateSupStatementPicker;
    private static DatePicker stopDateSupStatementPicker;
    private static DatePicker startDateCustStatementPicker;
    private static DatePicker stopDateCustStatementPicker;


    private static JFXPanel fromdatePickerFXPanel;
    private static JFXPanel  toDatePickerFXPanel;
    private static DatePicker fromDatePicker;
    private static DatePicker toDatePicker;

    private static JFXPanel startdateReceiptsPickerFXPanel;
    private static JFXPanel  endDateReceiptsPickerFXPanel;
    private static DatePicker startdateReceiptsDatePicker;
    private static DatePicker endDateReceiptstoDatePicker;

    private final JTable salesTable;

    private String[] salesTableColumnNames = {
            "Item no",
            "Product Name",
            "Quantity",
            "Price",
            "Total",
            "VAT",
            "Margin",
            "Invoice no.",
            "Customer",
            "status",
            "Sold by",
            "Till",
            "Date",
            "ProductId"

    };

    private String[] salesSummaryTableColumnNames = {
            "Product Name",
            "Quantity",
            "Total",
            "Margin"
    };

    private String[] stockStatusTableColumnNames = {
            "Product no.",
            "Product Name",
            "Quantity Available",
            "Threshold",
            "Last Changed Date"
    };



    private String[] deliveryTableColumnNames = {
            "Delivery Id",
            "Supplier",
            "Product",
            "Quantity",
            "Price",
            "Total Amount",
            "Invoice",
            "Time"
    };

    private static CategoryAxis xAxis;
    private static NumberAxis yAxis;
    private static BarChart<String,Number> bc;

    public ProductCategory(String title) {
        super(title);
        settingsParser = new SettingsParser("settings.xml");
        serverIp=settingsParser.getServerIp();
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);
        parent = new JDialog(); // for validation error messages


        deleteTransactionButton.setVisible(false);
        deleteItemButton.setVisible(false);
        //customerComboBox.setVisible(false);
        //printStatementButton.setVisible(false);

        marginformattedTextField.setEditable(false);
        cashFormattedTextField.setEditable(false);
        deletedSalesFormattedTextField.setEditable(false);

       leftxPanel.setBackground(new Color(214,217,223));
       addProductPanel.setBackground(new Color(214,217,223));
       keyLabel1.setForeground(Color.magenta);
       keyLabel2.setForeground(Color.red);

//       unpackButton.setVisible(false);//getting rid of unpack functionality, can be done with distribute and adjust
//       unpackButton.setEnabled(false);//getting rid of unpack functionality, can be done with distribute and adjust

        java.net.URL imgURL = getClass().getResource("/images/ic_trending_up_red_48dp.png");
        Image logoImage = Toolkit.getDefaultToolkit().getImage(imgURL);
        setIconImage(logoImage);


        salesTable = new JTable();
        salesTable.setFillsViewportHeight(true);
        salesTable.setBackground(Color.WHITE);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(189, 221, 255));

        //stockTable.getSelectionModel().addListSelectionListener(new StockTableRowListener());
        stockTable.setRowSelectionAllowed(true);

        customerStatementTable.getSelectionModel().addListSelectionListener(new CustomerStatementTableRowListener());
        customerStatementTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suppliersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        distributionTable.setRowSelectionAllowed(true);
        distributionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


        itemsEventList = new BasicEventList();
        products = new BasicEventList();
        try {
            products.addAll(mAcess.getAllProducts());
        } catch (Exception e) {
            e.printStackTrace();
        }



        productsDialog = new ProductDialog(null, "Products", products,serverIp);
        productsDialog.setSize(new Dimension(700,500));

        categoryList.addListSelectionListener(new ListSelectionHandler());
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        datePickerFXPanel = new JFXPanel();
        datePickerFXPanel.setPreferredSize(new Dimension(20,20));

        stopDatePickerFXPanel = new JFXPanel();
        stopDatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        startDatePickerSupStatementFXPanel = new JFXPanel();
        startDatePickerSupStatementFXPanel.setPreferredSize(new Dimension(20,20));

        stopDatePickerSupStatementFXPanel = new JFXPanel();
        stopDatePickerSupStatementFXPanel.setPreferredSize(new Dimension(20,20));

        startDatePickerCustStatementFXPanel = new JFXPanel();
        startDatePickerCustStatementFXPanel.setPreferredSize(new Dimension(20,20));

        stopDatePickerCustStatementFXPanel = new JFXPanel();
        stopDatePickerCustStatementFXPanel.setPreferredSize(new Dimension(20,20));

        fromdatePickerFXPanel = new JFXPanel();
        fromdatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        toDatePickerFXPanel = new JFXPanel();
        toDatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        startdateReceiptsPickerFXPanel = new JFXPanel();
        startdateReceiptsPickerFXPanel.setPreferredSize(new Dimension(20,20));

        endDateReceiptsPickerFXPanel = new JFXPanel();
        endDateReceiptsPickerFXPanel.setPreferredSize(new Dimension(20,20));

        startDatePanel.add(datePickerFXPanel);
        stopDatePanel.add(stopDatePickerFXPanel);

        fromDatePanel.add(fromdatePickerFXPanel);
        toDatePanel.add(toDatePickerFXPanel);

        startDateReceiptsPanel.add(startdateReceiptsPickerFXPanel);
        endDateReceiptsPanel.add(endDateReceiptsPickerFXPanel);

        startDateSupStatementPanel.add(startDatePickerSupStatementFXPanel);
        stopDateSupStatementPanel.add(stopDatePickerSupStatementFXPanel);

        startDateCustStatementPanel.add(startDatePickerCustStatementFXPanel);
        stopDateCustStatementPanel.add(stopDatePickerCustStatementFXPanel);

        errorLabel.setVisible(false);
        errorLabel.setText("");

        thresholdtextField.setText("1");

        usersTable.setRowSelectionAllowed(true);

        distributionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);


                TableModel model = distributionTable.getModel();
//                double receivedQuantity =(double)model.getValueAt(row,2);
//                double storeQuantity =(double)model.getValueAt(row,3);
//                double shopQuantity =(double)model.getValueAt(row,4);
                double lowThreshold =(double)model.getValueAt(row,3);

                //double totalQuantity = receivedQuantity + storeQuantity + shopQuantity;
                double totalQuantity = (double)model.getValueAt(row,2);;

                if (totalQuantity<=lowThreshold) {
                    setBackground(Color.ORANGE);
                    setForeground(Color.black);
                }else if(row % 2 == 0) {
                    setBackground(Color.white);
                    setForeground(Color.black);
                }else {
                    setBackground(new Color(242,242,242));
                    setForeground(table.getForeground());
                }
                return this;
            }
        });

//        customerStatementTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
//            @Override
//            public Component getTableCellRendererComponent(JTable table,
//                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {
//
//                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
//
//
//                TableModel model = customerStatementTable.getModel();
//                String transactionType =(String)model.getValueAt(row,3);
//
//
//                if (transactionType.equals("Del")) {
//                    setBackground(Color.ORANGE);
//                    setForeground(Color.black);
//                }else if(row % 2 == 0) {
//                    setBackground(Color.white);
//                    setForeground(Color.black);
//                }else {
//                    setBackground(new Color(242,242,242));
//                    setForeground(table.getForeground());
//                }
//                return this;
//            }
//        });

        //TODO: refactor this code with a single populateTable() function

        populateProductsTable();


          populateCategoriesListBox();

        supplierTransactionsTable.getSelectionModel().addListSelectionListener(new SupplierTransactionTableRowListener());
        refundsTable.getSelectionModel().addListSelectionListener(new ReturnsTableRowListener() );


        //https://stackoverflow.com/questions/14852719/double-click-listener-on-jtable-in-java
        distributionTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(stockPanel);
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    TableModel model = table.getModel();
                    int productId = (int)model.getValueAt(row,0);
                    String productName = (String)model.getValueAt(row,1);
                    Distributor distributor = new Distributor(productId,loggedInUser,serverIp);
                    distributor.setProductName(productName);

                    DistributeDialog distributionDialog = new DistributeDialog(topFrame, "Distribution and Adjustment" + " - "
                            + distributor.getProductName(),
                            distributor,stockItemsEventList,loggedInUser, serverIp);
                    distributionDialog.setVisible(true);

                    populateDistributionTable();
                    //populateStockTable2();
                }
            }
        });

//        createItemSuppliedTableModelListener();



        eventListProducts = GlazedLists.eventList(getProducts2());
        eventListSuppliers = GlazedLists.eventList(getSuppliers());
        eventListCustomers = GlazedLists.eventList(getCustomers());

       Thread thread = new Thread(new Runnable() {
           @Override
           public void run() {
               try {
                   populateComboBox2(productsSalesComboBox,eventListProducts,new ProductsTextFilterator());
                   populateComboBox2(customerComboBox,eventListCustomers,new CustomerTextFilterator());
               } catch (InvocationTargetException e) {
                   e.printStackTrace();
               }
           }
       });
       thread.start();



        //input validation
        categoryNametextField.setInputVerifier(new NotEmptyValidator(parent,categoryNametextField,"Enter category. (Press ESC to clear this message)" ));
        productNametextField.setInputVerifier(new NotEmptyValidator(parent,productNametextField,"Enter product name. (Press ESC to clear this message)"));
        priceTextField.setInputVerifier(new NotEmptyNumberValidator(parent,priceTextField, "Invalid price. (Press ESC to clear this message)"));
        costPriceTextField.setInputVerifier(new NotEmptyNumberValidator(parent,costPriceTextField, "Invalid price (Press ESC to clear this message)"));
        unitsComboBox.setInputVerifier(new ComboNotSelectedValidator(parent,unitsComboBox, "Select a unit. (Press ESC to clear this message)"));

        //refresh products and categories tables
        RefreshAction refreshAction = new RefreshAction("Refresh", new Integer(KeyEvent.VK_R));

//        //reset form fields when cancel button is clicked
          CancelAction cancelAction = new CancelAction("Cancel", new Integer(KeyEvent.VK_C));
         cancelButton.setAction(cancelAction);            //cancel button in category panel
         cancelProductAddButton.setAction(cancelAction); //cancel button in product panel

        populateCategoriesComboBox();

        ArrayList<Unit> units = mAcess.getAllUnits();
        ComboBoxModel cbUnitsModel = new DefaultComboBoxModel(units.toArray());
        unitsComboBox.setModel(cbUnitsModel);
        unitsComboBox.setSelectedIndex(-1);

        //add a category to the database
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String categoryName = categoryNametextField.getText().trim();
                    String description = descriptiontextArea.getText().trim();
                    Category category = new Category(categoryName);
                    category.setDescription(description);
                    if(submitButton.getText() =="Add") {
                        mAcess.createProductCategory(category);
                    }else{
                        category.setCategoryId(categoryId);
                        mAcess.updateCategory(category);
                        submitButton.setText("Add");
                        submitButton.setBackground(new Color(214,217,223));
                    }
                    resetFormFields(categoryPanel);
                    populateCategoriesComboBox();
                    populateCategoriesListBox();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        //add a product to the database
        submitProductAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "Do you want to continue with this action?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    Category category;
                    if (categoryComboBox.getSelectedIndex() == -1) {
                        category = new Category("");
                    } else {
                        category = (Category) categoryComboBox.getSelectedItem();
                    }

                    try {

                        Product product = new Product();
                        product.setProductName(productNametextField.getText().trim());
                        product.setCategory(category);
                        product.setDescription(descriptionTextField.getText().trim());
                        double costPrice = Double.parseDouble(costPriceTextField.getText().trim());
                        double sellingPrice = Double.parseDouble(priceTextField.getText().trim());
                        if(costPrice>sellingPrice){
                            JOptionPane.showMessageDialog(null,
                                    "Cost price is greater than selling price","Invalid price", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        product.setCostprice(costPrice);
                        product.setVatable(vatCheckBox.isSelected());
                        product.setPrice(sellingPrice);
                        product.setUnits(((Unit) unitsComboBox.getSelectedItem()).getUnitName());
                        product.setComment(commentTextArea.getText().trim());
                        product.setBarcode(barcodeTextField.getText().trim());
                        product.setStockLowThreshold(Double.parseDouble(thresholdtextField.getText().trim()));
                        if (submitProductAddButton.getText() == "Add") {
                            mAcess.insertProduct(product);
                        } else {
                            product.setProductId(productId);
                            mAcess.updateProduct(product);
                        }
                        resetFormFields(addProductPanel);
                        resetFormFields(unitsPanel);
                        resetFormFields(costPanel);
                        thresholdtextField.setText("1");
                        refreshProductsTable();
                        populateDistributionTable(); // refresh dist table
                        submitProductAddButton.setText("Add");
                        eventListProducts.clear();
                        eventListProducts.addAll(getProducts2());
                        products.clear();
                        products.addAll(mAcess.getAllProducts());

                    } catch (NumberFormatException ex) {
                        System.out.println("Invalid price!");
                    } catch (NullPointerException ex) {
                        System.out.println("Units not selected!");
                    } catch (PSQLException ex) {
                        System.out.println("Product name already exists");
                        ex.printStackTrace();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    } finally {

                    }
                }
            }
        });




        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == 0){
                    ((NotEmptyValidator)productNametextField.getInputVerifier()).setPopUpVisible(false);
                   // ((NotEmptyValidator)descriptionTextField.getInputVerifier()).setPopUpVisible(false);
                    ((NotEmptyNumberValidator)priceTextField.getInputVerifier()).setPopUpVisible(false);
                    ((ComboNotSelectedValidator)unitsComboBox.getInputVerifier()).setPopUpVisible(false);
                    ((NotEmptyValidator)categoryNametextField.getInputVerifier()).setPopUpVisible(false);

                }
//                else if(tabbedPane.getSelectedIndex() == 1){
//                    ((ComboNotSelectedValidator)productsComboBox.getInputVerifier()).setPopUpVisible(false);
//                    ((NotEmptyNumberValidator)quantityTextField.getInputVerifier()).setPopUpVisible(false);
//                }
            }
        });

        //**********************stock status report event handlers***********************


        //*****************Sales Report event handlers***********************************
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if(detailedRadioButton.isSelected()==true) {
                        //show detailed view
                        populateDetailedSalesTable();

                        formatDetailedSalesTable();

                    }else if(summaryRadioButton.isSelected()){
                        //show summary view
                        if(productsSalesComboBox.getSelectedIndex() == -1){
                            productsSalesComboBox.setSelectedIndex(0);
                        }

                        String startDate = startDatePicker.getValue().toString();
                        String stopDate = stopDatePicker.getValue().toString();
                        Double deletedSalesTotal = 0.0;

                        Product product = (Product)productsSalesComboBox.getSelectedItem();
                        ArrayList<Item> items = null;
                        if(product.getProductName() !=null){
                            items = mAcess.getItemsSoldSummary(startDate,
                                    stopDate,product.getProductId() );
                            deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate,product.getProductId());
                        }else {
                            items = mAcess.getItemsSoldSummary(startDate,
                                    stopDate);
                            deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate);
                        }


                        Object[][] items2D = new Object[items.size()][];
                        int i = 0;
                        Double totalSales = 0.0;
                        Double totalMargin = 0.0;
                        Double totalVat = 0.0;
                        for (Item item : items) {
                            items2D[i] = item.toArray_summary();
                            totalSales += item.getTotalPrice();
                            totalVat += item.getVat_amount();
                            totalMargin += item.getMargin();
                            i++;
                        }
                        totalSalesformattedTextField.setValue(totalSales);
                        marginformattedTextField.setValue(totalMargin);
                        vatFormattedTextField.setValue(totalVat);
                        deletedSalesFormattedTextField.setValue(deletedSalesTotal);
                        salesTable.setModel(new ItemsTableModel(items2D, salesSummaryTableColumnNames));
                        JScrollPane scrollPane = new JScrollPane(salesTable);
                        salesPanel.removeAll();
                        salesPanel.add(scrollPane);

                        //TODO: find a solution for the display quirk; tentatively solved below. Perhaps run on a separate thread: platform.runlater()
                        salesPanel.setVisible(false);
                        salesPanel.setVisible(true);
                    }else {
                        //chart view
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //salesPanel.remove(scrollPane);
                                ArrayList<Product> products = new ArrayList<>();
                                try {
                                    Product product = (Product)(listProducts.getSelectedValue());
                                    products = (ArrayList<Product>) listProducts.getSelectedValuesList();
                                }catch (NullPointerException e1){
                                    System.out.println("product not selected");
                                }catch (ClassCastException e1)
                                {
                                    System.out.println("product not selected, canot cast empty list to arrarylist");
                                }

                                if(revenueVsProductBarRadioButton.isSelected()) {
                                    createbarChart(startDatePicker.getValue().toString(), stopDatePicker.getValue().toString());
                                }else if(revenueVsProductPieRadioButton.isSelected()){
                                    createPieChart(startDatePicker.getValue().toString(), stopDatePicker.getValue().toString());
                                }else if(revenueLineRadioButton.isSelected()){
                                    createLineChartForAllProducts(startDatePicker.getValue().toString(), stopDatePicker.getValue().toString());
                                }else if(revenueByProductLineRadioButton.isSelected() && products !=null){
                                    createLineChart_Revenue_GivenProduct(startDatePicker.getValue().toString(), stopDatePicker.getValue().toString(), products);
                                }else if(quantityByProductLineRadioButton.isSelected() && products !=null){
                                    createLineChart_QuantitySold_GivenProduct(startDatePicker.getValue().toString(),
                                            stopDatePicker.getValue().toString(), products);
                                }
                            }
                        });
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                                "All sales and stock information associated to this product will be deleted.\n" +
                                        "You must delete sales information for this product before performing this action.\n" +
                                        " Do you want to continue?",
                        "Dangerous Action", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    try {

                        //TODO: need to improve this, simply use the CASCADE option in foreign key consraints
                        //remove all the product's entries from the stock status table
                       // mAcess.deleteStockStatus(productId);

                        //remove all the stock details for the product
                        mAcess.deleteStock(productId);

                        //remove all the transactions for the product
                        //mAcess.deleteAllTransactions(productId);

                        //delete the product
                        mAcess.deleteProduct(productId);

                        refreshProductsTable();
                        resetFormFields(addProductPanel);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }finally {
                        submitProductAddButton.setText("Add");
                        //submitProductAddButton.setBackground(new Color(214,217,223));
                    }
                }
            }
        });

        deleteCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                                "This Action can not be reversed. You must delete products\n under this category" +
                                        " before performing this action. Do you want to continue?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    try {

                        mAcess.deleteCategory(categoryId);
                        populateCategoriesListBox();
                        resetFormFields(categoryPanel);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }finally {
                        submitButton.setText("Add");
                        submitButton.setBackground(new Color(214,217,223));
                    }
                }
            }
        });
        addSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    Supplier supplier = new Supplier();
                    supplier.setSupplierName(supplierNameTextField.getText());
                    supplier.setPhone1(phone1FormattedTextField.getText());
                    supplier.setPhone2(phone2FormattedTextField.getText());
                    supplier.setEmail(emailFormattedTextField.getText());
                    supplier.setAddress(addressTextField.getText());
                    supplier.setBankName(bankTextField.getText());
                    supplier.setAccountNumber(accountNumberTextField.getText());
                    mAcess.addSupplier(supplier);
                    resetFormFields(addSupplierPanel);
                    populateSupplierTable2();
                    eventListSuppliers.clear();
                    eventListSuppliers.addAll(getSuppliers());
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        cancelButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFormFields(addSupplierPanel);
            }
        });


         deleteSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                int n = JOptionPane.showConfirmDialog(null,
                        "This Action can not be reversed.\n " +
                                "Statement for registered customer(s) will be changed." +
                                "\n Do you want to continue?",
                        "Warning", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    int[] selectedRows = salesTable.getSelectedRows();
                    int [] itemIds = new int [selectedRows.length];

                    ArrayList<Item> items = new ArrayList<>();
                    for(int i=0; i<selectedRows.length; i++){
                        int itemId = (int) salesTable.getModel().getValueAt(selectedRows[i],0);
                        Item item = new Item();
                        item.setTransactionId(itemId);
                        item.setProductId((int) salesTable.getModel().getValueAt(selectedRows[i],13));
                        item.setQuantity((Double) salesTable.getModel().getValueAt(selectedRows[i],2));
                        item.setPrice((Double) salesTable.getModel().getValueAt(selectedRows[i],3));
                        item.setTotalPrice((Double) salesTable.getModel().getValueAt(selectedRows[i],4));
                        item.setInvoiceNumber((int) salesTable.getModel().getValueAt(selectedRows[i],7));
                        item.setMargin((Double) salesTable.getModel().getValueAt(selectedRows[i],6));
                        item.setTime((String) salesTable.getModel().getValueAt(selectedRows[i],12));
                        items.add(item);
                    }
                    try {
                        mAcess.deleteSales(items,loggedInUser);
                        populateDetailedSalesTable();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }finally {
                    }
                }

            }
        });
        clearTableSelectionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                productsTable.clearSelection();
            }
        });




        printStatementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Customer customer = (Customer) customerComboBox.getSelectedItem();
                int customerId = customer.getId();

                ArrayList<CustomerTransaction> customerTransactions = getCustomerTransactions(customerId,
                        startDateCustStatementPicker.getValue().toString(),stopDateCustStatementPicker.getValue().toString());
                for(CustomerTransaction customerTransaction : customerTransactions){
                    try {
                        customerTransaction.addAllItems(mAcess.getInvoiceItems(customerTransaction.getReceiptId()));
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    } catch (ClassNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }


                ReceiptHeader receiptHeader = new ReceiptHeader();
                receiptHeader.setBusinessName(settingsParser.getBusinessName());
                receiptHeader.setLocation(settingsParser.getLocation());
                receiptHeader.setTelephoneNumber1(settingsParser.getPhone1());
                receiptHeader.setTelephoneNumber2(settingsParser.getPhone2());
                receiptHeader.setTin(settingsParser.getTin());

                PrinterJob job = PrinterJob.getPrinterJob();
                PageFormat pf = job.defaultPage();
                Paper paper = new Paper();
                double margin = 3.6; // 1 tenth of an inch
                paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                        - margin * 2);
                pf.setPaper(paper);

                job.setPrintable(new StatementPrinter(customerTransactions, receiptHeader),pf);

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
        });
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Customer customer = new Customer();
                    customer.setFirstname(firstNameTextField.getText());
                    customer.setLastname(lastNameTextField.getText());
                    customer.setSex((String)sexComboBox.getSelectedItem());
                    //customer.setBarcode(barcodeTextField.getText());
                    customer.setBirthday(birthdayTextField.getText());
                    customer.setEmail(emailTextField.getText());
                    customer.setAddress(addressLocationTextField.getText());
                    customer.setPhone2(phone2TextField.getText());
                    customer.setPhone1(phone1TextField.getText());
                    mAcess.addCustomer(customer);
                    resetFormFields(addCustomerPanel);
                    eventListCustomers.clear();
                    eventListCustomers.addAll(getCustomers());

                    populateCustomersTable();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        cancelButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFormFields(addCustomerPanel);
            }
        });

        receiveGoodsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                AddItemsDialog addItemsDialog = new AddItemsDialog(eventListSuppliers, products,
                        settingsParser.getDefaultStockDestination(),stockItemsEventList,loggedInUser,serverIp);
                addItemsDialog.setMinimumSize(new Dimension(700, 600));
                addItemsDialog.setVisible(true);



                //TODO: this update is slowing down the system. Improve it
                //TODO: tables should not be updated if additemsDialog is cancelled.
                int initalRowCount = supplierTransactionsTable.getRowCount();
                populateSupplyTransactionsTable();
                int finalRowCount = supplierTransactionsTable.getRowCount();

                if(initalRowCount!=finalRowCount) {//new row added, populate other tables
                    //TODO: improve this logic. consider a table event listener or table.getModel().addTableModelListener https://stackoverflow.com/questions/15546651/listener-for-addition-deletion-of-row-in-jtable
                    //TODO or simply add an return value like a boolean to the additemsdialog to notify whether item addition was successful or not
                    populateDeliveryTable();
                    populateDistributionTable(); //refresh distribution UI table when item is received
                    populateStockTable2(getSockItems());
                }
            }
        });
        adjustStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StockAdjustmentDialog stockAdjustmentDialog = new StockAdjustmentDialog(loggedInUser,
                        eventListProducts,stockItemsEventList,serverIp);
                stockAdjustmentDialog.setVisible(true);
                populateDistributionTable();
                populateRefundsTable();
            }
        });
        paymentsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(supplierPanel);
                SupplierPaymentDialog supplierPaymentDialog = new SupplierPaymentDialog(topFrame,eventListSuppliers,serverIp);
                supplierPaymentDialog.setMinimumSize(new Dimension(400, 250));
                supplierPaymentDialog.setVisible(true);
                populateSupplyTransactionsTable();

            }
        });
        deleteCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "This will delete the customer and all associated invoices and receipts.\n Do you want to continue with this action?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    try {
                        int row = customersTable.getSelectedRow();
                        int customerId = (int) customersTable.getModel().getValueAt(row, 0);
                        mAcess.deleteCustomer(customerId);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                populateCustomersTable();
            }
        });
        deleteItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "Do you want to continue with this action?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    int row_supplyTable = itemsSuppliedTable.getSelectedRow();
                    int deliveryId = (int) itemsSuppliedTable.getModel().getValueAt(row_supplyTable, 0);
                    mAcess.deleteSuppliedItem(deliveryId);

                    int row_transactionTable = supplierTransactionsTable.getSelectedRow();
                    int transactionId = (int) supplierTransactionsTable.getModel().getValueAt(row_transactionTable, 0);
                    populateDeliveryTable(transactionId);

                    populateSupplyTransactionsTable();
                    populateDistributionTable();
                    populateStockTable2(getSockItems());
                }
            }
        });
        deleteTransactionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "Do you want to continue with this action?",
                        "Confirmation", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    int row_transactionTable = supplierTransactionsTable.getSelectedRow();
                    int transactionId = (int) supplierTransactionsTable.getModel().getValueAt(row_transactionTable, 0);
                    mAcess.deleteSupplierTransaction(transactionId);
                    populateSupplyTransactionsTable();
                    populateDeliveryTable(transactionId);
                    populateDistributionTable();
                    ArrayList stockItems = getSockItems();
                    populateStockTable2(stockItems);
                }

            }
        });
        deleteSupplierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "This will delete the supplier, associated invoices and payments.\n Do you want to continue with this action?",
                        "Warning", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    int row = suppliersTable.getSelectedRow();
                    int supplierId = (int) suppliersTable.getModel().getValueAt(row, 0);
                    mAcess.deleteSupplier(supplierId);
                    populateSupplierTable2();
                    populateDistributionTable();
                    populateStockTable2(getSockItems());
                }

            }
        });
//        unpackButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                SplitterDialog splitterDialog = new SplitterDialog(serverIp);
//                splitterDialog.setMinimumSize(new Dimension(500, 300));
//                splitterDialog.setVisible(true);
//                populateStockTable2(getSockItems());
//                populateDistributionTable();
//            }
//        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    char [] password = passwordField.getPassword();
                    char [] confirmpassword = confirmpasswordField.getPassword();

                    if(Arrays.equals(password,confirmpassword)) {
                        byte[] salt = SaltedMD5.getSalt();
                        String securePassword = SaltedMD5.getSecurePassword(new String(password), salt);

                        User user = new User();
                        user.setUserName(userNameTextField.getText().trim());
                        user.setFirstName(user_firstNameTextField.getText().trim());
                        user.setLastName(user_lastNameTextField.getText().trim());

                        user.setPassword(securePassword);
                        user.setSalt(salt);
                        user.setAdmin(adminCheckBox.isSelected());

                        mAcess.registerUser(user);
                        resetFormFields(register_panel);
                        errorLabel.setVisible(false);
                        populateUsersTable();
                    }else
                    {
                        errorLabel.setVisible(true);
                        errorLabel.setForeground(Color.RED);
                        errorLabel.setText("Passwords do not match!");
                    }
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (NoSuchProviderException e1) {
                    e1.printStackTrace();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        addUnitsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(productPanel);
                AddUnitsDialog addUnitsDialog = new AddUnitsDialog(topFrame,serverIp);
                addUnitsDialog.setVisible(true);

                ArrayList<Unit> units = mAcess.getAllUnits();
                ComboBoxModel cbUnitsModel = new DefaultComboBoxModel(units.toArray());
                unitsComboBox.setModel(cbUnitsModel);
            }
        });
        cancelRegistrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFormFields(register_panel);
            }
        });
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(userAccountsPanel);
                int row = usersTable.getSelectedRow();
                String username = (String)usersTable.getModel().getValueAt(row, 0);
                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(username,topFrame,serverIp);
                changePasswordDialog.setVisible(true);

            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateStatementTable();
            }
        });
        addLoctionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String location = locationTextField.getText();
                    if(location.length()>0) {
                        mAcess.addLocation(new Site(locationTextField.getText()));
                        locationsList.setModel(new DefaultComboBoxModel(mAcess.getLocations().toArray()));
                        resetFormFields(locationPanel);
                    }

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        removeLocationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int n = JOptionPane.showConfirmDialog(null,
                        "All stock information associated to this location will be deleted.\n" +
                                "You must delete stock information for this location before performing this action.\n" +
                                " Do you want to continue?",
                        "Dangerous Action", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {

                    try {
                        Site location = (Site) locationsList.getSelectedValue();
                        mAcess.deleteLocation(location);
                        locationsList.setModel(new DefaultComboBoxModel(mAcess.getLocations().toArray()));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                int index = tabbedPane.getSelectedIndex();

                switch (index){
//                    case 0:
//                        populateProductsTable();
//                        populateCategoriesListBox();
//                        break;
                    case 1:
                        if(tab1Loaded==false){
                            populateSupplierTable2();
                            populateSupplyTransactionsTable();

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        populateComboBox2(supplierComboBox1,eventListSuppliers,new SupplierTextFilterator());
                                    } catch (InvocationTargetException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            });
                            thread.start();
                            tab1Loaded=true;
                        }
                        break;
                    case 2:
                        if(tab2Loaded==false){
                            populateDistributionTable();
                            populateStockTable2(getSockItems());
                            stockTable.getColumnModel().getColumn(0).setPreferredWidth(50);
                            stockTable.getColumnModel().getColumn(1).setPreferredWidth(250);
                            locationsList.setModel(new DefaultComboBoxModel(mAcess.getLocations().toArray()));
                            tab2Loaded=true;
                        }
                        break;
                    case 3:
                        if(tab3Loaded==false){
                            populateCustomersTable();
                            populateStatementTable(); //customer transactions
                            tab3Loaded=true;
                        }
                        break;
                    case 4:
                        if(tab4Loaded==false){
                            populateProductslist();
                            tab4Loaded=true;
                        }
                        break;
                    case 5:
                        if(tab5Loaded==false){
                            populateTillsCombobox();
                            populateCashiersComboBox();
                            tab5Loaded=true;
                        }
                        break;
                    case 6:
                        if(tab6Loaded==false){
                            populateRefundsTable();
                            tab6Loaded=true;
                        }
                        break;
                    case 7:
                        if(tab7Loaded==false){
                            populateUsersTable();
                            tab7Loaded=true;
                        }
                        break;
                    case 8:
                        if(tab8Loaded==false){
                            populateSettingsTable();
                            tab8Loaded=true;
                        }
                        break;
                }


            }
        });
        searchStockTransactions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<StockItem> stockItems =
                            mAcess.getStock(fromDatePicker.getValue().toString(),toDatePicker.getValue().toString());
                    populateStockTable2(stockItems);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
        searchReceiptsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Query query = new Query();
                query.setStartDate(startdateReceiptsDatePicker.getValue().toString());
                query.setEndDate(endDateReceiptstoDatePicker.getValue().toString());
                if(tillComboBox.getSelectedIndex()>0)
                    query.setTillnumber((int)tillComboBox.getSelectedItem());

                if(cashierComboBox.getSelectedIndex()>0)
                    query.setUsername(((User) cashierComboBox.getSelectedItem()).getUserName());

                try {
                    double cash = mAcess.getCashReceived(query);
                    cashFormattedTextField.setValue(cash);
                    populateReceiptTable(query);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }




            }
        });
        printSupplierStatementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int supplierId = ((Supplier)supplierComboBox1.getSelectedItem()).getId();
                ArrayList<SupplierTransaction> supplierTransactions = getSupplierTransactions(supplierId,
                        startDateSupStatementPicker.getValue().toString(),stopDateSupStatementPicker.getValue().toString());

                for(SupplierTransaction supplierTransaction : supplierTransactions){
                    supplierTransaction.addAllItems(getDeliveries(supplierTransaction.getId()));
                }

                ReceiptHeader receiptHeader = new ReceiptHeader();
                receiptHeader.setBusinessName(settingsParser.getBusinessName());
                receiptHeader.setLocation(settingsParser.getLocation());
                receiptHeader.setTelephoneNumber1(settingsParser.getPhone1());
                receiptHeader.setTelephoneNumber2(settingsParser.getPhone2());
                receiptHeader.setTin(settingsParser.getTin());


                PrinterJob job = PrinterJob.getPrinterJob();
                PageFormat pf = job.defaultPage();
                Paper paper = new Paper();
                double margin = 3.6; // 1 tenth of an inch
                paper.setImageableArea(margin, margin, paper.getWidth() - margin * 2, paper.getHeight()
                        - margin * 2);
                pf.setPaper(paper);

                job.setPrintable(new SupplierStatementPrinter(supplierTransactions, receiptHeader),pf);

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
        });
        refreshButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateDistributionTable();
                populateStockTable2(getSockItems());
            }
        });
        refreshReturnsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateRefundsTable();
            }
        });
        viewDeletedSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeletedSalesView dialog = new DeletedSalesView(serverIp);
                dialog.setMinimumSize(new Dimension(700, 600));
                dialog.setVisible(true);
            }
        });
        applyVATButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    if(productsSalesComboBox.getSelectedIndex() == -1){
                        productsSalesComboBox.setSelectedIndex(0);
                    }
                    Product product = (Product)productsSalesComboBox.getSelectedItem();
                    ArrayList<Item> items = new ArrayList<>();

                    String startDate = startDatePicker.getValue().toString();
                    String stopDate = stopDatePicker.getValue().toString();
                    Double deletedSalesTotal = 0.0;
                    if(product.getProductName() !=null){
                        items = mAcess.getItemsSold(startDate,stopDate
                                ,product.getProductId() );
                        deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate,product.getProductId());
                    }else {
                        items = mAcess.getItemsSold(startDate,
                                stopDate);
                        deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate);
                    }
                    Object[][] items2D = new Object[items.size()][];
                    int i = 0;
                    Double totalSales = 0.0;
                    Double totalmargin = 0.0;
                    Double totalVat = 0.0;
                    for (Item item : items) {
                        Product _product = mAcess.getProduct(item.getProductId());
                        mAcess.updateVatForSoldItems(item,_product.getVat());
                        items2D[i] = item.toArray();
                        totalSales += item.getTotalPrice();
                        totalmargin += item.getMargin();
                        totalVat += item.getVat_amount();
                        i++;
                    }


                    totalSalesformattedTextField.setValue(totalSales);
                    vatFormattedTextField.setValue(totalVat);
                    marginformattedTextField.setValue(totalmargin);
                    deletedSalesFormattedTextField.setValue(deletedSalesTotal);

                    populateDetailedSalesTable();
                    formatDetailedSalesTable();
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        deleteReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "This Action can not be reversed.\n " +
                                "Do you want to continue?",
                        "Warning", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {
                    int[] selectedRows = receiptsTable.getSelectedRows();
                    //int [] receiptIds = new int [selectedRows.length];

                    ArrayList<Receipt> receipts = new ArrayList<>();
                    for(int i=0; i<selectedRows.length; i++){
                        int receiptId = (int) receiptsTable.getModel().getValueAt(selectedRows[i],0);
                        Receipt receipt = new Receipt();
                        receipt.setReceiptId(receiptId);
                        receipts.add(receipt);

                    }
                    try {
                        mAcess.deleteReceipts(receipts);
                        Query query = new Query();
                        query.setStartDate(startdateReceiptsDatePicker.getValue().toString());
                        query.setEndDate(endDateReceiptstoDatePicker.getValue().toString());
                        if(tillComboBox.getSelectedIndex()>0)
                            query.setTillnumber((int)tillComboBox.getSelectedItem());

                        if(cashierComboBox.getSelectedIndex()>0)
                            query.setUsername(((User) cashierComboBox.getSelectedItem()).getUserName());
                        double cash = mAcess.getCashReceived(query);
                        cashFormattedTextField.setValue(cash);
                        populateReceiptTable(query);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }finally {
                    }
                }

            }
        });
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workbook workbook = new HSSFWorkbook();
                Sheet sheet = workbook.createSheet("Sales");

                Row header = sheet.createRow(0);

                Cell headerCell = header.createCell(0);
                headerCell.setCellValue("Item no.");

                headerCell = header.createCell(1);
                headerCell.setCellValue("Product Name");

                headerCell = header.createCell(2);
                headerCell.setCellValue("Quantity");

                headerCell = header.createCell(3);
                headerCell.setCellValue("Price");

                headerCell = header.createCell(4);
                headerCell.setCellValue("Total");

                headerCell = header.createCell(5);
                headerCell.setCellValue("VAT");

                headerCell = header.createCell(6);
                headerCell.setCellValue("Margin");

                headerCell = header.createCell(7);
                headerCell.setCellValue("Invoice no.");

                headerCell = header.createCell(8);
                headerCell.setCellValue("Customer");

                headerCell = header.createCell(9);
                headerCell.setCellValue("Status");

                headerCell = header.createCell(10);
                headerCell.setCellValue("Sold By");

                headerCell = header.createCell(11);
                headerCell.setCellValue("Till");

                headerCell = header.createCell(12);
                headerCell.setCellValue("Date");



                String startDate = startDatePicker.getValue().toString();
                String stopDate = stopDatePicker.getValue().toString();
                ArrayList<Item> items = new ArrayList<>();
                try {
                    items = mAcess.getItemsSold(startDate,stopDate);
                    int i = 1;
                    for(Item item : items){
                        Row row = sheet.createRow(i);
                        Cell cell = row.createCell(0);
                        cell.setCellValue(item.getTransactionId());

                        cell = row.createCell(1);
                        cell.setCellValue(item.getProductName());

                        cell = row.createCell(2);
                        cell.setCellValue(item.getQuantity());

                        cell = row.createCell(3);
                        cell.setCellValue(item.getPrice());

                        cell = row.createCell(4);
                        cell.setCellValue(item.getTotalPrice());

                        cell = row.createCell(5);
                        cell.setCellValue(item.getVat_amount());

                        cell = row.createCell(6);
                        cell.setCellValue(item.getMargin());

                        cell = row.createCell(7);
                        cell.setCellValue(item.getInvoiceNumber());

                        cell = row.createCell(8);
                        cell.setCellValue(item.getCustomerName());

                        cell = row.createCell(9);
                        cell.setCellValue(item.getInvoiceStatus().toString());

                        cell = row.createCell(10);
                        cell.setCellValue(item.getSellername());

                        cell = row.createCell(11);
                        cell.setCellValue(item.getTill());

                        cell = row.createCell(12);
                        cell.setCellValue(item.getTime());



                        i++;
                    }

                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }

                //File dir = new File(".");
                File dir = new File("C:\\sales\\.");
                String path = dir.getAbsolutePath();
                String fileLocation = path.substring(0, path.length()-1) + "sales_" + startDate + "__" + stopDate + ".xls";

                try {
                    FileOutputStream outputStream = new FileOutputStream(fileLocation);
                    workbook.write(outputStream);
                    workbook.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        });
        deleteUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int n = JOptionPane.showConfirmDialog(null,
                        "You are about to delete a user. This Action can not be reversed.\n " +
                                "Do you want to continue?",
                        "Warning", JOptionPane.YES_NO_OPTION);

                if (n == JOptionPane.YES_OPTION) {

                    int rowNo = usersTable.getSelectedRow();
                    String username = (String)usersTable.getValueAt(rowNo,0);

                    try {
                        mAcess.deleteUser(username);
                        populateUsersTable();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }finally {
                    }
                }
            }
        });
    }

    private void formatDetailedSalesTable() {
        JScrollPane scrollPane = new JScrollPane(salesTable);
        salesPanel.removeAll();
        salesPanel.add(scrollPane);

        //TODO: find a solution for the display quirk wherein it does not show anything;
        // tentatively solved below. Perhaps run on a separate thread: platform.runlater()
        //it may be caused by setting widths after table is populated; shift width setting to populateDetailedSalesTahle();
        salesPanel.setVisible(false);
        salesPanel.setVisible(true);

        salesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // productname
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(50);  // quantity
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(50);
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        salesTable.getColumnModel().getColumn(6).setPreferredWidth(50);
    }

    private ArrayList getSockItems() {
        ArrayList stockItems = new ArrayList();
        try {
            stockItems = mAcess.getStock();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return stockItems;
    }


    private void createItemSuppliedTableModelListener() {
        itemsSuppliedTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                //TODO: consider all table columns editable (if necessary)

                    double quantity = (double) itemsSuppliedTable.getValueAt(row, 3);
                    double price = (double) itemsSuppliedTable.getValueAt(row, 4);
                    int invoice_no = (int) itemsSuppliedTable.getValueAt(row, 6);
                    double total = quantity * price;
                    itemsSuppliedTable.setValueAt(total, row, 5);
                    int deliveryId = (int) itemsSuppliedTable.getValueAt(row, 0);

                    Item item = new Item();
                    item.setQuantity(quantity);
                    item.setPrice(price);
                    item.setInvoiceNumber(invoice_no);
                    mAcess.updateDelivery(deliveryId, item);
                    populateSupplyTransactionsTable();

                populateDistributionTable();
                populateStockTable2(getSockItems());
            }
        });
    }

    private ArrayList<CustomerTransaction> getCustomerTransactions(int customerId,String startDate, String endDate ) {
        ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
        try {
            customerTransactions = mAcess.getCustomerTransactions(customerId,startDate,endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerTransactions;
    }

    public static ArrayList<Customer> getCustomers() {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            customers = mAcess.getCustomers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    private void populateDeliveryTable() {
        ArrayList<Item> deliveries = getDeliveries();
        TextFilterator deliveriesFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Item item = (Item)o;
                baselist.add(item.getSellername());
                baselist.add(item.getProductName());
                baselist.add(item.getInvoiceNumber());
                baselist.add(item.getTime());
                baselist.add(item.getPrice());
                baselist.add(item.getTotalPrice());
            }
        };

        String [] deliveriesPropertyNames = {"transactionId","sellername","productName", "quantity", "price", "totalPrice",
                "invoiceNumber", "time"};
        //boolean [] editable = {false,false, false, true, true, true, true, false};
        boolean [] editable = {false,false, false, false, false, false, false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, deliveriesPropertyNames, deliveryTableColumnNames,
        editable);
        populateFilterableTable(itemsSuppliedTable,deliveries,
                                deliveriesFilterator,filterDeliveriesTextField,tableFormat);
    }

    //provides the transaction details when a row in the master (supplier) transactions table is clicked
    private void populateDeliveryTable(int transactionId) {
        ArrayList<Item> deliveries = getDeliveries(transactionId);
        TextFilterator deliveriesFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Item item = (Item)o;
                baselist.add(item.getSellername());
                baselist.add(item.getProductName());
                baselist.add(item.getInvoiceNumber());
                baselist.add(item.getTime());
                baselist.add(item.getPrice());
                baselist.add(item.getTotalPrice());
            }
        };

        String [] deliveriesPropertyNames = {"transactionId","sellername","productName", "quantity", "price", "totalPrice",
                "invoiceNumber", "time"};

        boolean [] editable = {false,false, false, false, false, false, false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, deliveriesPropertyNames, deliveryTableColumnNames,
                editable);
        populateFilterableTable(itemsSuppliedTable,deliveries,
                deliveriesFilterator,filterDeliveriesTextField,tableFormat);
    }

    private void populateDistributionTable() {
        ArrayList<Distributor> distributors = getDistributors();
        TextFilterator distributionFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Distributor distributor = (Distributor) o;
                baselist.add(distributor.getProductName());
                baselist.add(distributor.getTotalQuantityInStock());
                baselist.add(distributor.getDate_time_created());
                baselist.add(distributor.getStockLowThreshold());
                baselist.add(distributor.getDate_time_modified());
            }
        };

        String [] distributionPropertyNames = {"productId","productName","totalQuantityInStock", "stockLowThreshold", "date_time_modified"};

        String [] columnNames = {"Product no.", "Product", "Total Qty",
                "Low Threshold", "Date Modified"};

        boolean [] editable = {false,false, false, false, false, false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(Distributor.class, distributionPropertyNames, columnNames,
                editable);
        populateFilterableTable(distributionTable,distributors,
                distributionFilterator,distributionTextField,tableFormat);
    }

    private void populateSupplierTable2() {
        ArrayList<Supplier> suppliers = getSuppliers();
        TextFilterator supplierFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Supplier supplier = (Supplier) o;
                baselist.add(supplier.getId());
                baselist.add(supplier.getSupplierName());
                baselist.add(supplier.getAccountNumber());
                baselist.add(supplier.getBankName());
                baselist.add(supplier.getAddress());
                baselist.add(supplier.getDateCreated());
                baselist.add(supplier.getEmail());
                baselist.add(supplier.getPhone1());
                baselist.add(supplier.getPhone2());
            }
        };

        String[] supplierTableColumnNames = {
                "Id","Supplier Name","Phone1","Phone2","Email","Address","Bank","Account No.","Date Added"
        };

        String [] supplierPropertyNames = {"id","supplierName","phone1", "phone2",
                "email","address","bankName","accountNumber","dateCreated"};

        boolean [] editable = {false,true, true, true, true, true, true,true,false};

        TableFormat tableFormat = GlazedLists.tableFormat(Supplier.class, supplierPropertyNames, supplierTableColumnNames,
                editable);
        populateFilterableTable(suppliersTable,suppliers,
                supplierFilterator,supplierFilterTextField,tableFormat);

        addSuppliersTableModelListener();
    }

    private void addSuppliersTableModelListener() {
        suppliersTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                try {
                    TableModel tableModel = suppliersTable.getModel();
                    int supplierId = (int) tableModel.getValueAt(row, 0);
                    Supplier supplier = new Supplier();
                    supplier.setId(supplierId);
                    supplier.setSupplierName((String)tableModel.getValueAt(row, 1));
                    supplier.setAccountNumber((String)tableModel.getValueAt(row, 7));
                    supplier.setBankName((String)tableModel.getValueAt(row, 6));
                    supplier.setEmail((String)tableModel.getValueAt(row, 4));
                    supplier.setPhone1((String)tableModel.getValueAt(row, 2));
                    supplier.setPhone2((String)tableModel.getValueAt(row, 3));
                    supplier.setAddress((String)tableModel.getValueAt(row, 5));
                    mAcess.updateSupplier(supplier);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void populateUsersTable() {
        ArrayList<User> users = mAcess.getUsers();
        TextFilterator userFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                User user = (User) o;
                baselist.add(user.getUserName());
                baselist.add(user.getFirstName());
                baselist.add(user.getLastName());
                baselist.add(user.getDate_created());
            }
        };

        String[] userTableColumnNames = {
                "username","First Name","Last Name","Admin","Date created"};

        String [] userPropertyNames = {"userName","firstName","lastName","admin", "date_created"};

        boolean [] editable = {false,true, true, true, false};

        TableFormat tableFormat = GlazedLists.tableFormat(User.class, userPropertyNames, userTableColumnNames,
                editable);
        populateFilterableTable(usersTable,users,
                userFilterator,userFilterTextField,tableFormat);

        addUserTableModelListener();
    }

    private void addUserTableModelListener() {
        usersTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                try {
                    TableModel tableModel = usersTable.getModel();
                    String username = (String) tableModel.getValueAt(row, 0);
                    User user = new User();
                    user.setUserName(username);
                    user.setFirstName((String)tableModel.getValueAt(row, 1));
                    user.setLastName((String)tableModel.getValueAt(row, 2));
                    user.setAdmin((boolean)tableModel.getValueAt(row, 3));
                    mAcess.updateUser(user);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    private void addProductTableModelListener() {
        productsTable2.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();


                if(e.getType()==0) {
                    try {
                        TableModel tableModel = productsTable2.getModel();
                        int productId = (int) tableModel.getValueAt(row, 0);
                        boolean isVatable = (boolean) tableModel.getValueAt(row, 5);
                        mAcess.updateVat(productId, isVatable);
                        Product updatedProduct = mAcess.getProduct(productId);

                        tableModel.setValueAt(updatedProduct.getVat(), row, 6);
                        tableModel.setValueAt(updatedProduct.getMarkup(), row, 7);
                        tableModel.setValueAt(updatedProduct.getLastModifiedDate(), row, 12);

                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                }
        });
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
    }

    private void populateSettingsTable() {

        String[] columnNames = {"Setting", "Value"};

        String [][] settings = {{"Business name", settingsParser.getBusinessName()},
                {"Address", settingsParser.getLocation()},
                {"Phone 1", settingsParser.getPhone1()},
                {"Phone 2", settingsParser.getPhone2()},
                {"TIN", settingsParser.getTin()},
                {"Server Ip", settingsParser.getServerIp()},

                //{"default_stock_destination", default_destination.getName() }
    };
        SettingsTableModel settingsTableModel = new SettingsTableModel(settings, columnNames);

        settingsTable.setModel(settingsTableModel);

        addSettingsTableModelListener();
    }

    private void addSettingsTableModelListener() {
        settingsTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                try {
                    SettingsTableModel tableModel = (SettingsTableModel)settingsTable.getModel();
                    String nodeName = (String) tableModel.getValueAt(row, 0);
                    String textContent = (String) tableModel.getValueAt(row, 1);

                    switch (nodeName){
                        case "Business name":
                            settingsParser.setBusinessName(textContent);
                            break;
                        case "Address":
                            settingsParser.setLocation(textContent);
                            break;
                        case "Phone 1":
                            settingsParser.setPhone1(textContent);
                            break;
                        case "Phone 2":
                            settingsParser.setPhone2(textContent);
                            break;
                        case "TIN":
                            settingsParser.setTin(textContent);
                            break;
                        case "Server Ip":
                            settingsParser.setServerIp(textContent);
                            break;
                    }

                    if(nodeName == "server_ip") {
                        settingsParser.updateSettings("server", "server_ip", textContent);
                    }else{
                        settingsParser.updateSettings("header", nodeName, textContent);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }

    private void populateCustomersTable() {
        ArrayList<Customer> customers = getCustomers();
        TextFilterator customersFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                Customer customer = (Customer) o;
                baselist.add(customer.getFirstname());
                baselist.add(customer.getLastname());
                baselist.add(customer.getSex());
                baselist.add(customer.getBirthday());
                baselist.add(customer.getPhone1());
                baselist.add(customer.getPhone2());
                baselist.add(customer.getAddress());
                baselist.add(customer.getEmail());
                baselist.add(customer.getDateCreated());
                baselist.add(customer.getDateModified());

            }
        };

        String [] customerPropertyNames = {"id","firstname","lastname", "sex",
                "birthday", "email",  "phone1", "phone2", "address", "dateCreated"};

        String [] columnNames = {"id.", "First name", "Last name", "Sex", "Birthday", "email", "Phone1", "Phone2", "Address",
                "Date"};

        boolean [] editable = {false, true,true, true, true, true, true, true, true, true};

        JComboBox sexComboBox = new JComboBox();
        sexComboBox.addItem("");
        sexComboBox.addItem("male");
        sexComboBox.addItem("female");

        TableFormat tableFormat = GlazedLists.tableFormat(Customer.class, customerPropertyNames, columnNames,editable);
        populateFilterableTable(customersTable,customers,
                customersFilterator,filterCustomerTextField,tableFormat);

        TableColumn sexColumn = customersTable.getColumnModel().getColumn(3);
        sexColumn.setCellEditor(new DefaultCellEditor(sexComboBox));
        customersTable.setRowHeight(25);

        addCustomerTableModelListener();
    }

    private void addCustomerTableModelListener() {
        customersTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                try {
                    TableModel tableModel = customersTable.getModel();
                    int customerId = (int) tableModel.getValueAt(row, 0);
                    Customer customer = new Customer();
                    customer.setId(customerId);
                    customer.setFirstname((String)tableModel.getValueAt(row, 1));
                    customer.setLastname((String)tableModel.getValueAt(row, 2));
                    customer.setSex((String)tableModel.getValueAt(row, 3));
                    customer.setBirthday((String)tableModel.getValueAt(row, 4));
                    customer.setEmail((String)tableModel.getValueAt(row, 5));
                    customer.setPhone1((String)tableModel.getValueAt(row, 6));
                    customer.setPhone2((String)tableModel.getValueAt(row, 7));
                    customer.setAddress((String)tableModel.getValueAt(row, 8));
                    mAcess.updateCustomer(customer);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }




    //populates the customer statement table
    private void populateStatementTable() {
        ArrayList<CustomerStatement> customerStatements = getCustomerStatements();
        TextFilterator statementsFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                CustomerStatement customerStatement = (CustomerStatement)o;
                baselist.add(customerStatement.getId());
                baselist.add(customerStatement.getAmount());
                baselist.add(customerStatement.getBalance());
                baselist.add(customerStatement.getType());
                baselist.add(customerStatement.getDate_entered());
                baselist.add(customerStatement.getDate_modified());
                baselist.add(customerStatement.getFirstname());
                baselist.add(customerStatement.getLastname());
            }
        };

        String [] statementColumLabels = {"No.", "First name", "Last name", "Type", "Amount" , "Balance", "Date" };
        String [] statementPropertyNames = {"id","firstname","lastname", "type", "amount", "balance", "date_entered"};
        boolean [] editable = {false,false, false, false, false, false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(CustomerStatement.class, statementPropertyNames, statementColumLabels,
                editable);
        populateFilterableTable(customerStatementTable,customerStatements,
                statementsFilterator,statementFilterTextField,tableFormat);

        customerStatementTable.getColumnModel().getColumn(3).setCellRenderer(new CustomerStatementTableCellRenderer());
    }


    private void populateInvoiceTable() {
        int row = customerStatementTable.getSelectedRow();
        int statement_id = (int) customerStatementTable.getModel().getValueAt(row, 0);
        int invoice_id = getInvoiceId(statement_id);

        ArrayList<Item> items = getInvoiceItems(invoice_id);

        String [] itemColumLabels = {"Product", "Quantity", "Units", "Price", "Total"};
        String [] itemPropertyNames = {"productName","quantity","units","price", "totalPrice"};
        boolean [] editable = {false,false, false, false, false};

        itemsEventList.clear();
        itemsEventList.addAll(items);

        TableFormat tableFormat = GlazedLists.tableFormat(Item.class, itemPropertyNames,
                itemColumLabels, editable);
        invoiceTable.setModel(new EventTableModel(itemsEventList,tableFormat));
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

    private int getInvoiceId(int statement_id) {
        int invoice_id = 0;
        try {
            invoice_id = mAcess.getInvoiceId(statement_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoice_id;
    }

    private void populateSupplyTransactionsTable() {
        ArrayList<SupplierTransaction> supplierTransactions = getSupplierTransactions();
        TextFilterator suppliertransactionsFilterator = new TextFilterator() {
            @Override
            public void getFilterStrings(List baselist, Object o) {
                SupplierTransaction supplierTransaction = (SupplierTransaction) o;
                baselist.add(supplierTransaction.getDescription());
                baselist.add(supplierTransaction.getTransaction_type());
                baselist.add(supplierTransaction.getAmount());
                baselist.add(supplierTransaction.getBalance());
                baselist.add(supplierTransaction.getSupplierName());
                baselist.add(supplierTransaction.getDate_created());
            }
        };

        String [] transactionsColumLabels = {"Transaction Id", "Supplier", "Description", "Transaction Type", "Amount" , "Balanace", "Date" };
        String [] transactionsPropertyNames = {"id","supplierName","description", "transaction_type", "amount", "balance",
                 "date_created"};
        boolean [] editable = {false,false, false, false, false, false, false};

        TableFormat tableFormat = GlazedLists.tableFormat(SupplierTransaction.class, transactionsPropertyNames, transactionsColumLabels,
                editable);
        populateFilterableTable(supplierTransactionsTable,supplierTransactions,
                suppliertransactionsFilterator,supplyTransactionsFiltertextField,tableFormat);
    }

    private void populateDetailedSalesTable() throws SQLException, ClassNotFoundException {
        if(productsSalesComboBox.getSelectedIndex() == -1){
            productsSalesComboBox.setSelectedIndex(0);
        }
        Product product = (Product)productsSalesComboBox.getSelectedItem();
        ArrayList<Item> items = new ArrayList<>();

        String startDate = startDatePicker.getValue().toString();
        String stopDate = stopDatePicker.getValue().toString();
        Double deletedSalesTotal = 0.0;
        if(product.getProductName() !=null){
            items = mAcess.getItemsSold(startDate,stopDate
                    ,product.getProductId() );
            deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate,product.getProductId());
        }else {
            items = mAcess.getItemsSold(startDate,
                    stopDate);
            deletedSalesTotal = mAcess.getDeletedSalesTotal(startDate,stopDate);
        }
        Object[][] items2D = new Object[items.size()][];
        int i = 0;
        Double totalSales = 0.0;
        Double totalmargin = 0.0;
        Double totalVat = 0.0;
        for (Item item : items) {
            items2D[i] = item.toArray();
            totalSales += item.getTotalPrice();
            totalmargin += item.getMargin();
            totalVat += item.getVat_amount();
            i++;
        }

        salesTable.setModel(new ItemsTableModel(items2D, salesTableColumnNames));
        TableColumnModel columnModel = salesTable.getColumnModel();
        columnModel.removeColumn(salesTable.getColumnModel().getColumn(13)); //hide productid

        totalSalesformattedTextField.setValue(totalSales);
        vatFormattedTextField.setValue(totalVat);
        marginformattedTextField.setValue(totalmargin);
        deletedSalesFormattedTextField.setValue(deletedSalesTotal);
    }


    private static void populateComboBox(JComboBox comboBox, ArrayList arrayList) throws InvocationTargetException {
        ComboBoxModel cbModel = new DefaultComboBoxModel(arrayList.toArray());
        comboBox.setModel(cbModel);
        //comboBox.setSelectedIndex(-1);
        //EventList<Product> products = GlazedLists.eventList(getProducts());
        EventList<Product> products = GlazedLists.eventList(arrayList);
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    AutoCompleteSupport support = AutoCompleteSupport.install(comboBox,products, new ProductsTextFilterator());
                    support.setFilterMode(TextMatcherEditor.CONTAINS);
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
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

    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column != -1 && column !=5) {
            ItemsTableModel model = (ItemsTableModel) e.getSource();
            double quantity = (double) model.getValueAt(row, 2);
            double price = (double) model.getValueAt(row, 4);
            double total = quantity * price;
            model.setValueAt(total, row, 5);
        }
    }

    public static final class ProductsTextFilterator implements TextFilterator<Product> {
        @Override
        public void getFilterStrings(List<String> baseList, Product product) {
            final String productName = product.getProductName();
            baseList.add(productName);
        }
    }

    public static final class SupplierTextFilterator implements TextFilterator<Supplier> {
        @Override
        public void getFilterStrings(List<String> baseList, Supplier supplier) {
            baseList.add(supplier.getSupplierName());
        }
    }


    private void populateCategoriesComboBox() {
        ArrayList<Category> categories = null;
        try {
            categories = mAcess.getProductCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ComboBoxModel cbModel = new DefaultComboBoxModel(categories.toArray());
        categoryComboBox.setModel(cbModel);
        categoryComboBox.setSelectedIndex(-1);
    }

    private void populateCategoriesListBox() {
        ArrayList<Category> categories = null;
        try {
            categories = mAcess.getProductCategories();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ComboBoxModel cbModel = new DefaultComboBoxModel(categories.toArray());
        categoryList.setModel(cbModel);
        categoryList.setSelectedIndex(-1);
    }

    private void populateTillsCombobox() {
        ArrayList<Integer> tills = null;
        try {
            tills = mAcess.getTillNumbers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ComboBoxModel cbModel = new DefaultComboBoxModel(tills.toArray());
        tillComboBox.setModel(cbModel);
        tillComboBox.insertItemAt("",0);
        tillComboBox.setSelectedIndex(-1);

    }

    private void populateCashiersComboBox() {
        ArrayList<User> users = null;
        try {
            users = mAcess.getUsers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ComboBoxModel cbModel = new DefaultComboBoxModel(users.toArray());
        cashierComboBox.setModel(cbModel);
        cashierComboBox.insertItemAt("",0);
        cashierComboBox.setSelectedIndex(-1);
    }

//    private void populateTable(JTable table, ArrayList<Object>data ){
//        Object [][] obj2D = new Object[data.size()][];
//        int i=0;
//        for(Object object : data){
//            obj2D[i] = (object.toArray());
//            i++;
//        }
//        productsTable.setModel(new ItemsTableModel(products2D, productsTableColumnNames));
//
//    }

    private void populateProductsTable() {

       EventList products2 = new BasicEventList();



        try {
            products2.addAll(mAcess.getAllProducts());
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextFilterator productFilterator = new TextFilterator() {
            public void getFilterStrings(List baseList, Object element) {
                Product product = (Product) element;
                baseList.add(product.getProductName());
                baseList.add(product.getPrice());
                baseList.add(product.getUnits());
                baseList.add(product.getCategory());
                baseList.add(product.getBarcode());
            }
        };

        TextComponentMatcherEditor matcherEditor = new TextComponentMatcherEditor(filterTextField, productFilterator);
        FilterList filteredProducts = new FilterList(products2, new ThreadedMatcherEditor(matcherEditor));

        //TODO: Add category field
        String[] propertyNames = new String[] {"productId","productName", "description", "costprice", "price",
                "vatable","vat","markup",
                "units", "stockLowThreshold", "barcode", "dateCreated",  "lastModifiedDate",  "comment"};
        String[] columnLabels = new String[] {"Product no.","Product", "Description", "Cost Price",  "Sell Price",
                "Vatable","VAT","Mark up",  "Units", "Threshold","Barcode", "Date Added",  "Last Modified", "Comment" };

        boolean [] editable = {false,false, false,false,false,true, true,true,false, false,false,false,true,false};


        TableFormat tableFormat = GlazedLists.tableFormat(Product.class, propertyNames, columnLabels,editable);
        productsTable2 = new JTable(new EventTableModel(filteredProducts, tableFormat));
        //productsTable2.setFillsViewportHeight(true);
        productsTable2.getSelectionModel().addListSelectionListener(new ProductsTableRowListener());
        productsTable2.setRowSelectionAllowed(true);
        productsTable2.getColumnModel().getColumn(1).setPreferredWidth(200);
        productsTable2.getColumnModel().getColumn(2).setPreferredWidth(50); //comments
        productsTable2.getColumnModel().getColumn(3).setPreferredWidth(50);
        productsTable2.getColumnModel().getColumn(4).setPreferredWidth(50);
        productsTable2.getColumnModel().getColumn(5).setPreferredWidth(50);
        productsTable2.getColumnModel().getColumn(0).setPreferredWidth(30); // productid
        productsTable2.getColumnModel().getColumn(6).setPreferredWidth(20); // units
        productsTable2.getColumnModel().getColumn(7).setPreferredWidth(20); // threshold
        productsTable2.getColumnModel().getColumn(8).setPreferredWidth(20); // barcode
        productsTable2.getColumnModel().getColumn(11).setPreferredWidth(20); // comments
        productsTable2.getColumnModel().getColumn(12).setPreferredWidth(20);
        productsTable2.getColumnModel().getColumn(13).setPreferredWidth(20);


        JScrollPane scrollPane = new JScrollPane(productsTable2);
        //productPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        productsDisplayPanel.removeAll();
        productsDisplayPanel.add(scrollPane);

        productsDisplayPanel.setVisible(false);
        productsDisplayPanel.setVisible(true);

        addProductTableModelListener();

    }

    private void populateStockTable2(ArrayList<StockItem>stockItems) {
        stockItemsEventList = new BasicEventList();

        try {
            //stockItemsEventList.addAll(mAcess.getStock());
            stockItemsEventList.addAll(stockItems);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextFilterator stockFilterator = new TextFilterator() {
            public void getFilterStrings(List baseList, Object element) {
                StockItem stockItem = (StockItem) element;
                baseList.add(stockItem.getTransactionId());
                baseList.add(stockItem.getProductName());
                baseList.add(stockItem.getQuantity());
                baseList.add(stockItem.getDirection());
                baseList.add(stockItem.getComment());
                baseList.add(stockItem.getLocation());
                baseList.add(stockItem.getBalance());
                baseList.add(stockItem.getSource_dest());
                baseList.add(stockItem.getDateCreated());
            }
        };

        TextComponentMatcherEditor stockMatcherEditor = new TextComponentMatcherEditor(filterStockTextField, stockFilterator);
        FilterList filteredStockItems = new FilterList(stockItemsEventList, new ThreadedMatcherEditor(stockMatcherEditor));

        String[] stockItemPropertyNames = new String[] {"transactionId","productName", "location", "quantity", "direction",
                "balance", "source_dest", "dateCreated", "comment"};
        String[] stockTablecolumnLabels = new String[] {"id.","Product", "location", "Quantity","Direction", "Balance",
                "Source/Dest", "Date", "Comment"};

        TableFormat stockTableFormat = GlazedLists.tableFormat(StockItem.class, stockItemPropertyNames, stockTablecolumnLabels);
        stockTable.setModel(new EventTableModel(filteredStockItems,stockTableFormat));
    }


    private void populateFilterableTable(JTable table,ArrayList arrayList, TextFilterator textFilterator,
                                         JTextField textField, TableFormat tableFormat) {
        EventList eventList = new BasicEventList();
        eventList.addAll(arrayList);

        TextComponentMatcherEditor matcherEditor = new TextComponentMatcherEditor(textField, textFilterator);
        FilterList filterList = new FilterList(eventList, new ThreadedMatcherEditor(matcherEditor));

        table.setModel(new EventTableModel(filterList,tableFormat));
    }

    private void populateFilterableTable(JTable table,EventList eventList, TextFilterator textFilterator,
                                         JTextField textField, TableFormat tableFormat) {
        //EventList eventList = new BasicEventList();
        //eventList.addAll(arrayList);

        TextComponentMatcherEditor matcherEditor = new TextComponentMatcherEditor(textField, textFilterator);
        FilterList filterList = new FilterList(eventList, new ThreadedMatcherEditor(matcherEditor));

        table.setModel(new EventTableModel(filterList,tableFormat));
    }


    private void populateDeliveriesTable() {
        ArrayList<Item> deliveries = getDeliveries();

        Object [][] deliveries2D = new Object[deliveries.size()][];
        int i=0;
        for(Item item : deliveries){
            deliveries2D[i] = (item.toArray_deliveries());
            i++;
        }
        itemsSuppliedTable.setModel(new ItemsTableModel(deliveries2D, deliveryTableColumnNames));
    }

    private ArrayList<Item> getDeliveries() {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getSuppliedItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    private ArrayList<Item> getDeliveries(int transactionId) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getSuppliedItems(transactionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
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

    private ArrayList<Distributor> getDistributors() {
        ArrayList<Distributor> distributors = new ArrayList<>();
        try {
            distributors = mAcess.getDistributors();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return distributors;
    }

    private ArrayList<CustomerStatement> getCustomerStatements() {
        ArrayList<CustomerStatement> customerStatements = new ArrayList<>();
        try {
            customerStatements = mAcess.getCustomerStatements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customerStatements;
    }

    private ArrayList<SupplierTransaction> getSupplierTransactions() {
        ArrayList<SupplierTransaction> supplierTransactions = new ArrayList<>();
        try {
            supplierTransactions = mAcess.getSupplierTransactions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supplierTransactions;
    }

    private ArrayList<SupplierTransaction> getSupplierTransactions(int supplierId, String startDate, String endDate) {
        ArrayList<SupplierTransaction> supplierTransactions = new ArrayList<>();
        try {
            supplierTransactions = mAcess.getSupplierTransactions(supplierId, startDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return supplierTransactions;
    }

    private ArrayList<Item> getItemsSoldOnCredit(int transactionId) {
        ArrayList<Item> items = new ArrayList<>();
        try {
            items = mAcess.getItemsSoldOnCredit(transactionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }


    private ArrayList<Supplier> getSuppliers(){
        ArrayList<Supplier> suppliers = new ArrayList<>();
        try {
            suppliers = mAcess.getSuppliers();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    private ArrayList<StockItem> getStock(){
        ArrayList<StockItem> stockItems = new ArrayList<>();
        try {
            stockItems = mAcess.getStock();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockItems;
    }

    private static ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            products = mAcess.getAllProducts();
            Product product = new Product();
            products.add(0,product);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

    //TODO: Remove this clone
    private  ArrayList<Product> getProducts2() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            MySqlAccess mAcess2 = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,settingsParser.getServerIp());
            products = mAcess2.getAllProducts();
            Product product = new Product();
            products.add(0,product);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }




   /* private void populateCategoriesTable(){
        ArrayList<Category> categories = new ArrayList<>();
        try{
            categories = mAcess.getProductCategories();
        }catch (Exception e){
            e.printStackTrace();
        }

        Object[][] categories2D = new Object[categories.size()][];
        int i=0;
        for(Category category : categories){
            categories2D[i] = category.toArray();
            i++;
        }
        categoriesTable.setModel(new ItemsTableModel(categories2D,categoryTableColumnNames));

    }*/


    private void resetFormFields(JPanel panel){
        for(Component control : panel.getComponents())
        {
            if(control instanceof JTextField)
            {
                JTextField ctrl = (JTextField) control;
                ctrl.setText("");
            }
            else if (control instanceof JComboBox)
            {
                JComboBox ctr = (JComboBox) control;
                ctr.setSelectedIndex(-1);

            }
            else if (control instanceof JTextArea)
            {
                JTextArea ctr = (JTextArea) control;
                ctr.setText("");
            }
            else if (control instanceof JCheckBox)
            {
                JCheckBox ctr = (JCheckBox) control;
                ctr.setSelected(false);
            }
        }
    }

    public class RefreshAction extends AbstractAction {

        public RefreshAction(String text, Integer mnemonic){
            super(text);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();

            //refresh categories combo box
            categoryComboBox.setModel(new DefaultComboBoxModel());
            populateCategoriesComboBox();

            //refresh categories and products tables
            if(button == refreshCategoriesButton) {
               // ((ItemsTableModel) categoriesTable.getModel()).clearTable();
                //populateCategoriesTable();
            }else if(button == refreshProductsButton){
                refreshProductsTable();
//                EventList<Product> products = GlazedLists.eventList(allProducts);
//
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        support = AutoCompleteSupport.install(productsComboBox,products, new ProductsTextFilterator());
//                    }
//                });

            }
//            else if(button == refreshStockButton){
//                ((ItemsTableModel) stockTable.getModel()).clearTable();
//                populateStockTable();
//
//                //TODO: REFRESH PRODUCT COMBOBOX - fix auto complete suppport
//                //re-populate products combobox
//                //ArrayList<Product> allProducts = getProducts();
//                //ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
//                //productsComboBox.setModel(cbModel);
//                //ComboBoxModel cbModel = productsComboBox.getModel();
//            }
        }
    }

    private void refreshProductsTable() {
        //((ItemsTableModel) productsTable2.getModel()).clearTable();
        populateProductsTable();
        //  support.uninstall();

        //re-populate products combobox
        ArrayList<Product> allProducts = getProducts();
        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        //productsComboBox.setModel(cbModel); //affects autocomplete
    }

    public class CancelAction extends AbstractAction{

        public CancelAction(String text, Integer mnemonic){
            super(text);
            putValue(MNEMONIC_KEY,mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            cancelOperation(e);
        }
    }

    private void cancelOperation(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        if(button==cancelProductAddButton) {
            resetFormFields(addProductPanel);
            resetFormFields(costPanel);
            resetFormFields(unitsPanel);
            submitProductAddButton.setText("Add");
            thresholdtextField.setText("1");
            //submitProductAddButton.setBackground(new Color(214,217,223));
        }else if (button == cancelButton){
            resetFormFields(categoryPanel);
            submitButton.setText("Add");
            submitButton.setBackground(new Color(214,217,223));
        }else if (button==cancelStockButton){
            resetFormFields(stockPanel);
            submitStockButton.setText("Add");
        }
    }

    //***********************Sales Report Code begins here**************************

    private void populateProductslist() {
        ArrayList<Product> allProducts = getProducts();
        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        listProducts.setModel(cbModel);
    }

    //create a pie chart from sales summary
    private void createPieChart(String startDate, String endDate) {

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        try {
            Double grandTotal = mAcess.getTotalRevenue(startDate, endDate);
            ArrayList<Item> items = mAcess.getItemsSoldSummary(startDate, endDate);
            totalSalesformattedTextField.setValue(grandTotal);
            for(Item item : items){
                Double percentage = (item.getTotalPrice()/grandTotal)*100;
                pieChartData.add(new PieChart.Data(item.getProductName() + "(" + Math.round(percentage) + "%)",
                        percentage));
            }

            final PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Sales Revenue");

            Scene scene  = new Scene(chart,800,600);

            chartFxPanel.setScene(scene);
            salesPanel.removeAll();
            salesPanel.add(chartFxPanel);
            salesPanel.setVisible(false);
            salesPanel.setVisible(true);


        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    //create a bar chart of sales summary
    private void createbarChart(String startDate, String endDate) {
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();
        bc = new BarChart<>(xAxis,yAxis);

        bc.setTitle("Product sales");
        xAxis.setLabel("Product");
        yAxis.setLabel("Revenue");

        ArrayList<Item> items = null;

        try {
            items = mAcess.getItemsSoldSummary(startDate, endDate);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        XYChart.Series series = new XYChart.Series();
        for(Item item : items){
            series.getData().add(new XYChart.Data(item.getProductName(), item.getTotalPrice()));
        }

        Scene scene  = new Scene(bc,800,600);
        bc.getData().add(series);
        chartFxPanel.setScene(scene);
        salesPanel.removeAll();
        salesPanel.add(chartFxPanel);
        salesPanel.setVisible(false);
        salesPanel.setVisible(true);
    }

    //create linechart of sales revenue by date
    private void createLineChartForAllProducts(String startDate, String endDate) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final LineChart<String,Number> lineChart =
                new LineChart<String, Number>(xAxis,yAxis);

        lineChart.setTitle("Revenue by date");
        xAxis.setLabel("Date");
        yAxis.setLabel("Revenue");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            ArrayList<Hashtable<String, Object>> sales = mAcess.getSalesbyDate(startDate, endDate);

            XYChart.Series series = new XYChart.Series();
            series.setName("All products");

            for(Hashtable<String, Object> sale : sales){
                series.getData().add(new XYChart.Data(dateFormat.format(sale.get("date")), sale.get("sum")));
            }

            Scene scene  = new Scene(lineChart,800,600);
            lineChart.getData().add(series);
            chartFxPanel.setScene(scene);
            salesPanel.removeAll();
            salesPanel.add(chartFxPanel);
            salesPanel.setVisible(false);
            salesPanel.setVisible(true);

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    //create linechart of sales revenue by date
    private void createLineChart_Revenue_GivenProduct(String startDate, String endDate, ArrayList<Product> products) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final LineChart<String,Number> lineChart =
                new LineChart<String, Number>(xAxis,yAxis);

        lineChart.setTitle("Product Sales by date");
        xAxis.setLabel("Date");
        yAxis.setLabel("Revenue");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {

            for(Product product : products){
                ArrayList<Hashtable<String, Object>> sales = mAcess.getSalesForProductByDate(startDate, endDate, product.getProductId());

                XYChart.Series series = new XYChart.Series();
                series.setName(product.getProductName());

                for(Hashtable<String, Object> sale : sales){
                    series.getData().add(new XYChart.Data(dateFormat.format(sale.get("date")), sale.get("sum")));
                }
                lineChart.getData().add(series);
            }

            Scene scene  = new Scene(lineChart,800,600);
            chartFxPanel.setScene(scene);
            salesPanel.removeAll();
            salesPanel.add(chartFxPanel);
            salesPanel.setVisible(false);
            salesPanel.setVisible(true);

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    //create linechart of quantity of sold for a given product
    private void createLineChart_QuantitySold_GivenProduct(String startDate, String endDate, ArrayList<Product> products) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        final LineChart<String,Number> lineChart =
                new LineChart<String, Number>(xAxis,yAxis);

        lineChart.setTitle("Product Sales by date");
        xAxis.setLabel("Date");
        yAxis.setLabel("Quantity");

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            for(Product product : products){
                ArrayList<Hashtable<String, Object>> sales = mAcess.getSalesForProductByDate(startDate, endDate, product.getProductId());
                XYChart.Series series = new XYChart.Series();
                series.setName(product.getProductName());

                for(Hashtable<String, Object> sale : sales){
                    series.getData().add(new XYChart.Data(dateFormat.format(sale.get("date")), sale.get("productquantity")));
                }

                lineChart.getData().add(series);
            }

            Scene scene  = new Scene(lineChart,800,600);

            chartFxPanel.setScene(scene);
            salesPanel.removeAll();
            salesPanel.add(chartFxPanel);
            salesPanel.setVisible(false);
            salesPanel.setVisible(true);

        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }


    private void populateReceiptTable(Query query) {


        ArrayList<Receipt> receipts = getReceipts(query);

        EventList eventList = new BasicEventList();
        eventList.addAll(receipts);

        String [] receiptColumLabels = {"Receipt no.","Cash Received", "Change", "Invoice no.","Inv_Date", "Balance", "Till" ,
                "Cashier","Rec_Date" };
        String [] receiptPropertyNames = {"receiptId", "cashReceived", "change", "invoice_id","invoice_date_created", "balance", "tillnumber",
                "cashierName", "date_created"};
        boolean [] editable = {false,false, false,false,false,false, false,false,false};

        TableFormat tableFormat = GlazedLists.tableFormat(Receipt.class, receiptPropertyNames, receiptColumLabels,
                editable);
        receiptsTable.setModel(new EventTableModel(eventList,tableFormat));
        //receiptsTable.getColumnModel().removeColumn(receiptsTable.getColumnModel().getColumn(4)); //hide invoice no
        receiptsTable.getColumnModel().removeColumn(receiptsTable.getColumnModel().getColumn(5)); //hide balance

        ArrayList<Integer>deletedInvoices = new ArrayList<>();

        try {
            deletedInvoices = mAcess.getDeletedInvoices();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        receiptsTable.getColumnModel().getColumn(0).setCellRenderer(new HiglightReceiptTableCellRenderer(deletedInvoices));
        receiptsTable.getColumnModel().getColumn(3).setCellRenderer(new HiglightReceiptTableCellRenderer(deletedInvoices));
        receiptsTable.getColumnModel().getColumn(4).setCellRenderer(new DateTableCellRenderer());
        receiptsTable.getColumnModel().getColumn(7).setCellRenderer(new DateTableCellRenderer());


    }

    private ArrayList<Receipt> getReceipts(Query query) {
        ArrayList<Receipt> receipts = new ArrayList<>();
        try {
            receipts = mAcess.getReceipts(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receipts;
    }





    ////////***********************




    private static void createScene() {
        startDatePicker = new DatePicker();
        datePickerFXPanel.setScene(new Scene(startDatePicker));

        stopDatePicker = new DatePicker();
        stopDatePickerFXPanel.setScene(new Scene(stopDatePicker));

        fromDatePicker = new DatePicker();
        fromdatePickerFXPanel.setScene(new Scene(fromDatePicker));

        toDatePicker = new DatePicker();
        toDatePickerFXPanel.setScene(new Scene(toDatePicker));

        startdateReceiptsDatePicker = new DatePicker();
        startdateReceiptsPickerFXPanel.setScene(new Scene(startdateReceiptsDatePicker));

        endDateReceiptstoDatePicker = new DatePicker();
        endDateReceiptsPickerFXPanel.setScene(new Scene(endDateReceiptstoDatePicker));

        startDateSupStatementPicker = new DatePicker();
        startDatePickerSupStatementFXPanel.setScene(new Scene(startDateSupStatementPicker));

        stopDateSupStatementPicker = new DatePicker();
        stopDatePickerSupStatementFXPanel.setScene(new Scene(stopDateSupStatementPicker));

        startDateCustStatementPicker = new DatePicker();
        startDatePickerCustStatementFXPanel.setScene(new Scene(startDateCustStatementPicker));

        stopDateCustStatementPicker = new DatePicker();
        stopDatePickerCustStatementFXPanel.setScene(new Scene(stopDateCustStatementPicker));
    }

    public static void init(){
//        chartFxPanel = new JFXPanel();

        ProductCategory frame = new ProductCategory("Administration");
        frame.setContentPane(frame.productCategoryPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(700,400));

        chartFxPanel = new JFXPanel();

        // create JavaFX scene
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createScene();
            }
        });
    }



    public static void main(String[] args) {
        //UIManager.put("nimbusBlueGrey", new Color(242,242,242));
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });

        loggedInUser=args[0];
        System.out.println(loggedInUser);


    }

    private class ProductsTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {

                int row = productsTable2.getSelectedRow();
                productId = (int) productsTable2.getModel().getValueAt(row, 0);
                Product product = mAcess.getProduct(productId);
                productNametextField.setText(product.getProductName());
                descriptionTextField.setText(product.getDescription());
                costPriceTextField.setText(Double.toString(product.getCostprice()));
                vatCheckBox.setSelected(product.isVatable());
                priceTextField.setText(Double.toString(product.getPrice()));
                thresholdtextField.setText(Double.toString(product.getStockLowThreshold()));
                barcodeTextField.setText(product.getBarcode());
                commentTextArea.setText(product.getComment());


                categoryComboBox.setSelectedItem(product.getCategory());
                unitsComboBox.setSelectedItem(mAcess.getUnit(product.getUnits()));

                submitProductAddButton.setText("Update");
                //submitProductAddButton.setBackground(Color.ORANGE);

            }catch (ArrayIndexOutOfBoundsException e1){
                System.out.println("new product being entered, should not select it");
            }catch (Exception e1){
                e1.printStackTrace();
            }


        }
    }

    //TODO: merge these 3 renderers into 1
    //chages font color to red in receipts table when the invoice date is different from receipt date
    public class DateTableCellRenderer extends DefaultTableCellRenderer {

        public DateTableCellRenderer() {
            //setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = NumberFormat.getNumberInstance().format(value);
            }

            String inv_date = ((String)table.getModel().getValueAt(row, 4)).substring(0,11);
            String rec_date = ((String)table.getModel().getValueAt(row, 8)).substring(0,11);




            if (!inv_date.equals(rec_date)) {
                //setBackground(Color.ORANGE);
                setForeground(Color.red);
            }
            else {
//                if (row % 2 == 0) {
//                    setBackground(tableEvenRowColor);
//                    setForeground(primaryTextColor);
//                }
//                else {
//                    setBackground(Color.white);
//                    setForeground(table.getForeground());
//                }
                setForeground(table.getForeground());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    //chages font color  in receipts table when the items have been deleted from invoice
    public class HiglightReceiptTableCellRenderer extends DefaultTableCellRenderer {

        private ArrayList<Integer>deletedInvoices = new ArrayList<>();

        public HiglightReceiptTableCellRenderer(ArrayList<Integer>deletedInvoices) {
            //setHorizontalAlignment(JLabel.RIGHT);
            this.deletedInvoices=deletedInvoices;

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            if (value instanceof Number) {
//                value = NumberFormat.getNumberInstance().format(value);
//            }


            Integer invoiceId = (Integer)table.getModel().getValueAt(row, 3);

           if(deletedInvoices.contains(invoiceId)){
                setForeground(Color.MAGENTA);
           } else {
                setForeground(table.getForeground());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    //chages font color to red in customer statement table when transaction type is Del
    public class CustomerStatementTableCellRenderer extends DefaultTableCellRenderer {

        public CustomerStatementTableCellRenderer() {
            //setHorizontalAlignment(JLabel.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = NumberFormat.getNumberInstance().format(value);
            }

            String type = (String)table.getModel().getValueAt(row, 3);

            if (type.equals("Del")) {
                setForeground(Color.red);
            }
            else {

                setForeground(table.getForeground());
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }

    }

    private class StockTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {

                int row = stockTable.getSelectedRow();
                productId = (int) stockTable.getModel().getValueAt(row, 0);
                String comment = (String) stockTable.getModel().getValueAt(row, 6);
                Product product = mAcess.getProduct(productId);
                productsComboBox.setSelectedItem(product);
                stockCommentsTextArea.setText(comment);

                int quantity = (int) stockTable.getModel().getValueAt(row, 2);
                quantityTextField.setText(Integer.toString(quantity));
                stockTransactionId = (int) stockTable.getModel().getValueAt(row, 5);

                submitStockButton.setText("Update");

            }catch (ArrayIndexOutOfBoundsException e1){
                System.out.println("new stock being entered, should not select it");
            }catch (Exception e1){
                e1.printStackTrace();
            }


        }
    }

    private class CustomerStatementTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {
                int row = customerStatementTable.getSelectedRow();
                //int transactionId = (int) customerStatementTable.getModel().getValueAt(row, 0);
                //populateTransactionDetailTable(transactionId);
                String type = (String) customerStatementTable.getModel().getValueAt(row, 3);
                if(type.equals("Inv")) {
                    populateInvoiceTable();
                }else {
                    itemsEventList.clear();
                }
            }catch (ArrayIndexOutOfBoundsException e1){
                e1.printStackTrace();
            }catch (Exception e1){
                e1.printStackTrace();
            }


        }
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




    private class SupplierTransactionTableRowListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int row = supplierTransactionsTable.getSelectedRow();
            try {
                //TODO: -1 is used to prevent arrayout of bounds exception when createItemSuppliedTableModelListener() calls the populateSupplyTransactionsTable();
                //TODO: the cleaner way to refresh the supplierTransactions table is to update the arraylist used to populate it.
                if(row !=-1) {
                    int transactionId = (int) supplierTransactionsTable.getModel().getValueAt(row, 0);
                    populateDeliveryTable(transactionId);
                    createItemSuppliedTableModelListener();
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





    private class ListSelectionHandler implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            try {
                JList categoriesList = (JList) e.getSource();
                Category category = (Category) categoriesList.getSelectedValue();
                categoryNametextField.setText(category.getCategoryName());
                descriptiontextArea.setText(category.getDescription());
                categoryId = category.getCategoryId();

                submitButton.setText("Update");
                submitButton.setBackground(Color.orange);
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }
    }


    public static class CustomerTextFilterator implements TextFilterator<Customer> {
        @Override
        public void getFilterStrings(List<String> baseList, Customer customer) {
            baseList.add(customer.getFirstname());
            baseList.add(customer.getLastname());
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
}
