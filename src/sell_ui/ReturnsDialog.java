package sell_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Vector;

public class ReturnsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel deliveryPanel;
    private JPanel deliveryDefTablePanel;
    private JPanel defaultSettingsPanel;
    private JTable additemsTable;
    private JPanel topRightPanel;
    private JTextField receiptTextField;
    private JComboBox supplierComboBox;
    private JButton addItemButton;
    private JButton removeItemButton;
    private JFormattedTextField grandTotalFormattedTextField;
    private JFormattedTextField amountPaidFormattedTextField;
    private JTextField commentTextField;
    private JButton viewReturnsButton;
    private int rowNumber = 0;
    private final ItemsTableModel itemsTableModel;
    private Transaction transaction;
    private static MySqlAccess mAcess;
    private String serverIP;
    private String loggedinUser;
    private static NumberFormat doubleFormat;

    private String[] itemsTableColumnNames = {"No.",
            "Product",
            "Quantity",
            "Units",
            "Price",
            "Total Price",
            "product id"};

    public ReturnsDialog(JFrame frame,EventList products, String loggedinUser, String serverIp) {
        super(frame, true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Returns");

        doubleFormat = NumberFormat.getNumberInstance();
        NumberFormatter doubleFormatter = new NumberFormatter(doubleFormat);
        doubleFormatter.setFormat(doubleFormat);

        amountPaidFormattedTextField.setFormatterFactory(
                new DefaultFormatterFactory(doubleFormatter));

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        this.serverIP=serverIp;
        this.loggedinUser=loggedinUser;

        grandTotalFormattedTextField.setEditable(false);

        ImageIcon addItemIcon = createImageIcon("/images/ic_add_green_18dp.png", "Add Item");
        ImageIcon removeIcon = createImageIcon("/images/ic_remove_black_18dp.png", "Remove Item");

        addItemButton.setIcon(addItemIcon);
        addItemButton.setContentAreaFilled(false);
        addItemButton.setToolTipText("Add an item");

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

                grandTotalFormattedTextField.setValue(computeGrandTotal());
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

                if(product !=null) {
                    addItemToTable(product);
                }

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

        setLocation(new Point(300,30));
        removeItemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int rowIndex = additemsTable.getSelectedRow();
                    itemsTableModel.removeRow(rowIndex);

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


        amountPaidFormattedTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                //super.keyReleased(e);
                if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
                    KeyboardFocusManager fm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    amountPaidFormattedTextField.setValue(null);
                    amountPaidFormattedTextField.setFocusable(false);
                    fm.getActiveWindow().requestFocusInWindow();


                }

                double cash = 0.0;

                if(!amountPaidFormattedTextField.getText().isEmpty()) {
                    cash = (double)UI.getFormattedTextFieldValue(amountPaidFormattedTextField);
                }

                if(cash>0.0) {
                    amountPaidFormattedTextField.setText(String.format("%,.0f", cash));
                }
            }
        });
        viewReturnsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ReturnsView dialog = new ReturnsView(serverIp);
                dialog.setMinimumSize(new Dimension(600,600));
                dialog.setVisible(true);

            }
        });
    }

    private void onOK() {
        if(additemsTable.getRowCount()>0){
            try {
                Refund refund = new Refund();
                refund.setUsername(loggedinUser);
                refund.setComment(commentTextField.getText());
                refund.setGrandTotal((Double)UI.getFormattedTextFieldValue(grandTotalFormattedTextField));

                if(amountPaidFormattedTextField.getText().length()>0)
                    refund.setAmount((Double)UI.getFormattedTextFieldValue(amountPaidFormattedTextField));

                if(receiptTextField.getText().length()>0)//TODO: use better validation-this is only checking for empty field
                    refund.setReceipt_no(Integer.parseInt(receiptTextField.getText()));



                ArrayList<Item> items = new ArrayList<>();

                for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
                    Item item = new Item();
                    //item.setTransactionId(refund_id);
                    item.setProductId((int)row.elementAt(6));

                    double price = (Double) row.elementAt(4);
                    item.setPrice(price);

                    double quantity = (Double)row.elementAt(2);
                    item.setQuantity(quantity);

                    double totalprice = (Double) row.elementAt(5);
                    item.setTotalPrice(totalprice);

                    items.add(item);
                }

                int n = JOptionPane.showConfirmDialog(null, "Would you like to continue with this update?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    mAcess.refund(refund,items);
                    setVisible(false);
                }else {
                    return;
                }



            }catch (Exception e1){
                e1.printStackTrace();
                return;
            }
        }
        dispose();
    }



    private void onCancel() {
        ((ItemsTableModel) additemsTable.getModel()).clearTable();
        resetFormFields(topRightPanel);
        dispose();
    }

    private Double computeGrandTotal(){
        Double gtotal = 0.0;
        for( Vector<Object> row : (Vector<Vector<Object>>)itemsTableModel.getData()){
            gtotal += (Double) row.elementAt(5);
        }
        return  gtotal;
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
            Double price = product.getPrice();
            Double totalPrice = quantity*price;
            Object[] r1 = {rowNumber, product.getProductName(), quantity, product.getUnits(),price ,totalPrice ,productId};
            itemsTableModel.addRow(r1);



//            grandTotal += totalPrice;
//            grandTotalFormattedTextField.setValue(grandTotal);
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
