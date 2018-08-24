package sell_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.Item;
import model.Product;
import model.Transaction;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import print.ReceiptHeader;
import print.ReceiptPrinter;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class UI extends JFrame implements KeyListener ,TableModelListener {
    private JPanel mainPanel;
    private JPanel cartPanel;
    private JTable itemsTable;
    private JComboBox productsComboBox;
    //private JButton submitButton;
    private JPanel productPanel;
    private JPanel inputPanel;
    private JPanel totalPanel;
    private JFormattedTextField totalFormattedTextField;
    private JScrollPane tableScrollPane;
    private JPanel buttonPanel;
    private JFormattedTextField priceformattedTextField;
    private JPanel sidePanel;
    private JFormattedTextField changeformattedTextField;
    private JPanel changePanel;
    private JFormattedTextField cashformattedTextField;
    private JPanel testpanel;
    private static MySqlAccess mAcess;
    private Transaction transaction;
    private JDialog parent;
    private static String userName;
    private static Double grandTotal;
    private int rowNumber = 0;
    ProductDialog productsDialog;
    private Color primaryLightColor;
    private  Color primaryTextColor;
    private Color primaryColor;

    private String barcode = "";
    private final ItemsTableModel itemsTableModel;

    private String[] columnNames = {"No.",
            "Product Name",
            "Quantity",
            "Units",
            "Price",
            "Total Price",
            "product id"};



    public UI(String title) {

        super(title);
        parent = new JDialog();
        grandTotal = 0.0;

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME);
        transaction = new Transaction(userName);

        productsComboBox = new JComboBox();
        productsComboBox.setPreferredSize(new Dimension(300,35));
        //productsDialog = new CustomDialog(this,productsComboBox,this);
        productsDialog = new ProductDialog(this, "Products", productsComboBox);
        productsDialog.setSize(new Dimension(320,150));



        cashformattedTextField.setFocusable(false);
        //productsComboBox.setFocusable(false);

        primaryColor = Color.decode("#00bcd4");
        primaryLightColor = Color.decode("#b2ebf2");
        Color accentColor = Color.decode("#ff5722");
        Color backgroundColor = Color.decode("#FFFFFF");
        Color dividerColor = Color.decode("#BDBDBD");

        Color textColor = Color.decode("#FFFFFF");
        primaryTextColor = Color.decode("#212121");
        Color secondaryTextColor = Color.decode("#757575");

        cartPanel.setBackground(backgroundColor);



        itemsTable.setModel(new ItemsTableModel(columnNames));
        itemsTable.getModel().addTableModelListener(this);
        TableColumnModel columnModel = itemsTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(6)); // hide the productid
        itemsTable.getColumnModel().getColumn(2).setCellEditor(new IntegerEditor(1, 1000000));
        itemsTableModel = (ItemsTableModel) itemsTable.getModel();
        itemsTable.setFocusable(false);
        totalFormattedTextField.setFocusable(false);
        //submitButton.setFocusable(false);
        changeformattedTextField.setFocusable(false);

        alignTableCells();

        //itemsTable.getTableHeader().setOpaque(false);
        itemsTable.getTableHeader().setBackground(primaryColor);
        //itemsTable.getTableHeader().setBackground(new Color(26, 119, 196));
        itemsTable.getTableHeader().setForeground(primaryTextColor);

        itemsTable.getTableHeader().setFont(new Font(null, Font.PLAIN, 15));

        mainPanel.setBackground(backgroundColor);

        itemsTable.setShowGrid(true);
        itemsTable.setGridColor( new Color(194, 224, 242));
        itemsTable.setRowHeight(25);
        itemsTable.setFont(new Font(null, Font.PLAIN, 15));

        itemsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table,
                                                           Object value, boolean isSelected, boolean hasFocus, int row, int col) {

                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                //int quantityStocked = (int)table.getModel().getValueAt(row, 1);
                //int stockThreshold = (int)table.getModel().getValueAt(row, 2);



                if (row % 2 == 0) {
                    setBackground(primaryLightColor);
                    setForeground(primaryTextColor);
                }
                else {
                    setBackground(table.getBackground());
                    setForeground(table.getForeground());
                }
                return this;
            }
        });

        //itemsTable.getTableHeader().setBackground(Color.decode("#00BCD4"));



        itemsTable.getTableHeader().setBackground(primaryColor);
        totalFormattedTextField.setBackground(accentColor);
        totalFormattedTextField.setForeground(textColor);

        sidePanel.setBackground(backgroundColor);





        SubmitAction submitAction = new SubmitAction("OK", new Integer(KeyEvent.VK_ENTER));

        //submitButton.setAction(submitAction);

