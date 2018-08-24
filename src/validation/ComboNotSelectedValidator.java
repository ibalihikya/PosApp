package validation;

import javax.swing.*;

public class ComboNotSelectedValidator extends AbstractValidator  {
    public ComboNotSelectedValidator(JDialog parent, JComponent c, String message) {
        super(parent, c, message);
    }

    @Override
    protected boolean validationCriteria(JComponent c) {
//        Product product = (Product) ((JComboBox)c).getSelectedItem();
//        if(product.getProductName() == null){
//            return false;
//        }
        if(((JComboBox)c).getSelectedIndex() == -1)
            return false;
        return true;
    }
}
