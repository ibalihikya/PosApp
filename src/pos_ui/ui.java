package pos_ui;

import model.Item;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ui extends JPanel implements TableModelListener {

    public ui(){
        super(new GridLayout(2,0));

            String[] columnNames = {"No.",
            "Product Name",
            "Quantity",
            "Units",
            "Price",
            "Total Price"};

        JTable itemsTable = new JTable(new ItemsTableModel(columnNames));
        itemsTable.setPreferredScrollableViewportSize(new Dimension(1000,300));
        final ItemsTableModel  itemsTableModel = (ItemsTableModel) itemsTable.getModel();
        itemsTableModel.addTableModelListener(this);

        //add table to a scrollpane
        JScrollPane scrollPane = new JScrollPane(itemsTable);

        // Add scroll pane to this panel
        add(scrollPane);

        /**
         * This textfield captures the item quantity and when the enter key is pressed,
         * the entry fields are cleared and a new table row is created.
         */
        JTextField textFieldQuantity = new JTextField();
        add(textFieldQuantity);

        textFieldQuantity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Item icement = new Item(1,5,30000);
                Object[] r1 = {"1.","Cement1", icement.getQuantity(), "Kg",
                        new Double(icement.getPrice()),  new Double(icement.computeTotalPrice())};
                //DefaultTableModel model = (DefaultTableModel)e.getSource();
                itemsTableModel.addRow(r1);

            }
        });


    }




    @Override
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        TableModel model = (TableModel)e.getSource();
        String columnName = model.getColumnName(column);
        //Object data = model.getValueAt(row, column);

        //Todo 1: Do something with this data

    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HardwarePos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        ui newContentPane = new ui();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
