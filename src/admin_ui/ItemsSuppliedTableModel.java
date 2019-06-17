package admin_ui;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

public class ItemsSuppliedTableModel extends EventTableModel {
    public ItemsSuppliedTableModel(EventList source, TableFormat tableFormat) {
        super(source, tableFormat);
    }

    public ItemsSuppliedTableModel(EventList source, String[] propertyNames, String[] columnLabels, boolean[] writable) {
        super(source, propertyNames, columnLabels, writable);
    }

    public boolean isCellEditable(int row, int col) {

        if (col == 3 || col == 4 || col ==5 ) {
            return true;
        } else {
            return false;
        }
    }
}
