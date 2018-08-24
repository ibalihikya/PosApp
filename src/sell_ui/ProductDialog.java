package sell_ui;

import model.Product;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class ProductDialog extends JDialog {
    private Product product = null;
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");

    public ProductDialog(JFrame frame, String title, JComboBox comboBox) {
        super(frame, title, true); // !!!!! made into a modal dialog
        JPanel panel = new JPanel();
        panel.add(comboBox);
        panel.add(okButton);
        panel.add(cancelButton);

        add(panel);
        pack();
        setLocationRelativeTo(frame);



        okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"ok");
        okButton.getActionMap().put("ok", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                product = (Product) comboBox.getSelectedItem();
                setVisible(false);
            }
        });

        cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"cancel");
        cancelButton.getActionMap().put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                product = null;
                setVisible(false);
            }
        });
    }

    public Product getSelectedProduct() {
        return product;
    }
}
