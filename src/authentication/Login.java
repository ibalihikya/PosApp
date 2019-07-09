package authentication;

import Utilities.SettingsParser;
import model.User;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import sell_ui.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;

public class Login extends JFrame {
    private JPanel mainPanel;
    private JTextField usernametextField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private JPanel buttonPanel;
    private JLabel labelProductName;
    private JFormattedTextField serverIpFormattedTextField;
    private JButton editIPButton;
    private JPanel serverPanel;
    private JLabel loadingLabel;
    private static MySqlAccess mAcess;
    private SettingsParser settingsParser;
    private String serverIP;

    public Login(String title) {
        super(title);
        settingsParser = new SettingsParser("settings.xml");
        serverIP = settingsParser.getServerIp();
        serverIpFormattedTextField.setText(serverIP);
        serverIpFormattedTextField.setFocusable(false);
        serverIpFormattedTextField.setEnabled(false);
        editIPButton.setFocusable(false);
        loadingLabel.setVisible(false);



        JRootPane rootPane = new JRootPane();

        java.net.URL imgURL = getClass().getResource("/images/ic_trending_up_black_18dp.png");
        Image logoImage = Toolkit.getDefaultToolkit().getImage(imgURL);

        ImageIcon editServerIPIcon = createImageIcon("/images/ic_edit_black_18dp.png", "Edit Server IP address");

        //setIconImage(getScaledImage(logoImage,18,18,Color.decode("#616161") ));
        setIconImage(logoImage);

        rootPane.setDefaultButton(loginButton);
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
        mainPanel.setBackground(primaryColor);
        serverPanel.setBackground(primaryColor);
        loginButton.setBackground(accentColor);
        buttonPanel.setBackground(primaryColor);
        labelProductName.setForeground(textColor);
        labelProductName.setFont(titleFont);
        //mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,settingsParser.getServerIp());


        LoginAction loginAction = new LoginAction("login");
        loginButton.setAction(loginAction);

        loginButton.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"login");
        loginButton.getActionMap().put("login", loginAction);
        editIPButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverIpFormattedTextField.setEnabled(true);
                serverIpFormattedTextField.setFocusable(true);
                serverIpFormattedTextField.requestFocusInWindow();
            }
        });
        serverIpFormattedTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settingsParser.setServerIp(serverIpFormattedTextField.getText());
                serverIpFormattedTextField.setEnabled(false);
                serverIpFormattedTextField.setFocusable(false);
            }
        });

        editIPButton.setIcon(editServerIPIcon);
        editIPButton.setContentAreaFilled(false);
        editIPButton.setToolTipText("Edit Server IP address");
    }

    private void loginUser() {
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIpFormattedTextField.getText());
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
                            loadingLabel.setVisible(true);
                            //String [] args = {posUser.getUserName()};
                            //UI.main(args);
                        String userlevel = "general";

                        if(posUser.isAdmin()){
                            userlevel = "admin";
                        }

                        UI salesUi = new UI("posapp");
                        String [] args = {posUser.getUserName(),userlevel};
                        salesUi.main(args);
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(mainPanel);
                            topFrame.dispose();
                    }
                });
                thread.start();

            }else{
                errorLabel.setText("Wrong username or password!");
            }
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.put("DesktopIconUI", null);
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIDefaults defaults = UIManager.getLookAndFeelDefaults();
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        Login frame = new Login("Sales Partner");
        frame.setSize(200,200);
        frame.setResizable(false);
        frame.setContentPane(frame.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);


    }

    public class LoginAction extends AbstractAction {

        public LoginAction(String text){
            super(text);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            loginUser();
        }
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h, Color color){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, color, null);
        g2.dispose();
        return resizedImg;
    }

    /** Returns an ImageIcon, or null if the path was invalid. */
    protected ImageIcon createImageIcon(String path,
                                        String description) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
