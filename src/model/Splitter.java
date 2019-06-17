package model;

import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;

public class Splitter {
    private int productId; //product (package) to unpack
    private String productName; //product (package) to unpack
    private int quantity; // number of packs available
    private int unitsPerPack;
    private int numOfPacksToSplit;
    private String date_time_created;
    private String date_time_modified;
    private static MySqlAccess mAcess;

    public Splitter(int productId, String serverIP) {

        this.productId = productId;
        mAcess = new MySqlAccess(SqlStrings.PRODUCTION_DB_NAME,serverIP);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUnitsPerPack() {
        return unitsPerPack;
    }

    public void setUnitsPerPack(int unitsPerPack) {
        this.unitsPerPack = unitsPerPack;
    }

    public int getNumOfPacksToSplit() {
        return numOfPacksToSplit;
    }

    public void setNumOfPacksToSplit(int numOfPacksToSplit) {
        this.numOfPacksToSplit = numOfPacksToSplit;
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

    public void split(int splitProductId, int split_qty  ){//splitProductId is the associated unpacked product
        try {
            //int new_pack_qty = quantity - numOfPacksToSplit;
            StockItem stockItem = new StockItem();
            stockItem.setProductId(splitProductId);
            stockItem.setDirection(Direction.in);
            //TODO: use a drop down list to select where the splitting is being done
            Site location = new Site("shop");
            location.setId(2);
            stockItem.setLocation(location); //because the unpacking is being done in the shop
            stockItem.setSource_dest(location); //because the unpacking is being done in the shop
            stockItem.setQuantity(split_qty);
            mAcess.updateStock("ibalihikya",stockItem );

            StockItem packageStockItem = new StockItem(); //The larger package that is being unpacked
            packageStockItem.setProductId(productId);
            packageStockItem.setDirection(Direction.out);
            //TODO: use a drop down list to select where the splitting is being done
            packageStockItem.setLocation(location); //because the unpacking is being done in the shop
            packageStockItem.setSource_dest(location); //because the unpacking is being done in the shop
            packageStockItem.setQuantity(numOfPacksToSplit);
            mAcess.updateStock("ibalihikya",packageStockItem );

        }catch (Exception e1){
            e1.printStackTrace();
        }

    }
}
