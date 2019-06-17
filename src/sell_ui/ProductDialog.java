package sell_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.ThreadedMatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TextComponentMatcherEditor;
import model.Product;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

import static javax.swing.ListSelectionModel.SINGLE_SELECTION;

public class ProductDialog extends JDialog {
    //TODO: ADD multiple selection to table so that a user can enter multiple items at once without having to keep opening the dialog
    //user should be able to enter quantities from the dialog or on pressing enter quantity should shift to qty cell.
    private Product product = null;
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private int productId;
    private JTable productsTable;
    private static MySqlAccess mAcess;

    public ProductDialog(JFrame frame, String title, EventList products, String serverIp) {
        super(frame, title, true);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,  serverIp);

        JLabel shortcutsLabel = new JLabel("Navigate to table: TAB" + " |  " + "Cancel: ALT+C" + " |  " + "OK: Enter");
        shortcutsLabel.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(shortcutsLabel);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JRootPane rootPane = new JRootPane();
        rootPane.setDefaultButton(okButton);

        JTextField filterField = new JTextField(25);

        TextFilterator productFilterator = new TextFilterator() {
            public void getFilterStrings(List baseList, Object element) {
                Product product = (Product) element;
                baseList.add(product.getProductName());
                baseList.add(product.getPrice());
                baseList.add(product.getUnits());
                baseList.add(product.getCategory());
                baseList.add(product.getBarcode());
                baseList.add(product.getProductId());
                baseList.add(product.getDescription());
            }
        };
        TextComponentMatcherEditor matcherEditor = new TextComponentMatcherEditor(filterField, productFilterator);
        FilterList filteredProducts = new FilterList(products, new ThreadedMatcherEditor(matcherEditor));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filterPanel.add(new JLabel("Filter:"));
        filterPanel.add(filterField);

        String[] propertyNames = new String[] {"productId","productName", "price", "units", "category", "description","barcode"};
        String[] columnLabels = new String[] {"Product no.","Product", "Price", "Units", "Category", "Description", "Barcode"};
        TableFormat tableFormat = GlazedLists.tableFormat(Product.class, propertyNames, columnLabels);
        productsTable = new JTable(new EventTableModel(filteredProducts, tableFormat));
        productsTable.setRowSelectionAllowed(true);
        productsTable.setSelectionMode(SINGLE_SELECTION);

        productsTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        productsTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        productsTable.getColumnModel().getColumn(2).setPreferredWidth(40);
        productsTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        productsTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        productsTable.getColumnModel().getColumn(5).setPreferredWidth(40);
        productsTable.getColumnModel().getColumn(6).setPreferredWidth(20);


        //https://stackoverflow.com/questions/14852719/double-click-listener-on-jtable-in-java
        productsTable.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                JTable table =(JTable) mouseEvent.getSource();
                Point point = mouseEvent.getPoint();
                int row = table.rowAtPoint(point);
                if (mouseEvent.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    onOK();
                }
            }
        });

        setLayout(new BorderLayout());
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(productsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        filterField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                productsTable.clearSelection();

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_DOWN){
                    productsTable.requestFocusInWindow();
                    productsTable.setRowSelectionInterval(0,0);
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });


        CancelAction cancelAction = new CancelAction("Cancel", new Integer(KeyEvent.VK_C), filterField,productsTable);
        cancelButton.setAction(cancelAction);

        OKAction okAction = new OKAction("OK", filterField, productsTable);
        okButton.setAction(okAction);

        EscAction escAction = new EscAction(filterField);

        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"ok");
        okButton.getActionMap().put("ok", okAction);

        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"cancel");
        cancelButton.getActionMap().put("cancel",cancelAction );


        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        productsTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "ok");
        productsTable.getActionMap().put("ok",okAction);

        KeyStroke search = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        productsTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(search, "search");
        productsTable.getActionMap().put("search", escAction);


                pack();
        setLocationRelativeTo(frame);
    }


    public Product getSelectedProduct() {
        return product;
    }

    public class CancelAction extends AbstractAction{
        //private  JTextField textField;
        private JTable table;

        public CancelAction(String text, Integer mnemonic, JTextField textField, JTable table){
            super(text);
            //this.textField = textField;
            this.table = table;
            putValue(MNEMONIC_KEY, mnemonic);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            product = null;
            //textField.setText("");
            //textField.requestFocusInWindow();
            table.clearSelection();
            table.getSelectionModel().setLeadSelectionIndex(-1);
            table.getSelectionModel().setAnchorSelectionIndex(-1);
            table.getColumnModel().getSelectionModel().setAnchorSelectionIndex(-1);
            table.getColumnModel().getSelectionModel().setLeadSelectionIndex(-1);
            dispose();
        }
    }

    public class OKAction extends AbstractAction{

        //private  JTextField textField;
        //private JTable table;

        public OKAction(String text, JTextField textField, JTable table){
            super(text);
            //this.textField = textField;
            //this.table = table;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            onOK();
        }


    }

    private void onOK() {
        int row = productsTable.getSelectedRow();
        productId = (int) productsTable.getModel().getValueAt(row, 0);

        try {
            product = mAcess.getProduct(productId);
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        //textField.setText("");
        //textField.requestFocusInWindow();
        productsTable.clearSelection();
        productsTable.getSelectionModel().setLeadSelectionIndex(-1);
        productsTable.getSelectionModel().setAnchorSelectionIndex(-1);
        productsTable.getColumnModel().getSelectionModel().setAnchorSelectionIndex(-1);
        productsTable.getColumnModel().getSelectionModel().setLeadSelectionIndex(-1);
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
            productsTable.clearSelection();
            textField.requestFocusInWindow();
        }
    }

}
