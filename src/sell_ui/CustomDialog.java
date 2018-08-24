
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package sell_ui;

import model.Product;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/* 1.4 example used by DialogDemo.java. customised for use here */
class CustomDialog extends JDialog implements ActionListener,PropertyChangeListener, ItemListener {
    private Product selectedProduct = null;
    private JComboBox comboBox;
    private UI ui;

    private JOptionPane optionPane;

    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public Product getSelectedProduct() {
        return selectedProduct;
    }

    /** Creates the reusable dialog. */
    //public CustomDialog(Frame aFrame, JComboBox comboBox, UI parent) {
    public CustomDialog(Frame aFrame) {
        super(aFrame, true);

        setTitle("Products");

        //this.comboBox = comboBox;

        String [] products = {"Cement", "lime", "nails"};
        comboBox = new JComboBox(products);
        comboBox.setSelectedIndex(-1);

        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(comboBox,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);


        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the combobox always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent ce) {
                comboBox.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.
        comboBox.addActionListener(this);

        //comboBox.addItemListener(this);

        //Register an event handler that reacts to option pane state changes.

        optionPane.addPropertyChangeListener(this);
    }

    /** This method handles events for the combobox. */
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
        System.out.println("action");
    }

    /** This method reacts to state changes in the option pane. */
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop) ||
                JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                //selectedProduct = (Product) comboBox.getSelectedItem();
                //clearAndHide();
            } else { //user closed dialog or clicked cancel
                //selectedProduct = null;
                clearAndHide();
            }
        }
    }

    /** This method clears the dialog and hides it. */
    public void clearAndHide() {
        comboBox.setSelectedIndex(-1);
        setVisible(false);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
//        if (e.getStateChange() == ItemEvent.SELECTED) {
//            optionPane.setValue(btnString1);
//        }
        //System.out.println(e.getStateChange());
    }
}
