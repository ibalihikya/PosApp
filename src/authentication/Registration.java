package authentication;

import model.User;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Registration {
    private JPanel mainPanel;
    private JTextField firstNameTextField;
    private JTextField lastNameTextField;
    private JTextField userNameTextField;
    private JCheckBox adminCheckBox;
    private JButton registerButton;
    private JPasswordField passwordField;
    private static MySqlAccess mAcess;


    public Registration() {
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME);

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                char [] password = passwordField.getPassword();

                try {
                    byte[] salt = SaltedMD5.getSalt();
                    String securePassword = SaltedMD5.getSecurePassword(new String(password),salt);

                    User user = new User();
                    user.setUserName(userNameTextField.getText().trim());
                    user.setFirstName(firstNameTextField.getText().trim());
                    user.setLastName(lastNameTextField.getText().trim());
                    user.setPassword(securePassword);
                    user.setSalt(salt);
                    user.setAdmin(adminCheckBox.isSelected());

                    mAcess.registerUser(user);
                    resetFormFields(mainPanel);
                } catch (NoSuchAlgorithmException e1) {
                    e1.printStackTrace();
                } catch (NoSuchProviderException e1) {
                    e1.printStackTrace();
                }catch (Exception ex){
                    System.out.println("sql exception");
                }
            }
        });
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


    public static void main(String[] args) {
        JFrame frame = new JFrame("Registration");
        frame.setContentPane(new Registration().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
