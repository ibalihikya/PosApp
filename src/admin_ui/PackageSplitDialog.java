package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import model.Product;
import model.Splitter;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PackageSplitDialog extends JDialog {

    //TODO ADD  an intermediate stage where the user can revert changes before confirming

    private Product product = null;
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private static MySqlAccess mAcess;
    private JTextField quantityTextField;
    private JTextField numPacksToSplitTextField;
    private JTextField quantityPerPackTextField;
    private JComboBox productsComboBox;
    private Splitter splitter;
    private String serverIp="";

    public PackageSplitDialog(JFrame frame, String title, Splitter splitter, String serverIp) {
        super(frame, title, true); // !!!!! made into a modal dialog
        this.serverIp=serverIp;
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        this.splitter = splitter;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        //buttonPanel.add(shortcutsLabel);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JRootPane rootPane = new JRootPane();
        rootPane.setDefaultButton(okButton);

        quantityTextField = new JTextField(5);
        quantityTextField.setEditable(false);

        numPacksToSplitTextField = new JTextField(5);
        quantityPerPackTextField = new JTextField(5);

        productsComboBox = new JComboBox();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    populateComboBox2(productsComboBox,getProducts2(),new ProductCategory.ProductsTextFilterator());
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel(splitter.getProductName()));
        topPanel.add(new JLabel("Packs:"));
        topPanel.add(quantityTextField);

        topPanel.add(new JLabel("Packs to un-bundle:"));
        topPanel.add(numPacksToSplitTextField);

        topPanel.add(new JLabel("Quantity per Pack:"));
        topPanel.add(quantityPerPackTextField);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(new JLabel("Associate with:"));
        bottomPanel.add(productsComboBox);

        quantityTextField.setText(Double.toString(splitter.getQuantity()));

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(frame);

        CancelAction cancelAction = new CancelAction("Close", new Integer(KeyEvent.VK_C));
        cancelButton.setAction(cancelAction);

        OKAction okAction = new OKAction("Unpack");
        okButton.setAction(okAction);

        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"ok");
        okButton.getActionMap().put("ok", okAction);

        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"cancel");
        cancelButton.getActionMap().put("cancel",cancelAction );

    }


    public Product getSelectedProduct() {
        return product;
    }

    public class CancelAction extends AbstractAction{

        public CancelAction(String text, Integer mnemonic){
            super(text);
            putValue(MNEMONIC_KEY, mnemonic);

        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setVisible(false);
        }
    }

    public class OKAction extends AbstractAction{

        public OKAction(String text){
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int qty_per_pack = Integer.parseInt(quantityPerPackTextField.getText());
            int num_packs_to_split = Integer.parseInt(numPacksToSplitTextField.getText());
            int quantityToAdd = num_packs_to_split * qty_per_pack ;
            Product product = (Product) productsComboBox.getSelectedItem();
            int splitProductId =  product.getProductId();
            splitter.setNumOfPacksToSplit(num_packs_to_split);
            splitter.split(splitProductId, quantityToAdd );
            setVisible(false);
        }
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
