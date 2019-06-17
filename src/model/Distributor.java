package model;

import ca.odell.glazedlists.EventList;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

/**
 * Distributor is an object used to distribute a given product in the Receiving center to the store and shop. It can
 * also move products from shop to store.
 *
 */
public class Distributor {
    private int productId;
    private String productName;
    //private double received_quantity; // Quantity in the receiving center
    //private double store_quantity;
    //private double shop_quantity;
    private double totalQuantityInStock;
    private String date_time_created;
    private String date_time_modified;
    private double stockLowThreshold;
    private static MySqlAccess mAcess;


    public Distributor(int productId,  String serverIp) {
        this.productId = productId;
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME, serverIp);
    }

    public double getTotalQuantityInStock() {
        return totalQuantityInStock;
    }

    public void setTotalQuantityInStock(double totalQuantityInStock) {
        this.totalQuantityInStock = totalQuantityInStock;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getDate_time_created() {
        return date_time_created;
    }

    public void setDate_time_created(String date_time_created) {
        this.date_time_created = date_time_created;
    }

    public String getDate_time_modified() {
        return date_time_modified;
    }

    public void setDate_time_modified(String date_time_modified) {
        this.date_time_modified = date_time_modified;
    }

    public double getStockLowThreshold() {
        return stockLowThreshold;
    }

    public void setStockLowThreshold(double stockLowThreshold) {
        this.stockLowThreshold = stockLowThreshold;
    }

    public void transfer(Site source, Site dest, EventList stockItems, Double transfer_qty) {
        //TODO: Add some safe guards for a possible race condition whereby the receiving center is updated from the supplier page

        try {
            //mAcess.updateDistribution(sourceTableColumn,destTableColumn,source_qty,dest_qty,productId);
            StockItem stockItem = new StockItem();
            stockItem.setProductId(productId);

            //for the location whose quantity is being reduced
            stockItem.setDirection(Direction.out);
            stockItem.setLocation(source);
            stockItem.setSource_dest(dest);
            stockItem.setQuantity(transfer_qty);
            int transactionid = mAcess.updateStock("ibalihikya",stockItem);

            //TODO: this is to refresh stock transactions table but it should not be in Distributor object. For better design shift functionality to a refreshTable() function
            //TODO: which should be in the MainForm-ProductCategory
            StockItem returnedStockItem = mAcess.getStockItem(transactionid);
            stockItems.add(0,returnedStockItem);

            //for the location whose quantity is being reduced
            stockItem.setDirection(Direction.in);
            stockItem.setLocation(dest);
            stockItem.setSource_dest(source);
            transactionid= mAcess.updateStock("ibalihikya",stockItem);

            //TODO: this is to refresh stock transactions table but it should not be in Distributor object. For better design shift functionality to a refreshTable() function
            //TODO: which should be in the MainForm-ProductCategory
            returnedStockItem = mAcess.getStockItem(transactionid);
            stockItems.add(0,returnedStockItem);


        } catch (Exception e) {
            e.printStackTrace();
        }
        //update distribution set rec_center_qty = 5, store_qty = 10 where product_id=188;
    }
}