//        submitButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK),
//                "submit");
//        submitButton.getActionMap().put("submit", submitAction );


//        productsComboBox.setInputVerifier(new ComboNotSelectedValidator(parent,
//                productsComboBox, "Select a product"));


        populateProductsComboBox();

        cashformattedTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Double change =  (Double)cashformattedTextField.getValue()- (Double) totalFormattedTextField.getValue();
                Double change = Double.parseDouble(cashformattedTextField.getText()) -
                        (Double)(totalFormattedTextField.getValue());
                changeformattedTextField.setValue(change);
            }
        });

    }

    private void alignTableCells() {
        itemsTable.getColumnModel().getColumn(0).setMaxWidth(50);
        itemsTable.getColumnModel().getColumn(1).setMaxWidth(350);
        itemsTable.getColumnModel().getColumn(2).setMaxWidth(100);
        itemsTable.getColumnModel().getColumn(3).setMaxWidth(100);
        itemsTable.getColumnModel().getColumn(4).setMaxWidth(200);
        itemsTable.getColumnModel().getColumn(5).setMaxWidth(200);

        //left align column header
        HeaderAlignmentRenderer headerAlignmentRenderer = new HeaderAlignmentRenderer(SwingConstants.LEFT);
        itemsTable.getColumnModel().getColumn(0).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(1).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(2).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(3).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(4).setHeaderRenderer(headerAlignmentRenderer);
        itemsTable.getColumnModel().getColumn(5).setHeaderRenderer(headerAlignmentRenderer);



        //left align cell contents
        DefaultTableCellRenderer leftCellRenderer = new DefaultTableCellRenderer();
        leftCellRenderer.setHorizontalAlignment(SwingConstants.LEFT);
//        itemsTable.getColumnModel().getColumn(0).setCellRenderer(leftCellRenderer);
//        itemsTable.getColumnModel().getColumn(2).setCellRenderer(leftCellRenderer);

        itemsTable.getColumnModel().getColumn(0).setCellRenderer(new NumberTableCellRenderer());
        itemsTable.getColumnModel().getColumn(2).setCellRenderer(new NumberTableCellRenderer());

        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new NumberTableCellRenderer());
        itemsTable.getColumnModel().getColumn(5).setCellRenderer(new NumberTableCellRenderer());
    }

    private void populateProductsComboBox() {
        ArrayList<Product> allProducts = null;
        try {
            allProducts = mAcess.getAllProducts();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ComboBoxModel cbModel = new DefaultComboBoxModel(allProducts.toArray());
        productsComboBox.setModel(cbModel);
        //productsComboBox.setPrototypeDisplayValue("");
        EventList<Product> products = GlazedLists.eventList(allProducts);


        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    AutoCompleteSupport support = AutoCompleteSupport.install(productsComboBox,products, new ProductsTextFilterator());
                    //support.setSelectsTextOnFocusGain(false);
                    //support.setStrict(true);
                    support.setFilterMode(TextMatcherEditor.CONTAINS);


                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        //clear fields
        productsComboBox.setSelectedIndex(-1);
        priceformattedTextField.setText("");
    }

    @Override
    public void keyTyped(KeyEvent e) {

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
                product = mAcess.getProduct(barcode);
            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            }
            addItemToCart(product);
        } else if(KeyStroke.getKeyStroke(KeyEvent.VK_E,InputEvent.CTRL_DOWN_MASK) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)){
            //Edit quantity field
            itemsTable.setFocusable(true);
            itemsTable.requestFocusInWindow();
            int lastrow = itemsTable.getRowCount()-1;
            itemsTable.editCellAt(lastrow,2);
            itemsTable.setFocusable(false);
            itemsTable.transferFocus();
        }
        else if((KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_DOWN_MASK ) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)) && itemsTable.getRowCount()>0){
            //Submit transaction to database
            submitTransaction();
            barcode="";
        }
        else if(e.getKeyCode() == KeyEvent.VK_ENTER && barcode.length()<3) {
            // clear the quantity digits entered that would otherwise be added to the barcode
            //TODO: not full proof as user can enter anything greater than 3 but in not a valid barcode
            barcode="";
        }else if(KeyStroke.getKeyStroke(KeyEvent.VK_P,InputEvent.CTRL_DOWN_MASK) ==
                KeyStroke.getKeyStroke(e.getKeyCode(),InputEvent.CTRL_DOWN_MASK)){
            barcode="";
            productsDialog.setVisible(true);
            Product product = productsDialog.getSelectedProduct();
            if(product != null) {
                addItemToCart(product);
            }
            barcode="";
        } else {
            // some character has been read, append it to to barcode string
            //int id = e.getID();
            //only add unicode characters to barcode
            //if(id == KeyEvent.KEY_TYPED ) {
//                barcode += e.getKeyChar();
//            }

            //when alt+enter combination is entered to submit sales to db, do not add ALT key to barcode string,
            // it will affect next transaction
            //TODO: consider cases of other control characters
            if(e.getKeyCode() != KeyEvent.VK_CONTROL && e.getKeyCode() != KeyEvent.VK_ALT )
                barcode += e.getKeyChar();

        }
    }

    private void addItemToCart(Product product) {
        try {
            int productId = product.getProductId();
            rowNumber++;
            int quantity = 1;
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
            barcode="";
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {

        int row = e.getFirstRow();
        int column = e.getColumn();
        if(column != -1 && column !=5){//-1 to avoid updating non existent column when adding first row;
            // -5 to prevent cyclic updates when total column is updated (leading to stackoverflow error)
            ItemsTableModel model = (ItemsTableModel)e.getSource();
            int quantity = (int) model.getValueAt(row, 2);
            Double price = (Double) model.getValueAt(row,4);
            Double newTotal = quantity * price;
            model.setValueAt(newTotal,row,5);
            grandTotal = computeGrandTotal();
            totalFormattedTextField.setValue(grandTotal);
        }
    }

    private Double computeGrandTotal(){
        Double gtotal = 0.0;
        for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
            gtotal += (Double) row.elementAt(5);
        }
        return  gtotal;
    }


    public static final class ProductsTextFilterator implements TextFilterator<Product>{


        @Override
        public void getFilterStrings(List<String> baseList, Product product) {
            final String productName = product.getProductName();
            baseList.add(productName);
        }
    }

    public class SubmitAction extends AbstractAction {

        public SubmitAction(String text, Integer mnemonic){
            super(text);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            submitTransaction();
        }

    }

    private void submitTransaction() {
        try {

            for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
                Item item = new Item();
                item.setProductName((String)row.elementAt(1));
                item.setQuantity((int)row.elementAt(2));
                item.setPrice((Double) row.elementAt(4));
                item.setProductId((int)row.elementAt(6));
                item.computeTotalPrice();
                transaction.addItem(item);
            }

            int receiptId = mAcess.generateReceiptId(userName);
            transaction.setReceiptId(receiptId);
            mAcess.insertTransaction(transaction);
            ((ItemsTableModel) itemsTable.getModel()).clearTable();

            ReceiptHeader receiptHeader = new ReceiptHeader();
            receiptHeader.setBusinessName("ABC Hardware");
            receiptHeader.setReceiptNumber(receiptId);
            receiptHeader.setTelephoneNumber("0772605800");
            receiptHeader.setUserName("Ivan");

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(new ReceiptPrinter(transaction, receiptHeader));

            //boolean ok = job.printDialog();
            if (true) {
                try {
                    job.print();
                } catch (PrinterException ex) {
          /* The job did not successfully complete */
                }
            }
            grandTotal = 0.0;
            totalFormattedTextField.setText("");
            transaction = new Transaction(userName);
            rowNumber=0;

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
            setBackground(primaryColor);
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
                setBackground(primaryLightColor);
                setForeground(primaryTextColor);
            }
            else {
                setBackground(table.getBackground());
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
                    if (defaults.get("Table.alternateRowColor") == null)
                        defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        userName = args[0];
        UI frame = new UI("Sales Partner");
        frame.setContentPane(frame.mainPanel);
        frame.addKeyListener(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);

    }

}
