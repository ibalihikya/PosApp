package validation;

import javax.swing.*;

/**
 * A class for performing basic validation on text fields. All it does is make
 * sure that they are not null.
 *
 * @author Michael Urban
 */

public class NotEmptyValidator extends AbstractValidator {
    public NotEmptyValidator(JDialog parent, JTextField c, String message) {
        super(parent, c, message);
    }

    protected boolean validationCriteria(JComponent c) {
        String text = ((JTextField)c).getText();
        if (text.equals(""))
            return false;
        return true;
    }
}