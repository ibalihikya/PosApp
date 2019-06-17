package admin_ui;

import authentication.SaltedMD5;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

public class ChangePasswordDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPasswordField passwordField;
    private JPasswordField confirmpasswordField;
    private JLabel errorLabel;
    private static MySqlAccess mAcess;
    private String username;

    public ChangePasswordDialog(String username, JFrame frame, String serverIp) {
        super(frame,username,true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIp);
        this.username=username;
        errorLabel.setVisible(false);

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
        setLocationRelativeTo(frame);
    }

    private void onOK() {

        boolean isSuccessful = false;
        try {
            char[] password = passwordField.getPassword();
            char[] confirmpassword = confirmpasswordField.getPassword();

            if (Arrays.equals(password, confirmpassword)) {
                byte[] salt = SaltedMD5.getSalt();
                String securePassword = SaltedMD5.getSecurePassword(new String(password), salt);
                mAcess.changePassword(username,securePassword,salt);
                isSuccessful=true;
            }else {
                errorLabel.setVisible(true);
                errorLabel.setForeground(Color.RED);
                errorLabel.setText("Passwords do not match!");
            }
        }catch (Exception e1){
            e1.printStackTrace();
        }

        if(isSuccessful)
            dispose();

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
