package sell_ui;

import Utilities.SettingsParser;
import admin_ui.SettingsTableModel;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.*;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JPanel leftSettingsPanel;
    private JTable settingsTable;
    private SettingsParser settingsParser;

    public SettingsDialog(JFrame frame) {
        super(frame, true);
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        settingsParser = new SettingsParser("settings.xml");

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
        setLocation(new Point(300,30));

        populateSettingsTable();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void populateSettingsTable() {

        String[] columnNames = {"Setting", "Value"};

        String [][] settings = {{"Business name", settingsParser.getBusinessName()},
                {"Address", settingsParser.getLocation()},
                {"Phone 1", settingsParser.getPhone1()},
                {"Phone 2", settingsParser.getPhone2()},
                {"TIN", settingsParser.getTin()},
                {"Server Ip", settingsParser.getServerIp()},

                //{"default_stock_destination", default_destination.getName() }
        };
        SettingsTableModel settingsTableModel = new SettingsTableModel(settings, columnNames);

        settingsTable.setModel(settingsTableModel);

        addSettingsTableModelListener();
    }

    private void addSettingsTableModelListener() {
        settingsTable.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                try {
                    SettingsTableModel tableModel = (SettingsTableModel)settingsTable.getModel();
                    String nodeName = (String) tableModel.getValueAt(row, 0);
                    String textContent = (String) tableModel.getValueAt(row, 1);

                    switch (nodeName){
                        case "Business name":
                            settingsParser.setBusinessName(textContent);
                            break;
                        case "Address":
                            settingsParser.setLocation(textContent);
                            break;
                        case "Phone 1":
                            settingsParser.setPhone1(textContent);
                            break;
                        case "Phone 2":
                            settingsParser.setPhone2(textContent);
                            break;
                        case "TIN":
                            settingsParser.setTin(textContent);
                            break;
                        case "Server Ip":
                            settingsParser.setServerIp(textContent);
                            break;
                    }

                    if(nodeName == "server_ip") {
                        settingsParser.updateSettings("server", "server_ip", textContent);
                    }else{
                        settingsParser.updateSettings("header", nodeName, textContent);
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        });
    }
}
