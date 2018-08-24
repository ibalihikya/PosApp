package authentication;

import admin_ui.ProductCategory;
import model.User;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import sell_ui.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Login {
    private JPanel mainPanel;
    private JTextField usernametextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JComboBox usageComboBox;
    private JButton registerButton;
    private JTextField errortextField;
    private JLabel errorLabel;
    private JPanel buttonPanel;
    private JLabel labelProductName;
    private static MySqlAccess mAcess;

    public Login() {
        Color primaryColor = Color.decode("#00bcd4");
        Color primaryDarkColor = Color.decode("#0097a7");
        //Color accentColor = Color.decode("#607D8B");
        Color accentColor = Color.decode("#FF5722");
        Color backgroundColor = Color.decode("#FFFFFF");

        Color textColor = Color.decode("#FFFFFF");
        Color primaryTextColor = Color.decode("#212121");
        Color secondaryTextColor = Color.decode("#757575");
        Font titleFont = new Font("SansSerif", Font.BOLD, 14);

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(primaryDarkColor);
        loginButton.setBackground(accentColor);
        buttonPanel.setBackground(primaryDarkColor);
        labelProductName.setForeground(textColor);
        labelProductName.setFont(titleFont);
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME);



        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernametextField.getText().trim();
                String password = new String(passwordField.getPassword());

                //get registered user from database
                User user = null;

                try {
                    user = mAcess.getUser(username);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                }
                byte[] salt = new byte[0];
                if(user !=null){
                    salt = user.getSalt();
                    //hash the password
                    String securePassword = SaltedMD5.getSecurePassword(new String(password),salt);

                    //compare stored password and submitted passwords
                    if(user.getPassword().equals(securePassword)) {
                        final User posUser = user;
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //load the appropriate UI based on use cases: sales or administration
                                if(usageComboBox.getSelectedItem().toString()=="Administration" && posUser.isAdmin()){
                                    ProductCategory productCategory = new ProductCategory();
                                    String [] args = {posUser.getUserName()};
                                    productCategory.main(args);
                                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                                    topFrame.setVisible(false);
                                }else{
                                    UI salesUi = new UI("posapp");
                                    String [] args = {posUser.getUserName()};
                                    salesUi.main(args);
                                    JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                                    topFrame.setVisible(false);
                                }

                            }
                        });
                        thread.start();

                    }else{
                        errorLabel.setText("Wrong username or password!");
                    }
                }


            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Registration registration = new Registration();
                String[] args = {};
                registration.main(args);
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                topFrame.setVisible(false);
            }
        });
    }

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
                    if (defaults.get("Table.alternateRowColor") == null)
                        defaults.put("Table.alternateRowColor", new Color(240, 240, 240));
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }
        JFrame frame = new JFrame("Login");
        frame.setSize(200,200);
        frame.setResizable(false);
        frame.setContentPane(new Login().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }
}
