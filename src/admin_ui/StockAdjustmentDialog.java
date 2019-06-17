package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class StockAdjustmentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox productsComboBox;
    private JComboBox locationComboBox;
    private JFormattedTextField expectedQuantityFormattedTextField;
    private JTextArea commentTextArea;
    private JComboBox transactionTypeComboBox;
    private JFormattedTextField quantityFormattedTextField;
    private JTextField refundTextField;
    private JPanel refundPanel;
    private static MySqlAccess mAcess;
    private String username;

    public StockAdjustmentDialog(String username, EventList eventListProducts,  EventList stockItems, String serverIp) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Adjust Stock");
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);

        this.username = username;
//        locationComboBox.addItem(Location.shop);
//        locationComboBox.addItem(Location.store);
        ArrayList<Site> locations = mAcess.getLocations();
        locationComboBox.setModel(new DefaultComboBoxModel(locations.toArray()));
        locationComboBox.insertItemAt("", 0);
        locationComboBox.setSelectedIndex(0);

        refundPanel.setVisible(false);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(productsComboBox,eventListProducts,new ProductCategory.ProductsTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK(stockItems);
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

        setMinimumSize(new Dimension(500, 300));
        pack();
        setLocationRelativeTo(null);

        productsComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Site location = (Site)locationComboBox.getSelectedItem();
                    Product product = (Product)productsComboBox.getSelectedItem();
                    int productId = product.getProductId();
                    double expectedQuantity = mAcess.getQuantityInStock(productId, location);
                    expectedQuantityFormattedTextField.setText(Double.toString(expectedQuantity));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        transactionTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String transactionType = (String)transactionTypeComboBox.getSelectedItem();
                if(transactionType.equals("return")){
                    refundPanel.setVisible(true);
                }else {
                    refundPanel.setVisible(false);
                }
            }
        });
    }

    private void onOK(EventList stockItems) {
        try {
            Product product = (Product)productsComboBox.getSelectedItem();
            int productId = product.getProductId();

             double recorded_quantity = Double.parseDouble(expectedQuantityFormattedTextField.getText());
             double delta_quantity = Double.parseDouble(quantityFormattedTextField.getText());
            //String comment = commentTextArea.getText();
            String transactionType = (String)transactionTypeComboBox.getSelectedItem();
            Site location = (Site) locationComboBox.getSelectedItem();

            Direction direction = Direction.in;
            switch (transactionType){
                case "less by":
                    direction = Direction.down;
                    break;
                case "more by":
                    direction = Direction.up;
                    break;
                case "return":
                    direction = Direction.in;
                    break;
            }

            if(direction.equals(Direction.down) && (delta_quantity > recorded_quantity) || (delta_quantity<0)){ //prevent negative values
                JOptionPane.showMessageDialog(null,"Change can not be negative!","Invalid value"
                        ,JOptionPane.ERROR_MESSAGE);
            }else {

                StockItem stockItem = new StockItem();
                stockItem.setProductId(productId);
                stockItem.setDirection(direction);

                //source, destination are the same because it is an adjustment which is simply a manual correction
                stockItem.setSource_dest(location);
                stockItem.setLocation(location);
                stockItem.setQuantity(Double.parseDouble(quantityFormattedTextField.getText()));
                stockItem.setComment(commentTextArea.getText());

                int transactionid = mAcess.updateStock(username, stockItem);
                StockItem returnedStockItem = mAcess.getStockItem(transactionid);
               // StockItem returnedStockItem = mAcess.updateStock(username, stockItem);
                returnedStockItem.setProductName(product.getProductName());
                stockItems.add(0,returnedStockItem); // add to the top of the list


                if (transactionType.equals("return")) {
                    Refund refund = new Refund();
                    refund.setAmount(Double.parseDouble(refundTextField.getText()));
                    refund.setComment(commentTextArea.getText());
                    //refund.setId(transactionid);
                    refund.setId(returnedStockItem.getTransactionId());
                    mAcess.refund(refund);
                }
                dispose();
            }

        } catch (Exception e) {
            e.printStackTrace();
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
