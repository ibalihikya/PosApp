package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.Item;
import model.Product;
import model.Supplier;
import model.Transaction;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import sell_ui.DoubleEditor;
import sell_ui.ItemsTableModel;
import sell_ui.ProductDialog;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

public class AddItemsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel deliveryPanel;
    private JPanel deliveryDefTablePanel;
    private JTable additemsTable;
    private JPanel topRightPanel;
    private JTextField invoiceTextField;
    private JComboBox supplierComboBox;
    private JButton addItemButton;
    private JButton removeItemButton;
    private int rowNumber = 0;
    private final ItemsTableModel itemsTableModel;
    private Transaction transaction;
    private static MySqlAccess mAcess;

    private String[] itemsTableColumnNames = {"No.",
            "Product",
            "Quantity",
            "Units",
            "Price",
            "Total Price",
            "product id"};

    public AddItemsDialog(EventList suppliers, EventList products, String serverIp) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Receive Goods");

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);

        ImageIcon addItemIcon = createImageIcon("/images/ic_add_green_18dp.png", "Add Item");
        ImageIcon removeIcon = createImageIcon("/images/ic_remove_black_18dp.png", "Remove Item");

        addItemButton.setIcon(addItemIcon);
        addItemButton.setContentAreaFilled(false);
        addItemButton.setToolTipText("Add an item to invoice");

        removeItemButton.setIcon(removeIcon);
        removeItemButton.setContentAreaFilled(false);
        removeItemButton.setToolTipText("Remove Item");

        addItemButton.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.lightGray));
        removeItemButton.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.lightGray));

        additemsTable.setModel(new ItemsTableModel(itemsTableColumnNames));
        TableColumnModel columnModel = additemsTable.getColumnModel();
        columnModel.removeColumn(columnModel.getColumn(6)); // hide the productid
        itemsTableModel = (ItemsTableModel) additemsTable.getModel();

        additemsTable.getColumnModel().getColumn(2).setCellEditor(new DoubleEditor(0.0, 1000000.0));
        additemsTable.setRowHeight(25);




        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(supplierComboBox,suppliers, new ProductCategory.SupplierTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        additemsTable.getModel().addTableModelListener(new TableModelListener() {
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
        });

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
        addItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ProductDialog productsDialog = new ProductDialog(null, "Products", products,serverIp);
                productsDialog.setSize(new Dimension(700,500));
                productsDialog.setVisible(true);
                Product product = productsDialog.getSelectedProduct();

                if(product !=null)
                    addItemToTable(product);
                else {
                    return;
                }

                //set quantity field in edit mode
                int lastrow = additemsTable.getRowCount()-1;
//                additemsTable.setFocusable(true);
//                additemsTable.requestFocusInWindow();

                additemsTable.editCellAt(lastrow,2);

                JFormattedTextField ftf = (JFormattedTextField) additemsTable.getCellEditor(lastrow,2).getTableCellEditorComponent(additemsTable,1,false,lastrow,2);
                ftf.setText("");
                ftf.setFocusable(true);
                ftf.requestFocusInWindow();

            }
        });

        pack();
        setLocationRelativeTo(null);
        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowIndex = additemsTable.getSelectedRow();
//                    Double totalPrice = (Double) itemsTableModel.getValueAt(rowIndex, 5);
                    itemsTableModel.removeRow(rowIndex);
                    //TODO: Add total Amount text field and update it when items are removed or added
//                    grandTotal -= totalPrice;
//                    if (grandTotal == 0) {
//                        totalFormattedTextField.setText("");
//                    } else {
//                        totalFormattedTextField.setValue(grandTotal);
//                    }

                    //re-number the table rows
                    int newRowIndex = 1;
                    for (Vector<Object> row : (Vector<Vector<Object>>) itemsTableModel.getData()) {
                        row.setElementAt(newRowIndex, 0);
                        newRowIndex++;
                    }
                    rowNumber = additemsTable.getRowCount();
                }catch (ArrayIndexOutOfBoundsException e1){
                    System.out.println("No items in table");
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });

    }

    private void onOK() {
        if(additemsTable.getRowCount()>0){
            try {
                submitInvoice();
              }catch (Exception e1){
                e1.printStackTrace();
            }

        }
        dispose();
    }

    private void onCancel() {
        ((ItemsTableModel) additemsTable.getModel()).clearTable();
        resetFormFields(topRightPanel);
        dispose();
    }

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

    private void submitInvoice() {
        try {
            Supplier supplier = (Supplier)supplierComboBox.getSelectedItem();
            transaction = new Transaction(supplier.getId());

            for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
                Item item = new Item();
                item.setSellerid(transaction.getSellerId());
                item.setProductName((String)row.elementAt(1));
                double quantity = (Double)row.elementAt(2);
                item.setQuantity(quantity);
                item.setUnits((String)row.elementAt(3));
                double price = (Double) row.elementAt(4);
                item.setPrice(price);
                item.setProductId((int)row.elementAt(6));
                //item.setTotalPrice((Double)row.elementAt(5));
                //item.computeTotalPrice();
                item.setTotalPrice(quantity*price);
                item.setInvoiceNumber(Integer.parseInt(invoiceTextField.getText()));
                transaction.addItem(item);
            }
            mAcess.insertDelivery(transaction);
            ((ItemsTableModel) additemsTable.getModel()).clearTable();
        }
        catch (Exception e1) {
            e1.printStackTrace();
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

    private void addItemToTable(Product product) {
        try {

            int productId = product.getProductId();
            rowNumber++;
            Double quantity = 0.0;
            Double price = product.getCostprice();
            Double totalPrice = quantity*price;
            Object[] r1 = {rowNumber, product.getProductName(), quantity, product.getUnits(),price ,totalPrice ,productId};
            itemsTableModel.addRow(r1);



            //grandTotal += totalPrice;
            //totalFormattedTextField.setValue(grandTotal);
        }catch (NullPointerException ex){
            System.out.println("NullPointerException: product not selected!");

        }catch (ClassCastException ex){
            System.out.println("Unknown product!");
        } finally {
            //clearFields();

        }
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

}
