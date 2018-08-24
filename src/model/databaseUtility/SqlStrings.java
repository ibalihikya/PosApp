package model.databaseUtility;


public final class SqlStrings {

    public static final String PRODUCTION_DB_NAME = "public";
    //Test database
    public static final String TEST_DB_NAME = "posdb_test";
    public static final String sqlCreateDbStatement = "CREATE DATABASE " + TEST_DB_NAME;
    public static final String sqlCreateCategoryTable = "CREATE TABLE " + TEST_DB_NAME + ".`category` (" +
            "`categoryId` int(11) NOT NULL AUTO_INCREMENT," +
            "`categoryName` varchar(100) NOT NULL," +
            "`description` varchar(100) DEFAULT NULL," +
            "PRIMARY KEY (`categoryId`));";

    public static final String sqlCreateProductTable = "CREATE TABLE " + TEST_DB_NAME + ".`product` (" +
            "`productId` int(11) NOT NULL AUTO_INCREMENT," +
            "`categoryId` int(11) DEFAULT NULL," +
            "`productName` varchar(100) NOT NULL," +
            "`description` varchar(100) DEFAULT NULL," +
            "`price` double NOT NULL," +
            "`barcode` varchar(100) DEFAULT NULL," +
            "`lastModifiedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP " +
            "ON UPDATE CURRENT_TIMESTAMP," +
            "`comments` varchar(400) DEFAULT NULL," +
            "`dateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "`units` varchar(50) DEFAULT NULL," +
            "PRIMARY KEY (`productId`));";

    public static final String sqlCreateReceiptTable = "CREATE TABLE " + TEST_DB_NAME + ".`receipt` (" +
            "`receiptId` int(11) NOT NULL AUTO_INCREMENT," +
            "`userId` int(11) NOT NULL," +
            "`lastModifiedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP " +
            "ON UPDATE CURRENT_TIMESTAMP," +
            "PRIMARY KEY (`receiptId`));";

    public static final String sqlCreateStockTable = "CREATE TABLE " + TEST_DB_NAME + ".`stock` (" +
            "`transactionId` int(11) NOT NULL AUTO_INCREMENT," +
            "`productId` int(11) NOT NULL," +
            "`quantity` int(11) NOT NULL," +
            "`userId` int(11) NOT NULL," +
            "`lastModifiedDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP " +
            "ON UPDATE CURRENT_TIMESTAMP," +
            "`comments` varchar(400) DEFAULT NULL," +
            "`dateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "PRIMARY KEY (`transactionId`));";

    public static final String sqlCreateTransactionTable =  "CREATE TABLE " + TEST_DB_NAME +  ".`transaction` (" +
            "`transactionId` int(11) NOT NULL AUTO_INCREMENT," +
            "`productId` int(11) NOT NULL," +
            "`quantity` int(11) NOT NULL," +
            "`price` double NOT NULL," +
            "`discount` double DEFAULT NULL," +
            "`ReceiptId` int(11) NOT NULL," +
            "`total` double NOT NULL," +
            "PRIMARY KEY (`transactionId`));";

    public static final String sqlCreateUnitsTable = "CREATE TABLE " + TEST_DB_NAME + ".`units` (" +
            "`unitId` int(11) NOT NULL AUTO_INCREMENT," +
            "`unit` varchar(15) NOT NULL," +
            "PRIMARY KEY (`unitId`));";

    public static final String sqlCreateUsersTable = "CREATE TABLE " + TEST_DB_NAME + ".`users` (" +
            "`username` varchar(20) NOT NULL," +
            "`firstname` varchar(20) NOT NULL," +
            "`lastname` varchar(20) NOT NULL," +
            "`password` varchar(50) NOT NULL," +
            "`salt` BLOB NOT NULL," +
            "`isAdmin`  bool NOT NULL DEFAULT false," +
            "PRIMARY KEY (`username`));";

    public static final String sqlCreateStockStatusTable = "CREATE TABLE " + TEST_DB_NAME + ".`stockStatus` (" +
            "`productId` int(11) NOT NULL ," +
            "`quantity` int(11) NOT NULL ," +
            "`dateUpdated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (`productId`));";

    public static final String sqlCreateTriggerStockStatus_transaction = "CREATE TRIGGER " + TEST_DB_NAME + ".`tg_stockstatus_transaction` " +
            "AFTER INSERT ON transaction " +
            "FOR EACH ROW " +
            "UPDATE stockstatus " +
            "SET quantity = quantity - NEW.quantity " +
            "WHERE productId = NEW.productId ;";

    public static final String sqlCreateTriggerStockStatus_stock = "CREATE TRIGGER " + TEST_DB_NAME + ".`tg_stockstatus_stock` " +
            "AFTER INSERT ON stock " +
            "FOR EACH ROW " +
            "UPDATE stockstatus " +
            "SET quantity = quantity + NEW.quantity " +
            "WHERE productId = NEW.productId ;";

    public static final String sqlDropDatabase = "DROP DATABASE " + TEST_DB_NAME;

}
