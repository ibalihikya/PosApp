package reports;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.DatePicker;
import model.Item;
import model.Product;
import model.StockItem;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import sell_ui.ItemsTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;

public class Reports {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JPanel startDatePanel;
    private JPanel stopDatePanel;
    private JButton searchButton;
    private JPanel stockPanel;
    private JFormattedTextField totalSalesformattedTextField;
    private JButton refreshButton;
    private JButton exportToExcelButton;
    private JPanel salesPanel;
    private JList listProducts;
    private JPanel radiobuttonPanel;
    private JRadioButton detailedRadioButton;
    private JRadioButton summaryRadioButton;
    private JRadioButton chartRadioButton;
    private JRadioButton revenueVsProductBarRadioButton;
    private JRadioButton revenueVsProductPieRadioButton;
    private JRadioButton revenueLineRadioButton;
    private JRadioButton revenueByProductLineRadioButton;
    private JRadioButton quantityByProductLineRadioButton;
    private JPanel chartPanel;
    private JPanel stockStatusPanel;
    private JTextField totalSalesTextField;
    private static JFXPanel datePickerFXPanel;
    private static JFXPanel stopDatePickerFXPanel;
    private static JFXPanel chartFxPanel;
    private static DatePicker startDatePicker;
    private static DatePicker stopDatePicker;
    private static MySqlAccess mAcess;
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
            "Last Changed Date"
    };

    private static CategoryAxis xAxis;
    private static NumberAxis yAxis;
    private static BarChart<String,Number> bc;





    public Reports() {
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME);

        startDatePanel.add(datePickerFXPanel);
        stopDatePanel.add(stopDatePickerFXPanel);

        //populateStockTable();
        populateProductslist();

        salesTable = new JTable();
        salesTable.setFillsViewportHeight(true);
        salesTable.setBackground(Color.WHITE);
        salesTable.setShowGrid(true);
        salesTable.setGridColor(new Color(189, 221, 255));

       //JScrollPane scrollPane = new JScrollPane();
       //salesPanel.add(scrollPane);

        //JScrollPane scrollPane = new JScrollPane(bc);
        //salesPanel.add(chartFxPanel);


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
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                populateStockTable();
            }
        });
        exportToExcelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workbook workbook = new HSSFWorkbook();
                CreationHelper createHelper = workbook.getCreationHelper();
                Sheet sheet = workbook.createSheet("new sheet");


                try {
                    ArrayList<Item> items = mAcess.getItemsSold(startDatePicker.getValue().toString(),
                            stopDatePicker.getValue().toString());
                    Row header = sheet.createRow((short)0);
                    header.createCell(0).setCellValue("Product Name");
                    header.createCell(1).setCellValue("Quantity");
                    header.createCell(2).setCellValue("Price");
                    header.createCell(3).setCellValue("Total");
                    header.createCell(4).setCellValue("Receipt No.");
                    header.createCell(5).setCellValue("Time");

                    int i =1; //counter

                    for(Item item : items){
                        Row row = sheet.createRow((short)i);
                        row.createCell(0).setCellValue(item.getProductName());
                        row.createCell(1).setCellValue(item.getQuantity());
                        row.createCell(2).setCellValue(item.getPrice());
                        row.createCell(3).setCellValue(item.getTotalPrice());
                        row.createCell(4).setCellValue(item.getReceiptId());
                        row.createCell(5).setCellValue(item.getTime());
                        i++;
                    }
                    FileOutputStream fileOut = new FileOutputStream("C:\\Users\\ivan\\Desktop\\receipts\\workbook.xls");
                    workbook.write(fileOut);
                    fileOut.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }catch(NullPointerException e1){
                    System.out.println("Date not selected!");
                }


            }
        });

    }

    private void populateProductslist() {
        ArrayList<Product> allProducts = getProducts();
        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        listProducts.setModel(cbModel);
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





    public static void init(){
        datePickerFXPanel = new JFXPanel();
        datePickerFXPanel.setPreferredSize(new Dimension(20,20));

        stopDatePickerFXPanel = new JFXPanel();
        stopDatePickerFXPanel.setPreferredSize(new Dimension(20,20));

        chartFxPanel = new JFXPanel();

        JFrame frame = new JFrame("Reports");
        frame.setContentPane(new Reports().panel1);
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

    private static void createScene() {
        startDatePicker = new DatePicker();
        datePickerFXPanel.setScene(new Scene(startDatePicker));

        stopDatePicker = new DatePicker();
        stopDatePickerFXPanel.setScene(new Scene(stopDatePicker));
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
    }
}
