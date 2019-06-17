package sell_ui;

import model.Category;
import model.Product;
import model.Unit;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import org.postgresql.util.PSQLException;
import validation.ComboNotSelectedValidator;
import validation.NotEmptyNumberValidator;
import validation.NotEmptyValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class AddProductDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel addProductPanel;
    private JTextField productNametextField;
    private JTextField descriptionTextField;
    private JTextField priceTextField;
    private JTextField barcodeTextField;
    private JTextArea commentTextArea;
    private JComboBox categoryComboBox;
    private JFormattedTextField costPriceFormattedTextField;
    private JComboBox unitsComboBox;
    private JFormattedTextField thresholdFormattedtextField;
    private static MySqlAccess mAcess;
    private JDialog parent;
    private boolean DEBUG = false;

    public AddProductDialog(JFrame frame, String serverIp) {
        super(frame, true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("Add Product");

        parent = new JDialog(); // for validation error messages

        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);
        populateCategoriesComboBox();

        ArrayList<Unit> units = mAcess.getAllUnits();
        ComboBoxModel cbUnitsModel = new DefaultComboBoxModel(units.toArray());
        unitsComboBox.setModel(cbUnitsModel);
        unitsComboBox.setSelectedIndex(-1);

        //input validation
        productNametextField.setInputVerifier(new NotEmptyValidator(parent,productNametextField,"Enter product name. (Press ESC to clear this message)"));
        priceTextField.setInputVerifier(new NotEmptyNumberValidator(parent,priceTextField, "Invalid price. (Press ESC to clear this message)"));
        costPriceFormattedTextField.setInputVerifier(new NotEmptyNumberValidator(parent, costPriceFormattedTextField, "Invalid price (Press ESC to clear this message)"));
        unitsComboBox.setInputVerifier(new ComboNotSelectedValidator(parent,unitsComboBox, "Select a unit. (Press ESC to clear this message)"));


        addProductPanel.setBackground(new Color(214,217,223));

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
        setMinimumSize(new Dimension(400,400));
        setLocationRelativeTo(null);
    }

    private void onOK() {
        int n = JOptionPane.showConfirmDialog(null,
                "Do you want to continue with this action?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            Category category;
            if (categoryComboBox.getSelectedIndex() == -1) {
                category = new Category("");
            } else {
                category = (Category) categoryComboBox.getSelectedItem();
            }

            try {

                Product product = new Product();
                product.setProductName(productNametextField.getText().trim());
                product.setCategory(category);
                product.setDescription(descriptionTextField.getText().trim());

                //TODO: improve this code which handles the case when a user does not fill all fields, and defaults need to be submitted.
                Object o = costPriceFormattedTextField.getValue();
                if(o instanceof Double ){
                   product.setCostprice((double)o);
                }else if (o instanceof Number){
                    product.setCostprice( new Double(((Number)o).doubleValue()));
                }else{
                    product.setCostprice(0.0);
                }

                Object obj = thresholdFormattedtextField.getValue();
                if(obj instanceof Double ){
                    product.setStockLowThreshold((double)obj);
                }else if (obj instanceof Number){
                    product.setStockLowThreshold( new Double(((Number)obj).doubleValue()));
                }else{
                    product.setStockLowThreshold(0.0);
                }

                product.setPrice(Double.parseDouble(priceTextField.getText().trim()));
                product.setUnits(((Unit) unitsComboBox.getSelectedItem()).getUnitName());
                product.setComment(commentTextArea.getText().trim());
                product.setBarcode(barcodeTextField.getText().trim());
                mAcess.insertProduct(product);

            } catch (NumberFormatException ex) {
                System.out.println("Invalid price!");
            } catch (NullPointerException ex) {
                System.out.println("Units not selected!");
            } catch (PSQLException ex) {
                System.out.println("Product name already exists");
                ex.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            } finally {

            }
        }
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
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
}
