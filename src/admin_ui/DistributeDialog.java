package admin_ui;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;
import model.Distributor;
import model.Site;
import model.StockItem;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class DistributeDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel transferPanel;
    private JTextField transferTextField;
    private JComboBox sourceComboBox;
    private JComboBox destinationComboBox;
    private JTextField sourceQuantityTextField;
    private JTextField destQuantitytextField;
    private JPanel stockTransactionsPanel;
    private JTable stockTable;
    private Distributor distributor;
    private static EventList stock;
    private static MySqlAccess mAcess;



    public DistributeDialog(JFrame frame, String title, Distributor distributor, EventList stockItems, String serverIp) {
        super(frame, title, true);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);


        this.distributor=distributor;

        sourceQuantityTextField.setEditable(false);
        destQuantitytextField.setEditable(false);

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

        //String [] locations = {"------Select------","Receiving Center", "Store", "Shop"};
        ArrayList<Site> locations = mAcess.getLocations();
        ArrayList<Site> source_dests = mAcess.getSource_dests();


        sourceComboBox.setModel(new DefaultComboBoxModel(locations.toArray()));
        destinationComboBox.setModel(new DefaultComboBoxModel(source_dests.toArray()));

        sourceComboBox.insertItemAt("", 0);
        destinationComboBox.insertItemAt("", 0);

        sourceComboBox.setSelectedIndex(0);
        destinationComboBox.setSelectedIndex(0);

        transferTextField.setColumns(5);

//        receivingCenterTextField.setText(Double.toString(distributor.getReceived_quantity()));
//        storeTextField.setText(Double.toString(distributor.getStore_quantity()));
//        shopTextField.setText(Double.toString(distributor.getShop_quantity()));

        setMinimumSize(new Dimension(400,400));
        pack();
        setLocationRelativeTo(frame);
        sourceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(sourceComboBox.getSelectedIndex()!=0) {
                        Double source_qty = mAcess.getQuantityInStock(distributor.getProductId(), (Site) sourceComboBox.getSelectedItem());
                        sourceQuantityTextField.setText(Double.toString(source_qty));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        destinationComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if(sourceComboBox.getSelectedIndex()!=0) {
                        Double dest_quantity =
                                mAcess.getQuantityInStock(distributor.getProductId(), (Site) destinationComboBox.getSelectedItem());

                        destQuantitytextField.setText(Double.toString(dest_quantity));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        populateStockTable2();
    }

    private void onOK(EventList stockItems) {
        Double transfer_qty = Double.parseDouble(transferTextField.getText());

        Site source = (Site)sourceComboBox.getSelectedItem();
        Site dest = (Site) destinationComboBox.getSelectedItem();

        double source_qty = 0.0;
        try {
            source_qty = mAcess.getQuantityInStock(distributor.getProductId(), (Site) sourceComboBox.getSelectedItem());
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if(!source.getName().toString().equals(dest.getName().toString()) && (transfer_qty <= source_qty)
                && transfer_qty>0) {

                int n = JOptionPane.showConfirmDialog(null, "Would you like to continue with this change?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    distributor.transfer(source, dest,stockItems, transfer_qty);
                    setVisible(false);
                }

            dispose();
        }else{
            JOptionPane.showMessageDialog(null,"Invalid operation!", "Invalid Entry",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void populateStockTable2() {
        EventList stockItemsEventList = new BasicEventList();

        try {
            stockItemsEventList.addAll(mAcess.getStock(distributor.getProductId()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextFilterator stockFilterator = new TextFilterator() {
            public void getFilterStrings(List baseList, Object element) {
                StockItem stockItem = (StockItem) element;
                baseList.add(stockItem.getTransactionId());
                baseList.add(stockItem.getProductName());
                baseList.add(stockItem.getLocation());
                baseList.add(stockItem.getBalance());
                baseList.add(stockItem.getDateCreated());
            }
        };

        //TextComponentMatcherEditor stockMatcherEditor = new TextComponentMatcherEditor(filterStockTextField, stockFilterator);
        //FilterList filteredStockItems = new FilterList(stockItemsEventList, new ThreadedMatcherEditor(stockMatcherEditor));

        String[] stockItemPropertyNames = new String[] {"transactionId","productName", "location",
                "balance", "dateCreated"};
        String[] stockTablecolumnLabels = new String[] {"Tr_id.","Product", "location", "Balance",
                "Date"};

        TableFormat stockTableFormat = GlazedLists.tableFormat(StockItem.class, stockItemPropertyNames, stockTablecolumnLabels);
        //stockTable.setModel(new EventTableModel(filteredStockItems,stockTableFormat));

        stockTable.setModel(new EventTableModel(stockItemsEventList,stockTableFormat));
    }
}
