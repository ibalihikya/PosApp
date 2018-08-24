package admin_ui;

import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
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
import org.postgresql.util.PSQLException;
import sell_ui.ItemsTableModel;
import validation.ComboNotSelectedValidator;
import validation.NotEmptyNumberValidator;
import validation.NotEmptyValidator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ProductCategory {
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
    private JButton refreshStockButton;
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
    private JPanel stockStatusPanel;
    private JTable stockStatusTable;
    private JButton refreshButton;
    private JTextField thresholdtextField;
    private JDialog parent;
    private static MySqlAccess mAcess;
    private  AutoCompleteSupport support;
    private String[] productsTableColumnNames = {
            "ProductId",
            "Product Name",
            "Additional Info.",
            "Category",
            "Price",
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
        "Quantity",
        "Date Added",
        "Date Modified",
        "Comment"
    };


    //Sales Report variables
    private static JFXPanel datePickerFXPanel;
    private static JFXPanel stopDatePickerFXPanel;
    private static JFXPanel chartFxPanel;
    private static DatePicker startDatePicker;
    private static DatePicker stopDatePicker;
    private final JTable salesTable;

    private String[] salesTableColumnNames = {
            "Product Name",
            "Quantity",
            "Price",
            "Total",
            "ReceiptNo",
            "Date"
    };

    private String[] salesSummaryTableColumnNames = {
            "Product Name",
            "Quantity",
            "Total"
    };

    private String[] stockStatusTableColumnNames = {
            "Product Name",
            "Quantity",
            "Threshold",
            "Last Changed Date"
    };

    private static CategoryAxis xAxis;
    private static NumberAxis yAxis;
    private static BarChart<String,Number> bc;



    public ProductCategory() {
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME);
        parent = new JDialog(); // for validation error messages

        salesTable = new JTable();
        salesTable.setFillsViewportHeight(true);
        salesTable.setBackground(Color.WHITE);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(189, 221, 255));

        //Highlight products that are low in stock
        stockStatusTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                int quantityStocked = (int)table.getModel().getValueAt(row, 1);
                int stockThreshold = (int)table.getModel().getValueAt(row, 2);

                if (quantityStocked<=stockThreshold) {
                    setBackground(Color.orange);
                    setForeground(Color.BLACK);
                }
                else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });


        datePickerFXPanel = new JFXPanel();
        datePickerFXPanel.setPreferredSize(new Dimension(20,20));

        stopDatePickerFXPanel = new JFXPanel();
        stopDatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        startDatePanel.add(datePickerFXPanel);
        stopDatePanel.add(stopDatePickerFXPanel);

        populateStockTable();
        populateProductslist();

        populateProductsTable();
       // populateCategoriesTable();
       populateStockStatusTable();

        populateProductsComboBox();

        //input validation
        categoryNametextField.setInputVerifier(new NotEmptyValidator(parent,categoryNametextField,"Enter category!"));
        productNametextField.setInputVerifier(new NotEmptyValidator(parent,productNametextField,"Enter product name!"));
        //descriptionTextField.setInputVerifier(new NotEmptyValidator(parent,descriptionTextField,"Enter text!"));
        priceTextField.setInputVerifier(new NotEmptyNumberValidator(parent,priceTextField, "Invalid price!"));
        unitsComboBox.setInputVerifier(new ComboNotSelectedValidator(parent,unitsComboBox, "Select a unit!"));
        productsComboBox.setInputVerifier(new ComboNotSelectedValidator(parent,productsComboBox, "Select a product"));
        quantityTextField.setInputVerifier(new NotEmptyNumberValidator(parent,quantityTextField,"Enter quantity"));

        //refresh products and categories tables
        RefreshAction refreshAction = new RefreshAction("Refresh", new Integer(KeyEvent.VK_R));
        //refreshProductsButton.setAction(refreshAction);
        //refreshCategoriesButton.setAction(refreshAction);
        refreshStockButton.setAction(refreshAction);

        //reset form fields when cancel button is clicked
          CancelAction cancelAction = new CancelAction("Cancel", new Integer(KeyEvent.VK_C));
         cancelButton.setAction(cancelAction);            //cancel button in category panel
         cancelProductAddButton.setAction(cancelAction); //cancel button in product panel
          cancelStockButton.setAction(cancelAction);      //cancel button in stockpanel



        populateCategoriesComboBox();


        ArrayList<Unit> units = null;
        try {
            units = mAcess.getAllUnits();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ComboBoxModel cbUnitsModel = new DefaultComboBoxModel(units.toArray());
        unitsComboBox.setModel(cbUnitsModel);
        unitsComboBox.setSelectedIndex(-1);

        //add a category to the database
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String categoryName = categoryNametextField.getText().trim();
                String description = descriptiontextArea.getText().trim();
                Category category = new Category(categoryName);
                category.setDescription(description);
                try {
                    mAcess.createProductCategory(category);
                    resetFormFields(categoryPanel);
                    populateCategoriesComboBox();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });

        //add a product to the database
        submitProductAddButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Category category;
                if (categoryComboBox.getSelectedIndex() == -1) {
                    category = new Category("");
                }else {
                    category = (Category) categoryComboBox.getSelectedItem();
                }

                try {

                    Product product = new Product();
                    product.setProductName(productNametextField.getText().trim());
                    product.setCategory(category);
                    product.setDescription(descriptionTextField.getText().trim());
                    product.setPrice(Double.parseDouble(priceTextField.getText().trim()));
                    product.setUnits(((Unit)unitsComboBox.getSelectedItem()).getUnitName());
                    product.setComment(commentTextArea.getText().trim());
                    product.setBarcode(barcodeTextField.getText().trim());
                    product.setStockLowThreshold(Integer.parseInt(thresholdtextField.getText().trim()));
                    mAcess.insertProduct(product);
                    resetFormFields(addProductPanel);
                    refreshProductsTable();

                } catch (NumberFormatException ex){
                    System.out.println("Invalid price!");
                }catch (NullPointerException ex){
                    System.out.println("Units not selected!");
                }catch(PSQLException ex) {
                    System.out.println("Product name already exists");
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


        submitStockButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    int productId= ((Product) productsComboBox.getSelectedItem()).getProductId();
                    int quantity = Integer.parseInt(quantityTextField.getText().trim());
                    String comment = stockCommentsTextArea.getText().trim();
                    mAcess.addStock(1,productId, quantity,comment);
                    resetFormFields(stockPanel);
                } catch (NullPointerException e1) {
                    System.out.println("Product not selected!");
                }  catch (NumberFormatException ex) {
                    System.out.println("Invalid number!");
                }catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });


        tabbedPane.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                if(tabbedPane.getSelectedIndex() == 1){
                    ((NotEmptyValidator)productNametextField.getInputVerifier()).setPopUpVisible(false);
                   // ((NotEmptyValidator)descriptionTextField.getInputVerifier()).setPopUpVisible(false);
                    ((NotEmptyNumberValidator)priceTextField.getInputVerifier()).setPopUpVisible(false);
                    ((ComboNotSelectedValidator)unitsComboBox.getInputVerifier()).setPopUpVisible(false);
                    ((NotEmptyValidator)categoryNametextField.getInputVerifier()).setPopUpVisible(false);

                }else if(tabbedPane.getSelectedIndex() == 0){
                    ((ComboNotSelectedValidator)productsComboBox.getInputVerifier()).setPopUpVisible(false);
                    ((NotEmptyNumberValidator)quantityTextField.getInputVerifier()).setPopUpVisible(false);
                }
            }
        });

        //**********************stock status report event handlers***********************
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateStockStatusTable();
            }
        });

        //*****************Sales Report event handlers***********************************
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    if(detailedRadioButton.isSelected()==true) {
                        //show detailed view
                        ArrayList<Item> items = mAcess.getItemsSold(startDatePicker.getValue().toString(),
                                stopDatePicker.getValue().toString());

                        Object[][] items2D = new Object[items.size()][];
                        int i = 0;
                        Double totalSales = 0.0;
                        for (Item item : items) {
                            items2D[i] = item.toArray();
                            totalSales += item.getTotalPrice();
                            i++;
                        }
                        totalSalesformattedTextField.setValue(totalSales);

                        salesTable.setModel(new ItemsTableModel(items2D, salesTableColumnNames));
                        JScrollPane scrollPane = new JScrollPane(salesTable);
                        salesPanel.removeAll();
                        salesPanel.add(scrollPane);

                        //TODO: find a solution for the display quirk; tentatively solved below. Perhaps run on a separate thread: platform.runlater()
                        salesPanel.setVisible(false);
                        salesPanel.setVisible(true);

                    }else if(summaryRadioButton.isSelected()){
                        //show summary view
                        ArrayList<Item> items = mAcess.getItemsSoldSummary(startDatePicker.getValue().toString(),
                                stopDatePicker.getValue().toString());
                        Object[][] items2D = new Object[items.size()][];
                        int i = 0;
                        Double totalSales = 0.0;
                        for (Item item : items) {
                            items2D[i] = item.toArray_summary();
                            totalSales += item.getTotalPrice();
                            i++;
                        }
                        totalSalesformattedTextField.setValue(totalSales);
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

    }

    private void populateProductsComboBox() {
        ArrayList<Product> allProducts = getProducts();
        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        productsComboBox.setModel(cbModel);
//        EventList<Product> products = GlazedLists.eventList(allProducts);
//
//        try {
//            SwingUtilities.invokeAndWait(new Runnable() {
//                @Override
//                public void run() {
//                    support = AutoCompleteSupport.install(productsComboBox,products, new ProductsTextFilterator());
//                }
//            });
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
    }

    public static final class ProductsTextFilterator implements TextFilterator<Product> {
        @Override
        public void getFilterStrings(List<String> baseList, Product product) {
            final String productName = product.getProductName();
            baseList.add(productName);
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

    private void populateProductsTable() {
        ArrayList<Product> products = getProducts();
        Object [][] products2D = new Object[products.size()][];
        int i=0;
        for(Product product : products){
            products2D[i] = (product.toArray());
            i++;
        }
        productsTable.setModel(new ItemsTableModel(products2D, productsTableColumnNames));
    }

    private void populateStockTable(){
        ArrayList<StockItem> stockItems = getStock();
        Object[][] stock2D = new Object[stockItems.size()][];
        int i = 0;
        for(StockItem stockItem : stockItems){
            stock2D[i] = stockItem.toArray();
            i++;
        }
        stockTable.setModel(new ItemsTableModel(stock2D,stockTableColumnNames));
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

    private ArrayList<Product> getProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            products = mAcess.getAllProducts();
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

            }else if(button == refreshStockButton){
                ((ItemsTableModel) stockTable.getModel()).clearTable();
                populateStockTable();

                //re-populate products combobox
                ArrayList<Product> allProducts = getProducts();
                ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
                productsComboBox.setModel(cbModel);
            }
        }
    }

    private void refreshProductsTable() {
        ((ItemsTableModel) productsTable.getModel()).clearTable();
        populateProductsTable();
        //  support.uninstall();

        //re-populate products combobox
        ArrayList<Product> allProducts = getProducts();
        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        productsComboBox.setModel(cbModel);
    }

    public class CancelAction extends AbstractAction{

        public CancelAction(String text, Integer mnemonic){
            super(text);
            putValue(MNEMONIC_KEY,mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = (JButton)e.getSource();
            if(button==cancelProductAddButton) {
                resetFormFields(addProductPanel);
            }else if (button == cancelButton){
                resetFormFields(categoryPanel);
            }else if (button==cancelStockButton){
                resetFormFields(stockPanel);
            }
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

    private void populateStockStatusTable(){
        ArrayList<StockItem> stockItems = getStockStatus();
        Object[][] stock2D = new Object[stockItems.size()][];
        int i = 0;
        for(StockItem stockItem : stockItems){
            stock2D[i] = stockItem.toArray2();
            i++;
        }
        stockStatusTable.setModel(new ItemsTableModel(stock2D,stockStatusTableColumnNames));
    }

    private ArrayList<StockItem> getStockStatus(){
        ArrayList<StockItem> stockItems = new ArrayList<>();
        try {
            stockItems = mAcess.getStockStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockItems;
    }

    ////////***********************




    private static void createScene() {
        startDatePicker = new DatePicker();
        datePickerFXPanel.setScene(new Scene(startDatePicker));

        stopDatePicker = new DatePicker();
        stopDatePickerFXPanel.setScene(new Scene(stopDatePicker));


    }

    public static void init(){
//        datePickerFXPanel = new JFXPanel();
//        datePickerFXPanel.setPreferredSize(new Dimension(20,20));
//
//        stopDatePickerFXPanel = new JFXPanel();
//        stopDatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        chartFxPanel = new JFXPanel();

        JFrame frame = new JFrame("Administration");
        frame.setContentPane(new ProductCategory().productCategoryPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

        // create JavaFX scene
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                createScene();
            }
        });
    }

    public static void main(String[] args) {
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

        /*JFrame frame = new JFrame("Administration");
        frame.setContentPane(new ProductCategory().productCategoryPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        frame.pack();

        frame.setVisible(true);*/
    }
}
