package external_components;

import model.Item;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SellUi extends JFrame implements TableModelListener
{


    public SellUi()
    {
        super("Table");
        String[] cn = {"No.", "Item Code", "Item Name", "Quantity", "Price", "Total"};

        //Item combobox definition
        List<List<?>> tableData = new ArrayList<List<?>>();
        tableData.add(new ArrayList<String>(
                Arrays.asList("Cement", "Hima", "Annapolis")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Cement", "Tororo", "Concord")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Nails", "Half inch", "Trenton")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Tiles", "New Mexico", "Santa Fe")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Pvc pipe", "North Dakota", "Bismark")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Pvc pipe", "North Dakota", "Bismark")));
        tableData.add(new ArrayList<String>(
                Arrays.asList("Paint", "Sadolin", "Bismark")));



        String[] columns = new String[]{"State", "Name", "Capital"};
        int[] widths = new int[]{50, 100, 100};

        DetailedComboBox comboBox = new DetailedComboBox(columns, widths, 0);
        comboBox.setTableData(tableData);
        AutoCompletion.enable(comboBox);



        //initial blank row
        Object[][] r1 = {{"1.", " " ," ", " ",
                new Double(0.0),  new Double(0.0)}};




        DefaultTableModel model = new DefaultTableModel(r1,cn);
        model.addTableModelListener( this );

        JTable table = new JTable( model )
        {
            //  Returning the Class of each column will allow different
            //  renderers to be used based on Class
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }

            //  The Cost is not editable
//            public boolean isCellEditable(int row, int column)
//            {
//                int modelColumn = convertColumnIndexToModel( column );
//                return (modelColumn == 3) ? false : true;
//            }
        };
        table.setPreferredScrollableViewportSize(table.getPreferredSize());

        JScrollPane scrollPane = new JScrollPane( table );
        getContentPane().add( scrollPane );

        ComboBoxCellEditor itemEditor = new ComboBoxCellEditor(comboBox);
        table.getColumnModel().getColumn(2).setCellEditor(itemEditor);




//        JTextField txtQuantity = new JTextField();
//        DefaultCellEditor dce = new DefaultCellEditor(txtQuantity);
//        table.getColumnModel().getColumn(3).setCellEditor(dce);


//        String[] items = { "Bread", "Milk", "Tea", "Coffee" };
//        JComboBox editor = new JComboBox( items );
//
//        DefaultCellEditor dce = new DefaultCellEditor( editor );
//        table.getColumnModel().getColumn(0).setCellEditor(dce);
    }

    /*
     *  The cost is recalculated whenever the quantity or price is changed
     */
    public void tableChanged(TableModelEvent e)
    {
        Item icement2 = new Item(2,5,30000);
        Object[] r2 = {"2.", icement2.getProductId(),"Product2", icement2.getQuantity(),
                new Double(icement2.getPrice()),  new Double(icement2.computeTotalPrice())};


        if (e.getType() == TableModelEvent.UPDATE)
        {
            int row = e.getFirstRow();
            int column = e.getColumn();


                if (column == 3)
                {
                    DefaultTableModel model = (DefaultTableModel)e.getSource();
                    model.setValueAt("Cement", row, 2);
                    model.addRow(r2);

                }

        }
    }

    public static void main(String[] args)
    {
        SellUi frame = new SellUi();
        frame.setDefaultCloseOperation( EXIT_ON_CLOSE );
        frame.pack();
        frame.setLocationRelativeTo( null );
        frame.setVisible(true);
    }
}