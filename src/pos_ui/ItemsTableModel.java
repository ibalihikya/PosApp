package pos_ui;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class ItemsTableModel extends AbstractTableModel {

    protected Vector data;
    protected Vector    columnNames;

//    private String[] columnNames = {"No.",
//            "Product Name",
//            "Quantity",
//            "Units",
//            "Price",
//            "Total Price"};

//    private Object[][] data = {
//            {"1", "Cement", new Integer(5), "Kg", new Double(30000.0), new Double(150000)}
//            //{"1", "Cement", new Integer(5), new Double(30000.0), new Double(150000)},
//    };



    public ItemsTableModel(Object[] columnNames) {

        this.columnNames = convertToVector(columnNames);
        data = new Vector();
    }


    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Vector rowVector = (Vector)data.elementAt(rowIndex);
        return rowVector.elementAt(columnIndex);
    }

    @Override
    public String getColumnName(int column) {

        Object id = null;
        // This test is to cover the case when
        // getColumnCount has been subclassed by mistake ...
        if (column < columnNames.size() && (column >= 0)) {
            id = columnNames.elementAt(column);
        }
        return (id == null) ? super.getColumnName(column)
                : id.toString();
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Vector rowVector = (Vector)data.elementAt(rowIndex);
        rowVector.setElementAt(aValue, columnIndex);
    }

    public void addRow(Object[] rowData){
        addRow(convertToVector(rowData));
    }

    public void addRow(Vector rowData) {
        insertRow(getRowCount(), rowData);
    }

    public void insertRow(int row, Vector rowData) {
        //data.add(rowData);
        data.insertElementAt(rowData, row);
        justifyRows(row, row+1);
        fireTableRowsInserted(row, row);
    }



    private Vector convertToVector(Object[] anArray) {
        if (anArray == null) {
            return null;
        }
        Vector<Object> v = new Vector<Object>(anArray.length);
        for (Object o : anArray) {
            v.addElement(o);
        }
        return v;
    }

    private void justifyRows(int from, int to) {
        // Sometimes the DefaultTableModel is subclassed
        // instead of the AbstractTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        data.setSize(getRowCount());

        for (int i = from; i < to; i++) {
            if (data.elementAt(i) == null) {
                data.setElementAt(new Vector(), i);
            }
            ((Vector)data.elementAt(i)).setSize(getColumnCount());
        }
    }
}

