package model.databaseUtility;

import model.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class MySqlAccess {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private final static String DATABASE_URL = "jdbc:postgresql://localhost/posdb_sm";
    private String databaseName;
    public MySqlAccess(String databaseName) {
        this.databaseName = databaseName;
    }

    public  int createDatabase(String sqlCreateDbStatement) throws ClassNotFoundException, SQLException {
        //Be sure to call the close() function to close the connection after creating tables
     dbConnect();
        statement = connect.createStatement();
        return statement.executeUpdate(sqlCreateDbStatement);
    }

    public  int createTable(String sqlStatement) throws SQLException {
        statement = connect.createStatement();
        return statement.executeUpdate(sqlStatement);
    }

    public  int dropDatabase(String sqlStatement) throws SQLException, ClassNotFoundException {
        dbConnect();
        statement = connect.createStatement();
        return statement.executeUpdate(sqlStatement);
    }

    /**
     * Initial product definition
     * @param product the product to to be defined in the product table
     * @throws Exception
     */

public void insertProduct(Product product) throws Exception {
    try {
        //load the mysql driver
        dbConnect();


        preparedStatement = connect
                .prepareStatement("insert into  " + databaseName + ".product values " +
                        "(default,?,?, ?, ? , ?, ?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setInt(1, product.getCategory().getCategoryId());
        preparedStatement.setString(2, product.getProductName());
        preparedStatement.setString(3,product.getDescription());
        preparedStatement.setDouble(4,product.getPrice());
        preparedStatement.setString(5,product.getBarcode());
        preparedStatement.setTimestamp(6,Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(7,product.getComment());
        preparedStatement.setTimestamp(8,Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.setString(9,product.getUnits());
        preparedStatement.setInt(10,product.getStockLowThreshold());
        preparedStatement.executeUpdate();

        resultSet = preparedStatement.getGeneratedKeys();
        resultSet.next();
        int productId = resultSet.getInt(1);

        //insert product into stockstatus table and set initial stockstatus to O
        preparedStatement = connect
                .prepareStatement("insert into  " + databaseName + ".stockstatus values " +
                        "(?,?,?);");
        preparedStatement.setInt(1, productId);
        preparedStatement.setInt(2, 0);
        preparedStatement.setTimestamp(3,Timestamp.valueOf(LocalDateTime.now()));
        preparedStatement.executeUpdate();
    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();
    }
}

public ArrayList<Product> getAllProducts() throws Exception{
    ArrayList<Product> productsList = new ArrayList<>();
    try{
        dbConnect();
        preparedStatement = connect
                .prepareStatement("SELECT product.* , category.* FROM " + databaseName + ".product " +
                                "LEFT JOIN " + databaseName + ".category ON product.categoryId = category.categoryId ORDER BY product.dateCreated;");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            Category category = new Category();
            category.setCategoryId(resultSet.getInt("categoryId"));
            category.setCategoryName(resultSet.getString("categoryName"));

            Product product = new Product();
            product.setProductId(resultSet.getInt("productId"));
            product.setProductName(resultSet.getString("ProductName"));
            product.setDescription(resultSet.getString("description"));
            product.setCategory(category);
            product.setPrice(resultSet.getDouble("price"));
            product.setUnits(resultSet.getString("units"));
            product.setStockLowThreshold(resultSet.getInt("stock_low_threshold"));
            product.setBarcode(resultSet.getString("barcode"));
            product.setDateCreated(resultSet.getDate("dateCreated"));
            product.setLastModifiedDate(resultSet.getDate("lastModifiedDate"));
            product.setComment(resultSet.getString("comments"));
            productsList.add(product);
        }

        return productsList;

    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();

    }

}

public void insertTransaction(Transaction transaction) throws Exception{
    try {
        //load the mysql driver
        dbConnect();
        for(Item item : transaction.getItems()) {
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".transaction values (default, ?, ?, ?, ?, ?,?)");

            preparedStatement.setInt(1, item.getProductId());
            preparedStatement.setInt(2, item.getQuantity());
            preparedStatement.setDouble(3, item.getPrice());
            preparedStatement.setDouble(4, item.getDiscount());
            preparedStatement.setInt(5, transaction.getReceiptId());
            preparedStatement.setDouble(6, item.getTotalPrice());
            preparedStatement.executeUpdate();
        }
    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();
    }
}

    private int insertReceipt(String userName) throws Exception{
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".receipt values (default,?, ?)", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1,userName );
            preparedStatement.setTimestamp(2,Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        }
        catch (Exception e){
            throw e;
        }
        finally {
            close();
        }
    }


public int getProductCount() throws Exception {

    try{
        dbConnect();


        preparedStatement = connect
                .prepareStatement("SELECT COUNT(*) AS productCount  FROM " + databaseName + ".product");
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        return resultSet.getInt("productCount");

    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();

    }

}

public int removeProduct(int productId) throws Exception{

    try{
        dbConnect();
        preparedStatement = connect
                .prepareStatement("DELETE FROM " + databaseName + ".product WHERE productId = ? ;");
        preparedStatement.setInt(1,productId);
       return preparedStatement.executeUpdate();

    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();

    }
}

/*public int updateProductQuantity(int productId, int quantity) throws  Exception{
    try{
        dbConnect();
        preparedStatement = connect
                .prepareStatement("UPDATE posdb.product SET quantity = ? WHERE productId = ? ;");
        preparedStatement.setInt(1,quantity);
        preparedStatement.setInt(2,productId);
        return preparedStatement.executeUpdate();

    }
    catch (Exception e){
        throw e;
    }
    finally {
        close();

    }
}*/

/*
//load the mysql driver
        Class.forName("com.mysql.jdbc.Driver");

        //set up db connection
        connect = DriverManager
                .getConnection(DATABASE_URL, "root", "Dreamalive1");
* */

    private void dbConnect() throws ClassNotFoundException, SQLException {
        //load the mysql driver
        //Class.forName("com.postgesql.jdbc.Driver");
        Class.forName("org.postgresql.Driver");

        //set up db connection
        connect = DriverManager
                .getConnection(DATABASE_URL, "postgres", "Dreamalive1");
    }

    public void close() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {

        }
    }

    public int generateReceiptId(String userName){
        int receiptId =0;
        try{
            receiptId =  insertReceipt(userName);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return receiptId;
    }

    public void createProductCategory(Category category) throws Exception{
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".category values (default, ?, ?)");
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
                        preparedStatement.executeUpdate();
        }
        catch (Exception e){
            throw e;
        }
        finally {
            close();
        }
    }

    public ArrayList<Category> getProductCategories() throws Exception {
        ArrayList<Category> productCategories = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT *  FROM " + databaseName + ".category");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Category category = new Category();
                category.setCategoryId(resultSet.getInt("categoryId"));
                category.setCategoryName(resultSet.getString("categoryName"));
                category.setDescription(resultSet.getString("description"));
                productCategories.add(category);
            }

            return productCategories;

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }



    public void addStock(int userId, int productId, int quantity, String comment) throws Exception{
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".stock values (default, ?, ?, ?, ? , ?, ?)");

            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setInt(3, userId);
            preparedStatement.setTimestamp(4,Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(5,comment);
            preparedStatement.setTimestamp(6,Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
        }
        catch (Exception e){
            throw e;
        }
        finally {
            close();
        }


    }

    public void addUnit(Unit unit) throws Exception{
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".units values (default, ?)");
            preparedStatement.setString(1, unit.getUnitName());
            preparedStatement.executeUpdate();
        }
        catch (Exception e){
            throw e;
        }
        finally {
            close();
        }
    }

    public ArrayList<Unit> getAllUnits() throws Exception{
        ArrayList<Unit> units = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT *  FROM " + databaseName + ".units");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Unit unit = new Unit();
                unit.setUnitId(resultSet.getInt("unitId"));
                unit.setUnitName(resultSet.getString("unit"));
                units.add(unit);
            }

            return units;

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }

    public ArrayList<StockItem> getStock() throws Exception{
        ArrayList<StockItem> stock = new ArrayList<>();
        try{
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT stock.* , product.productName FROM " +
                            databaseName + ".stock " +
                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                StockItem stockItem = new StockItem();
                stockItem.setQuantityStocked(resultSet.getInt("quantity"));
                stockItem.setProductId(resultSet.getInt("productId"));
                stockItem.setProductName(resultSet.getString("productName"));
                stockItem.setDateCreated(resultSet.getDate("dateCreated"));
                stockItem.setLastModifiedDate(resultSet.getDate("lastModifiedDate"));
                stockItem.setComment(resultSet.getString("comments"));
                stock.add(stockItem);
            }

            return stock;
        }
        catch (Exception e){
            throw e;
        }
        finally {
            close();

        }

    }

    public int registerUser(User user) throws SQLException, ClassNotFoundException {
        //load the mysql driver
        dbConnect();
        int result = 0;
     try {
        preparedStatement = connect
                //.prepareStatement("insert into  "  + "users values (?,?,?,?,?,?)");
        .prepareStatement("insert into  " + databaseName + ".users values (?,?,?,?,?,?)");

         preparedStatement.setString(1, user.getUserName());
        preparedStatement.setString(2, user.getFirstName());
        preparedStatement.setString(3, user.getLastName());
        preparedStatement.setString(4, user.getPassword());
        preparedStatement.setBytes(5,user.getSalt());
        preparedStatement.setBoolean(6, user.isAdmin());
        result = preparedStatement.executeUpdate();
    }
        catch (Exception e){
        //throw e;
            e.printStackTrace();
    }
        finally {
        close();
        return result;
    }

    }

    public User getUser(String username) throws SQLException, ClassNotFoundException {
        User user = null;
        try{
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("select * from " + databaseName + ".users where users.username = ?;");
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            user = new User();
            user.setUserName(resultSet.getString("username"));
            user.setFirstName(resultSet.getString("firstname"));
            user.setLastName(resultSet.getString("lastname"));
            user.setPassword(resultSet.getString("password"));
            user.setSalt(resultSet.getBytes("salt"));
            user.setAdmin(resultSet.getBoolean("isAdmin"));
        }
        catch (Exception e){
            throw e;

        }
        finally {
            close();
            return user;
        }
    }

    public ArrayList<Item> getItemsSold(String startDate, String endDate) throws SQLException, ClassNotFoundException {


        dbConnect();
        preparedStatement = connect
                .prepareStatement("select transaction.*,  receipt.*, product.productName from " + databaseName +".transaction  " +
                        "left join " + databaseName +  ".receipt on transaction.receiptId = receipt.receiptId " +
                        "left join " + databaseName + ".product on transaction.productId = product.productId where " +
                        "receipt.lastModifiedDate > ? AND receipt.lastModifiedDate < ?;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:00");


        preparedStatement.setTimestamp(1,startTimestamp);
        preparedStatement.setTimestamp(2,endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()){
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getInt("quantity"));
            item.setProductId(resultSet.getInt("productId"));
            item.setReceiptId(resultSet.getInt("receiptId"));
            item.setTotalPrice(resultSet.getDouble("total"));

            Timestamp timestamp = resultSet.getTimestamp("lastModifiedDate");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }

        close();
        return items;
    }

    //Get the sales revenue for each product for the period under consideration
    public ArrayList<Item> getItemsSoldSummary(String startDate, String endDate) throws SQLException, ClassNotFoundException {

        dbConnect();
        preparedStatement = connect
                .prepareStatement("select product.productname, sum(transaction.total) as sum, sum(transaction.quantity) " +
                        "as productcount from " + databaseName + ".transaction left join " + databaseName + ".product on " +
                        "transaction.productid = product.productid " +
                        "left join " + databaseName +  ".receipt on transaction.receiptId = receipt.receiptId " +
                        "WHERE receipt.lastModifiedDate > ? AND receipt.lastModifiedDate < ? GROUP BY product.productname;");


        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:00");

        preparedStatement.setTimestamp(1,startTimestamp);
        preparedStatement.setTimestamp(2,endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()){
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            //item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getInt("productcount"));
            //item.setProductId(resultSet.getInt("productId"));
            item.setTotalPrice(resultSet.getDouble("sum"));
            items.add(item);
        }

        close();
        return items;
    }




    public ArrayList<StockItem> getStockStatus() throws SQLException, ClassNotFoundException {
        ArrayList<StockItem> stockItems = new ArrayList<>();
            dbConnect();
        //SELECT stockstatus.*, product.productName FROM posdb.stockstatus LEFT JOIN product ON stockstatus.productId = product.productId;
            preparedStatement = connect
                    .prepareStatement("SELECT stockstatus.*, product.productName, product.stock_low_threshold  FROM " + databaseName + ".stockstatus " +
                            "LEFT JOIN " + databaseName + ".product ON stockstatus.productId = product.productId;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockItem stockItem = new StockItem();
                stockItem.setProductName(resultSet.getString("productName"));
                stockItem.setQuantityStocked(resultSet.getInt("quantity"));
                stockItem.setStockLowThreshold(resultSet.getInt("stock_low_threshold"));

                Timestamp timestamp = resultSet.getTimestamp("dateUpdated");
                Date date = new Date(timestamp.getTime());
                stockItem.setLastModifiedDate(date);
                stockItems.add(stockItem);
            }

        close();
        return stockItems;
    }


    public Double getTotalRevenue(String startDate, String endDate) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("SELECT sum(total) AS totalrevenue  FROM " + databaseName + ".transaction " +
                        "left join " + databaseName +  ".receipt on transaction.receiptId = receipt.receiptId WHERE " +
                        "receipt.lastModifiedDate > ? AND receipt.lastModifiedDate < ?;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:00");

        preparedStatement.setTimestamp(1,startTimestamp);
        preparedStatement.setTimestamp(2,endTimestamp);

        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        Double totalrevenue = resultSet.getDouble("totalrevenue");
        close();
        return totalrevenue;
    }

    //Get sales revenue by date
    public ArrayList<Hashtable<String, Object>> getSalesbyDate(String startDate, String endDate)
            throws SQLException, ClassNotFoundException {

        dbConnect();
        preparedStatement = connect
                .prepareStatement("select receipt.lastmodifieddate::timestamp::date, sum(transaction.total) as sum, " +
                        "sum(transaction.quantity) as productquantity from " + databaseName + ".transaction " +
                        "left join " + databaseName + ".product on transaction.productid = product.productid " +
                        "left join " + databaseName +  ".receipt on transaction.receiptId = receipt.receiptId " +
                        "WHERE receipt.lastModifiedDate > ? AND receipt.lastModifiedDate < ? " +
                        "GROUP BY receipt.lastmodifieddate::timestamp::date;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:00");

        preparedStatement.setTimestamp(1,startTimestamp);
        preparedStatement.setTimestamp(2,endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Hashtable<String, Object>> sales = new ArrayList<>();

        while (resultSet.next()){
            Hashtable<String, Object> sale = new Hashtable<String, Object>();
            sale.put("date", resultSet.getDate("lastmodifieddate"));
            sale.put("sum", resultSet.getDouble("sum"));
            sale.put("productquantity", resultSet.getInt("productquantity"));

            sales.add(sale);
        }

        close();
        return sales;
    }

    //Get sales revenue for a given product by date
    public ArrayList<Hashtable<String, Object>> getSalesForProductByDate(String startDate, String endDate, int productid)
            throws SQLException, ClassNotFoundException {

        dbConnect();
        preparedStatement = connect
                .prepareStatement("select receipt.lastmodifieddate::timestamp::date, sum(transaction.total) as sum, " +
                        "sum(transaction.quantity) as productquantity from " + databaseName + ".transaction " +
                        "left join " + databaseName + ".product on transaction.productid = product.productid " +
                        "left join " + databaseName +  ".receipt on transaction.receiptId = receipt.receiptId " +
                        "WHERE receipt.lastModifiedDate > ? AND receipt.lastModifiedDate < ? AND transaction.productid=? " +
                        "GROUP BY receipt.lastmodifieddate::timestamp::date;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:00");

        preparedStatement.setTimestamp(1,startTimestamp);
        preparedStatement.setTimestamp(2,endTimestamp);
        preparedStatement.setInt(3,productid);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Hashtable<String, Object>> sales = new ArrayList<>();

        while (resultSet.next()){
            Hashtable<String, Object> sale = new Hashtable<String, Object>();
            sale.put("date", resultSet.getDate("lastmodifieddate"));
            sale.put("sum", resultSet.getDouble("sum"));
            sale.put("productquantity", resultSet.getInt("productquantity"));

            sales.add(sale);
        }

        close();
        return sales;
    }

    //Get a product with the given bardode
    public Product getProduct(String barcode) throws SQLException, ClassNotFoundException{
        dbConnect();
        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".product " +
                "where product.barcode=?;");

        preparedStatement.setString(1, barcode);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Product product = new Product();
        product.setProductId(resultSet.getInt("productId"));
        product.setProductName(resultSet.getString("ProductName"));
        product.setDescription(resultSet.getString("description"));
        //product.setCategory(category);
        product.setPrice(resultSet.getDouble("price"));
        product.setUnits(resultSet.getString("units"));
        product.setStockLowThreshold(resultSet.getInt("stock_low_threshold"));
        product.setBarcode(resultSet.getString("barcode"));
        product.setDateCreated(resultSet.getDate("dateCreated"));
        product.setLastModifiedDate(resultSet.getDate("lastModifiedDate"));
        product.setComment(resultSet.getString("comments"));

       return product;
    }


}
