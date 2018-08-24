package validation;

import javax.swing.*;
import java.util.regex.Pattern;

public class NotEmptyNumberValidator extends AbstractValidator {

    public NotEmptyNumberValidator(JDialog parent, JTextField c, String message) {
        super(parent, c, message);
    }

    protected boolean validationCriteria(JComponent c) {
        String text = ((JTextField)c).getText();
        boolean isInteger = Pattern.matches("^\\d*$", text);
        if (text.equals("") || !isInteger )
            return false;
        return true;
    }
}
