package admin_ui;

import javax.swing.table.AbstractTableModel;

public class SettingsTableModel extends AbstractTableModel {
    private String [] columnNames = {};
    private String [][] settings = {};

    public  SettingsTableModel(String [][] settings, String[] columnNames){
        this.settings = settings;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return settings.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return  settings[rowIndex][columnIndex] ;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public boolean isCellEditable(int row, int col) {

        if (col == 1 ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        settings[rowIndex][columnIndex] = (String)aValue;
        fireTableCellUpdated(rowIndex, columnIndex);
    }
}
