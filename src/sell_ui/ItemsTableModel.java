package sell_ui;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class ItemsTableModel extends AbstractTableModel {

    protected Vector data;
    protected Vector    columnNames;


    public ItemsTableModel(Object[] columnNames) {

        this.columnNames = convertToVector(columnNames);
        data = new Vector();
    }

    public ItemsTableModel(Object[][] data, Object[] columnNames){
        setDataVector(data, columnNames);
    }

    public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers) {
        setDataVector(convertToVector(dataVector), convertToVector(columnIdentifiers));
    }

    public Vector getData() {
        return data;
    }

    //source: code is from DefaultTable model
    public void setDataVector(Vector dataVector, Vector columnIdentifiers) {
        this.data = nonNullVector(dataVector);
        this.columnNames = nonNullVector(columnIdentifiers);
        justifyRows(0, getRowCount());
        fireTableStructureChanged();
    }

    //source: code is from DefaultTable model
    private static Vector nonNullVector(Vector v) {
        return (v != null) ? v : new Vector();
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
        Class objectClass = null;
        try{
            objectClass = getValueAt(0, columnIndex).getClass();
        }catch (NullPointerException ex){
            objectClass= String.class;
        }finally {
         return objectClass;
        }
    }

    /*@Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Vector rowVector = (Vector)data.elementAt(rowIndex);
        rowVector.setElementAt(aValue, columnIndex);
    }*/

    public boolean isCellEditable(int row, int col) {

        if (col == 2) {
            return true;
        } else {
            return false;
        }
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        Vector rowVector = (Vector)data.elementAt(rowIndex);
        rowVector.setElementAt(aValue, columnIndex);
        fireTableCellUpdated(rowIndex, columnIndex);
    }



    public void addRow(Object[] rowData){
        addRow(convertToVector(rowData));
    }

    public void addRow(Vector rowData) {
        insertRow(getRowCount(), rowData);
    }

    public void insertRow(int row, Vector rowData) {
        data.insertElementAt(rowData, row);
        justifyRows(row, row+1);
        fireTableRowsInserted(row, row);
    }

    public void clearTable(){
        data.clear();
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        data.removeElementAt(row);
        fireTableRowsDeleted(row, row);
    }


    //source: code from DefaultTableModel
    protected static Vector convertToVector(Object[] anArray) {
        if (anArray == null) {
            return null;
        }
        Vector<Object> v = new Vector<Object>(anArray.length);
        for (Object o : anArray) {
            v.addElement(o);
        }
        return v;
    }

    protected static Vector convertToVector(Object[][] anArray) {
        if (anArray == null) {
            return null;
        }
        Vector<Vector> v = new Vector<Vector>(anArray.length);
        for (Object[] o : anArray) {
            v.addElement(convertToVector(o));
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

