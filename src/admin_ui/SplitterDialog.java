package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.Product;
import model.Site;
import model.Splitter;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class SplitterDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox productsComboBox;
    private JComboBox productToUnpackComboBox;
    private JTextField quantityAvailableTextField;
    private JTextField quantityPerPackTextField;
    private JTextField quantityToUnpackTextField;
    private JPanel productPanel;
    private JTextField newQuantityTextField;
    private static MySqlAccess mAcess;
    private Color accentColor = Color.decode("#ff5722");
    private String serverIp="";

    public SplitterDialog(String serverIp) {
        this.serverIp=serverIp;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Unpack - Split cartons or packages into pieces");

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        newQuantityTextField.setBackground(accentColor);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(productsComboBox,getProducts2(),new ProductCategory.ProductsTextFilterator());
                    populateComboBox2(productToUnpackComboBox,getProducts2(),new ProductCategory.ProductsTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

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

        pack();
        setLocationRelativeTo(null);
        productToUnpackComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Product product = (Product)productToUnpackComboBox.getSelectedItem();
                try {

                    //TODO: use a drop down list to select where the splitting is being done
                    Site location = new Site("shop");
                    location.setId(2);
                    double quantity = mAcess.getQuantityInStock(product.getProductId(),location );
                   quantityAvailableTextField.setText(Double.toString(quantity));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        quantityToUnpackTextField.addKeyListener(new KeyAdapter() {
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
                int unitsPerPack =0;
                int numOfPackstoUnpack = 0;
                if(quantityPerPackTextField.getText()=="" || quantityPerPackTextField.getText().isEmpty()){
                    unitsPerPack =0;
                }else{
                    unitsPerPack = Integer.parseInt(quantityPerPackTextField.getText());
                }

                if(quantityToUnpackTextField.getText()=="" || quantityToUnpackTextField.getText().isEmpty()){
                    numOfPackstoUnpack = 0;
                }else{
                    numOfPackstoUnpack = Integer.parseInt(quantityToUnpackTextField.getText());
                }


                int newQuantity = unitsPerPack * numOfPackstoUnpack;
                newQuantityTextField.setText(Integer.toString(newQuantity));

            }
        });

        quantityPerPackTextField.addKeyListener(new KeyAdapter() {
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
                int unitsPerPack =0;
                int numOfPackstoUnpack = 0;
                if(quantityPerPackTextField.getText()=="" || quantityPerPackTextField.getText().isEmpty()){
                    unitsPerPack =0;
                }else{
                    unitsPerPack = Integer.parseInt(quantityPerPackTextField.getText());
                }

                if(quantityToUnpackTextField.getText()=="" || quantityToUnpackTextField.getText().isEmpty()){
                    numOfPackstoUnpack = 0;
                }else{
                    numOfPackstoUnpack = Integer.parseInt(quantityToUnpackTextField.getText());
                }

                int newQuantity = unitsPerPack * numOfPackstoUnpack;
                newQuantityTextField.setText(Integer.toString(newQuantity));
            }
        });
    }

    private void onOK() {
        int qty_per_pack = Integer.parseInt(quantityPerPackTextField.getText());
        int num_packs_to_split = Integer.parseInt(quantityToUnpackTextField.getText());
        int quantityToAdd = num_packs_to_split * qty_per_pack ;
        Product product = (Product) productsComboBox.getSelectedItem();
        int splitProductId =  product.getProductId(); // the new  product  - the sub-unit of the larger package

        Product productToUnpack = (Product)productToUnpackComboBox.getSelectedItem();
        Splitter splitter = new Splitter(productToUnpack.getProductId(),serverIp);
        splitter.setNumOfPacksToSplit(num_packs_to_split);
        splitter.split(splitProductId, quantityToAdd );
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void populateComboBox2(JComboBox comboBox, ArrayList arrayList, TextFilterator textFilterator) throws InvocationTargetException {
        ComboBoxModel cbModel = new DefaultComboBoxModel(arrayList.toArray());
        comboBox.setModel(cbModel);
        EventList eventList = GlazedLists.eventList(arrayList);
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

    private  ArrayList<Product> getProducts2() {
        ArrayList<Product> products = new ArrayList<>();
        try {
            MySqlAccess mAcess2 = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
            products = mAcess2.getAllProducts();
            Product product = new Product();
            products.add(0,product);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
