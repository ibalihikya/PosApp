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
    private PreparedStatement preparedStatement2 = null;
    private ResultSet resultSet = null;
    //private final static String DATABASE_URL = "jdbc:postgresql://localhost/posdb_sm";
    private String databaseUrl=null;
    private String databaseName;
    private String serverIp="";

    public MySqlAccess(String databaseName, String serverIP) {
        this.databaseName = databaseName;
        this.serverIp=serverIP;
        databaseUrl= "jdbc:postgresql://" + serverIP +"/posdb_sm";
    }

    public int createDatabase(String sqlCreateDbStatement) throws ClassNotFoundException, SQLException {
        //Be sure to call the close() function to close the connection after creating tables
        dbConnect();
        statement = connect.createStatement();
        return statement.executeUpdate(sqlCreateDbStatement);
    }

    public int createTable(String sqlStatement) throws SQLException {
        statement = connect.createStatement();
        return statement.executeUpdate(sqlStatement);
    }

    public int dropDatabase(String sqlStatement) throws SQLException, ClassNotFoundException {
        dbConnect();
        statement = connect.createStatement();
        return statement.executeUpdate(sqlStatement);
    }

    /**
     * Initial product definition
     *
     * @param product the product to to be defined in the product table
     * @throws Exception
     */

    public void insertProduct(Product product) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".product values " +
                            "(default,?,?, ?, ? , ?, ?,?,?,?,?,default,?)", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, product.getCategory().getCategoryId());
            preparedStatement.setString(2, product.getProductName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setString(5, product.getBarcode());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, product.getComment());
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(9, product.getUnits());
            preparedStatement.setDouble(10, product.getCostprice());
            preparedStatement.setDouble(11, product.getStockLowThreshold());

            preparedStatement.executeUpdate();

//            resultSet = preparedStatement.getGeneratedKeys();
//            resultSet.next();
//            int productId = resultSet.getInt(1);
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<Product> getAllProducts() throws Exception {
        ArrayList<Product> productsList = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT product.* , category.* FROM " + databaseName + ".product " +
                            "LEFT JOIN " + databaseName + ".category ON product.categoryId = category.categoryId ORDER BY product.lastmodifieddate DESC;");
            //"LEFT JOIN " + databaseName + ".category ON product.categoryId = category.categoryId ORDER BY product.dateCreated;");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Category category = new Category();
                category.setCategoryId(resultSet.getInt("categoryId"));
                category.setCategoryName(resultSet.getString("categoryName"));

                Product product = new Product();
                product.setProductId(resultSet.getInt("productId"));
                product.setProductName(resultSet.getString("ProductName"));
                product.setDescription(resultSet.getString("description"));
                product.setCategory(category);
                product.setCostprice(resultSet.getDouble("costprice"));
                product.setPrice(resultSet.getDouble("price"));
                product.setMarkup(resultSet.getDouble("margin"));
                product.setUnits(resultSet.getString("units"));
                product.setStockLowThreshold(resultSet.getDouble("stock_low_threshold"));
                product.setBarcode(resultSet.getString("barcode"));

                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                product.setDateCreated(time_created);

                timestamp = resultSet.getTimestamp("lastModifiedDate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                product.setLastModifiedDate(time_modified);

                product.setComment(resultSet.getString("comments"));
                productsList.add(product);
            }

            return productsList;

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }


    public int generateInvoice(CustomerTransaction transaction, InvoiceStatus invoiceStatus,int tillNumber) throws Exception {
        int invoice_id = 0;
        try {
            //load the mysql driver
            dbConnect();
            //TODO: This conditional is to satisfy foreign key constraint for customer_id. May need to improve this logic.

            if(transaction.getCustomerId()!=0) {
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".invoice " +
                                "(username, customer_id, amount, status, tillnumber ) " +
                                "values (?, ?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, transaction.getUserName());
                preparedStatement.setInt(2, transaction.getCustomerId());
                preparedStatement.setDouble(3, transaction.getAmount());
                preparedStatement.setString(4, String.valueOf(invoiceStatus));
                preparedStatement.setInt(5, tillNumber);

            }else{
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".invoice " +
                                "(username, amount, status,tillnumber) " +
                                "values (?, ?, ?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, transaction.getUserName());
                preparedStatement.setDouble(2, transaction.getAmount());
                preparedStatement.setString(3, String.valueOf(invoiceStatus));
                preparedStatement.setInt(4, tillNumber);
            }

            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            invoice_id = resultSet.getInt(1);

            for (Item item : transaction.getItems()) {
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".item " +
                                "(productid, quantity, price,discount, invoice_id, total) " +
                                " values (?, ?, ?, ?, ?,?)");

                preparedStatement.setInt(1, item.getProductId());
                preparedStatement.setDouble(2, item.getQuantity());
                preparedStatement.setDouble(3, item.getPrice());
                preparedStatement.setDouble(4, item.getDiscount());
                preparedStatement.setInt(5, invoice_id);
                preparedStatement.setDouble(6, item.getTotalPrice());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
             e.printStackTrace();
        } finally {
            close();
            return invoice_id;
        }
    }


//    public void insertDelivery(Transaction transaction) throws Exception{
//        try {
//            //load the mysql driver
//            dbConnect();
//            for(Item item : transaction.getItems()) {
//                preparedStatement = connect
//                        .prepareStatement("insert into  " + databaseName + ".stransactions (description, " +
//                                "supplier_id, transaction_type, amount) values (?, ?, ?, ? )", Statement.RETURN_GENERATED_KEYS);
//
//                preparedStatement.setString(1, item.getProductName());
//                preparedStatement.setInt(2, item.getSellerid());
//                preparedStatement.setString(3, "d");
//                preparedStatement.setDouble(4, item.computeTotalPrice());
//                preparedStatement.executeUpdate();
//
//                resultSet = preparedStatement.getGeneratedKeys();
//                resultSet.next();
//                int transactionId = resultSet.getInt(1);
//
//                preparedStatement = connect
//                        .prepareStatement("insert into  " + databaseName + ".stransactions_delivery " +
//                                "(id, transaction_type, product_id, quantity, price, supplier_invoice) " +
//                                "values (?, ?, ?, ?, ?, ?);");
//
//                preparedStatement.setInt(1, transactionId);
//                preparedStatement.setString(2, "d");
//                preparedStatement.setInt(3, item.getProductId());
//                preparedStatement.setDouble(4, item.getQuantity());
//                preparedStatement.setDouble(5, item.getPrice());
//                preparedStatement.setDouble(6, item.getInvoiceNumber());
//                preparedStatement.executeUpdate();
//
//                preparedStatement = connect
//                        .prepareStatement("insert into  " + databaseName + ".delivery " +
//                                "(product_id, transaction_id, quantity, price, total, supplier_invoice) " +
//                                "values (?, ?, ?, ?, ?, ?);");
//                preparedStatement.setInt(1, item.getProductId());
//                preparedStatement.setInt(2, transactionId);
//                preparedStatement.setDouble(3, item.getQuantity());
//                preparedStatement.setDouble(4, item.getPrice());
//                preparedStatement.setDouble(5, item.getTotalPrice());
//                preparedStatement.setDouble(6, item.getInvoiceNumber());
//                preparedStatement.executeUpdate();
//            }
//        }
//        catch (Exception e){
//            throw e;
//        }
//        finally {
//            close();
//        }
//    }

    public void insertDelivery(Transaction transaction) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".stransactions (description, " +
                            "supplier_id, transaction_type, amount) values (?, ?, ?, ? )", Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, "Delivery");
            preparedStatement.setInt(2, transaction.getSellerId());
            preparedStatement.setString(3, "d");
            preparedStatement.setDouble(4, transaction.computeGrandTotal2());
            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            int transactionId = resultSet.getInt(1);

            for (Item item : transaction.getItems()) {
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".delivery " +
                                "(product_id, transaction_id, quantity, price, total, supplier_invoice) " +
                                "values (?, ?, ?, ?, ?, ?);");
                preparedStatement.setInt(1, item.getProductId());
                preparedStatement.setInt(2, transactionId);
                preparedStatement.setDouble(3, item.getQuantity());
                preparedStatement.setDouble(4, item.getPrice());
                preparedStatement.setDouble(5, item.getTotalPrice());
                preparedStatement.setDouble(6, item.getInvoiceNumber());
                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }


    //TODO: Improve this function design, see TODO on generatereceiptid function call in UI class
    private int insertReceipt(String userName, String transaction_type, int customer_id, double amount,
                              double cash_received, double change) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            if (customer_id != 0) { //TODO: remove this temporary fix for customerid column when transaction is cash
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".receipt " +
                                "(username, transaction_type, customer_id, amount, cash_received, change) " +
                                "values (?, ?, ?, ?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, transaction_type);
                preparedStatement.setInt(3, customer_id);
                preparedStatement.setDouble(4, amount);
                preparedStatement.setDouble(5, cash_received);
                preparedStatement.setDouble(6, change);
            } else {
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".receipt " +
                                "(username, transaction_type, amount, cash_received, change) " +
                                "values (?, ?, ?,?,?)", Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, userName);
                preparedStatement.setString(2, transaction_type);
                preparedStatement.setDouble(3, amount);
                preparedStatement.setDouble(4, cash_received);
                preparedStatement.setDouble(5, change);
            }


            //preparedStatement.setTimestamp(2,Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }


    public int getProductCount() throws Exception {

        try {
            dbConnect();


            preparedStatement = connect
                    .prepareStatement("SELECT COUNT(*) AS productCount  FROM " + databaseName + ".product");
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("productCount");

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }

    public int deleteProduct(int productId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".product WHERE productId = ? ;");
            preparedStatement.setInt(1, productId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    //delete all transactions for the given product
    public int deleteAllTransactions(int productId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".transaction WHERE productid = ? ;");
            preparedStatement.setInt(1, productId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    //delete all stocking details for a given product. Dangerous procedure.
    public int deleteStock(int productId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".stock WHERE productId = ? ;");
            preparedStatement.setInt(1, productId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public int deleteStockTransaction(int transactionId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".stock WHERE transactionid = ? ;");
            preparedStatement.setInt(1, transactionId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    //delete the given product's entries in the stockstatus table
    public int deleteStockStatus(int productId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".stockstatus WHERE productId = ? ;");
            preparedStatement.setInt(1, productId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
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
                .getConnection(databaseUrl, "postgres", "Dreamalive1");
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



    public void createProductCategory(Category category) throws Exception {
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".category values (default, ?, ?)");
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
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


    public int updateStock(String username, StockItem stockItem) throws Exception {
        int transaction_id = 0;
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".stock (username, productid, quantity, " +
                            "location_id, direction, source_dest_id,  comments) " +
                            "values (?, ?, ?, ? , ?,?,?)", Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, stockItem.getProductId());
            preparedStatement.setDouble(3, stockItem.getQuantity());
            preparedStatement.setInt(4, stockItem.getLocation().getId());
            preparedStatement.setString(5, stockItem.getDirection().toString());
            preparedStatement.setInt(6, stockItem.getSource_dest().getId());
            preparedStatement.setString(7, stockItem.getComment());
            preparedStatement.executeUpdate();
            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            transaction_id = resultSet.getInt(1);
            //stockItem.setTransactionId(resultSet.getInt("transactionid"));
            //stockItem.setBalance(resultSet.getDouble("balance"));

//            Timestamp timestamp = resultSet.getTimestamp("dateCreated");
//            String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
//            stockItem.setDateCreated(dateCreated);

        } catch (Exception e) {
            throw e;
        } finally {
            close();
            return  transaction_id;
            //return stockItem;
        }


    }

    public void addUnit(Unit unit){
        try {
            //load the mysql driver
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".units values (default, ?)");
            preparedStatement.setString(1, unit.getUnitName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public ArrayList<Unit> getAllUnits() {
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

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return units;

        }

    }


//    public ArrayList<StockItem> getStock() throws Exception {
//        ArrayList<StockItem> stock = new ArrayList<>();
//        try {
//            dbConnect();
//            //TODO: query leaves out the sales transactions to minimize the size of returned data. Improve the UI With
//            // filter options so user can select only the required info
//            preparedStatement = connect
//                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
//                            databaseName + ".stock " +
//                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
//                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
//                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId WHERE " +
//                            "(location_id, direction, source_dest_id) != (2,'out', 2) ORDER BY lastmodifieddate DESC;");
//
//            //select * from stock where (location, direction, source_dest) != ('shop','out', 'customer');
//            resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                StockItem stockItem = new StockItem();
//                stockItem.setTransactionId(resultSet.getInt("transactionid"));
//                stockItem.setQuantity(resultSet.getInt("quantity"));
//                stockItem.setBalance(resultSet.getDouble("balance"));
//                stockItem.setProductId(resultSet.getInt("productId"));
//                stockItem.setProductName(resultSet.getString("productName"));
//
//                Site location = new Site(resultSet.getInt("location_id"),resultSet.getString("location"));
//                Site source_dest = new Site(resultSet.getInt("source_dest_id"),resultSet.getString("source_dest"));
//                stockItem.setLocation(location);
//                stockItem.setSource_dest(source_dest);
//                stockItem.setDirection(Direction.valueOf(resultSet.getString("direction")));
//
//
//                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
//                String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
//                stockItem.setDateCreated(dateCreated);
//
//                timestamp = resultSet.getTimestamp("lastmodifieddate");
//                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
//                stockItem.setLastModifiedDate(date_modified);
//
//                stockItem.setComment(resultSet.getString("comments"));
//                stockItem.setTransactionId(resultSet.getInt("transactionid"));
//                stock.add(stockItem);
//            }
//
//            return stock;
//        } catch (Exception e) {
//            throw e;
//        } finally {
//            close();
//
//        }
//
//    }

    public ArrayList<StockItem> getStock() throws Exception {
        ArrayList<StockItem> stock = new ArrayList<>();
        try {
            dbConnect();

            //TODO note the 100 records limit - may need to indicate on the jtable that only last 100 records are displayed
//            preparedStatement = connect
//                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
//                            databaseName + ".stock " +
//                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
//                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
//                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId WHERE " +
//                            "(location_id, direction, source_dest_id) != (2,'out', 2) ORDER BY datecreated DESC limit 100;");

            preparedStatement = connect
                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
                            databaseName + ".stock " +
                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId " +
                            " ORDER BY datecreated DESC limit 100;");

            //select * from stock where (location, direction, source_dest) != ('shop','out', 'customer');
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockItem stockItem = new StockItem();
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stockItem.setQuantity(resultSet.getInt("quantity"));
                stockItem.setBalance(resultSet.getDouble("balance"));
                stockItem.setProductId(resultSet.getInt("productId"));
                stockItem.setProductName(resultSet.getString("productName"));

                Site location = new Site(resultSet.getInt("location_id"),resultSet.getString("location"));
                Site source_dest = new Site(resultSet.getInt("source_dest_id"),resultSet.getString("source_dest"));
                stockItem.setLocation(location);
                stockItem.setSource_dest(source_dest);
                stockItem.setDirection(Direction.valueOf(resultSet.getString("direction")));


                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setDateCreated(dateCreated);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setLastModifiedDate(date_modified);

                stockItem.setComment(resultSet.getString("comments"));
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stock.add(stockItem);
            }

            return stock;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<StockItem> getStock(String startDate, String endDate) throws Exception {
        ArrayList<StockItem> stock = new ArrayList<>();
        try {
            dbConnect();
            //TODO: query leaves out the sales transactions to minimize the size of returned data. Improve the UI With
            // filter options so user can select only the required info
            //also note the 500 records limit - may need to indicate on the jtable that only last 500 records are displayed
//            preparedStatement = connect
//                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
//                            databaseName + ".stock " +
//                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
//                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
//                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId WHERE " +
//                            "(location_id, direction, source_dest_id) != (2,'out', 2) " +
//                            " AND (stock.datecreated > ? AND stock.datecreated < ?) " +
//                            "ORDER BY datecreated DESC;");

            preparedStatement = connect
                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
                            databaseName + ".stock " +
                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId WHERE " +
                            " (stock.datecreated > ? AND stock.datecreated < ?) " +
                            "ORDER BY datecreated DESC;");

            //TODO: remove hard coded values
            Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
            Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

            preparedStatement.setTimestamp(1, startTimestamp);
            preparedStatement.setTimestamp(2, endTimestamp);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockItem stockItem = new StockItem();
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stockItem.setQuantity(resultSet.getInt("quantity"));
                stockItem.setBalance(resultSet.getDouble("balance"));
                stockItem.setProductId(resultSet.getInt("productId"));
                stockItem.setProductName(resultSet.getString("productName"));

                Site location = new Site(resultSet.getInt("location_id"),resultSet.getString("location"));
                Site source_dest = new Site(resultSet.getInt("source_dest_id"),resultSet.getString("source_dest"));
                stockItem.setLocation(location);
                stockItem.setSource_dest(source_dest);
                stockItem.setDirection(Direction.valueOf(resultSet.getString("direction")));


                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setDateCreated(dateCreated);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setLastModifiedDate(date_modified);

                stockItem.setComment(resultSet.getString("comments"));
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stock.add(stockItem);
            }

            return stock;
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    //select * from stock where transactionid in (select max(transactionid) from stock  group by (location_id, productid) ) AND productid=430 ;

    public ArrayList<StockItem> getStock(int productId) throws Exception {
        ArrayList<StockItem> stock = new ArrayList<>();
        try {
            dbConnect();
            //TODO: query leaves out the sales transactions to minimize the size of returned data. Improve the UI With
            // filter options so user can select only the required info
            preparedStatement = connect
                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
                            databaseName + ".stock " +
                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId " +
                            "WHERE transactionid in (select max(transactionid) from stock  group by (location_id, productid) ) AND stock.productid=? ;");

            preparedStatement.setInt(1, productId);

//            preparedStatement = connect
//                    .prepareStatement("SELECT stock.* , product.productName FROM " +
//                            databaseName + ".stock " +
//                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId where " +
//                            "(location, direction, source_dest) != ('shop','out', 'customer') ORDER BY datecreated  DESC;");

//            preparedStatement = connect
//                    .prepareStatement("SELECT stock.* , product.productName FROM " +
//                            databaseName + ".stock " +
//                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId where " +
//                            "(direction, source_dest) != ('out', 'customer') ORDER BY datecreated  DESC;");


            //select * from stock where (location, direction, source_dest) != ('shop','out', 'customer');
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                StockItem stockItem = new StockItem();
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stockItem.setQuantity(resultSet.getInt("quantity"));
                stockItem.setBalance(resultSet.getDouble("balance"));
                stockItem.setProductId(resultSet.getInt("productId"));
                stockItem.setProductName(resultSet.getString("productName"));

                Site location = new Site(resultSet.getInt("location_id"),resultSet.getString("location"));
                Site source_dest = new Site(resultSet.getInt("source_dest_id"),resultSet.getString("source_dest"));
                stockItem.setLocation(location);
                stockItem.setSource_dest(source_dest);
                stockItem.setDirection(Direction.valueOf(resultSet.getString("direction")));


                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setDateCreated(dateCreated);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setLastModifiedDate(date_modified);


                stockItem.setComment(resultSet.getString("comments"));
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stock.add(stockItem);
            }

            return stock;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }


    public StockItem getStockItem(int transactionId) throws Exception {
        ArrayList<StockItem> stock = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT stock.* , product.productName, location.location, source_dest.source_dest FROM " +
                            databaseName + ".stock " +
                            "LEFT JOIN " + databaseName + ".location ON stock.location_id = location.id " +
                            "LEFT JOIN " + databaseName + ".source_dest ON stock.source_dest_id = source_dest.id " +
                            "LEFT JOIN " + databaseName + ".product ON stock.productId = product.productId " +
                            "WHERE transactionid = ? ;");

            preparedStatement.setInt(1, transactionId);


            resultSet = preparedStatement.executeQuery();
            resultSet.next();
                StockItem stockItem = new StockItem();
                stockItem.setTransactionId(resultSet.getInt("transactionid"));
                stockItem.setQuantity(resultSet.getInt("quantity"));
                stockItem.setBalance(resultSet.getDouble("balance"));
                stockItem.setProductId(resultSet.getInt("productId"));
                stockItem.setProductName(resultSet.getString("productName"));

                Site location = new Site(resultSet.getInt("location_id"),resultSet.getString("location"));
                Site source_dest = new Site(resultSet.getInt("source_dest_id"),resultSet.getString("source_dest"));
                stockItem.setLocation(location);
                stockItem.setSource_dest(source_dest);
                stockItem.setDirection(Direction.valueOf(resultSet.getString("direction")));


                Timestamp timestamp = resultSet.getTimestamp("dateCreated");
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setDateCreated(dateCreated);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                stockItem.setLastModifiedDate(date_modified);

                stockItem.setComment(resultSet.getString("comments"));
                stockItem.setTransactionId(resultSet.getInt("transactionid"));

            return stockItem;
        } catch (Exception e) {
            throw e;
        } finally {
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
            preparedStatement.setBytes(5, user.getSalt());
            preparedStatement.setBoolean(6, user.isAdmin());
            result = preparedStatement.executeUpdate();
        } catch (Exception e) {
            //throw e;
            e.printStackTrace();
        } finally {
            close();
            return result;
        }

    }

    public User getUser(String username) throws SQLException, ClassNotFoundException {
        User user = null;
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("select * from " + databaseName + ".users where users.username = ?;");
            preparedStatement.setString(1, username);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            user = new User();
            user.setUserName(resultSet.getString("username"));
            user.setFirstName(resultSet.getString("firstname"));
            user.setLastName(resultSet.getString("lastname"));
            user.setPassword(resultSet.getString("password"));
            user.setSalt(resultSet.getBytes("salt"));
            user.setAdmin(resultSet.getBoolean("isAdmin"));
        } catch (Exception e) {
            throw e;

        } finally {
            close();
            return user;
        }
    }

    public ArrayList<Item> getItemsSold(String startDate, String endDate) throws SQLException, ClassNotFoundException {


        dbConnect();
        preparedStatement = connect
                .prepareStatement("select item.*, invoice.username, invoice.date_created,invoice.status, invoice.tillnumber,  product.productName from " + databaseName + ".item  " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "left join " + databaseName + ".product on item.productid = product.productid where " +
                        "invoice.date_created >= ? AND invoice.date_created <= ? ORDER BY invoice.date_created DESC;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");


        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productname"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            item.setProductId(resultSet.getInt("productid"));
            item.setTill(resultSet.getInt("tillnumber"));

            item.setInvoiceNumber(resultSet.getInt("invoice_id"));
            item.setTotalPrice(resultSet.getDouble("total"));
            item.setMargin(resultSet.getDouble("margin"));
            item.setSellername(resultSet.getString("username"));
            //item.setTransactionId(resultSet.getInt("transactionid")); //TODO: replace this with line below if necessary
            item.setTransactionId(resultSet.getInt("id"));
            item.setInvoiceStatus(InvoiceStatus.valueOf(resultSet.getString("status")));

            Timestamp timestamp = resultSet.getTimestamp("date_created");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }

        close();
        return items;
    }


    public ArrayList<Item> getItemsSold(String startDate, String endDate, int productId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("select item.*, invoice.username, invoice.date_created, invoice.status, invoice.tillnumber product.productname from " + databaseName + ".item  " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "left join " + databaseName + ".product on item.productid = product.productid where " +
                        "product.productid = ? AND invoice.date_created > ? AND invoice.date_created < ? ORDER BY invoice.date_created DESC;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setInt(1, productId);
        preparedStatement.setTimestamp(2, startTimestamp);
        preparedStatement.setTimestamp(3, endTimestamp);


        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productname"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            item.setProductId(resultSet.getInt("productid"));
            item.setTill(resultSet.getInt("tillnumber"));
            //item.setReceiptId(resultSet.getInt("receiptId"));
            item.setInvoiceNumber(resultSet.getInt("invoice_id"));
            item.setTotalPrice(resultSet.getDouble("total"));
            item.setMargin(resultSet.getDouble("margin"));
            item.setSellername(resultSet.getString("username"));
            //item.setTransactionId(resultSet.getInt("transactionid")); //TODO: replace this with line below if necessary
            item.setTransactionId(resultSet.getInt("id"));
            item.setInvoiceStatus(InvoiceStatus.valueOf(resultSet.getString("status")));

            Timestamp timestamp = resultSet.getTimestamp("date_created");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }

        close();
        return items;
    }

    public ArrayList<Item> getItemsSoldOnCredit(int transactionId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("select transaction.*,  receipt.*, product.* from " + databaseName + ".transaction  " +
                        "left join " + databaseName + ".receipt on transaction.receiptId = receipt.receiptId " +
                        "left join " + databaseName + ".product on transaction.productId = product.productId where " +
                        "transaction.receiptid = ?;");

        preparedStatement.setInt(1, transactionId);

        resultSet = preparedStatement.executeQuery();
        //TODO: may need to create a suitable object that extends item.
        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            item.setProductId(resultSet.getInt("productId"));
            item.setReceiptId(resultSet.getInt("receiptId"));
            item.setTotalPrice(resultSet.getDouble("total"));
            item.setMargin(resultSet.getDouble("margin"));
            item.setSellername(resultSet.getString("username"));
            item.setTransactionId(resultSet.getInt("transactionid"));
            item.setUnits(resultSet.getString("units"));

            Timestamp timestamp = resultSet.getTimestamp("lastModifiedDate");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }

        close();
        return items;
    }

    //TODO: this method will be replaced with getInvoiceItems(int invoiceId)
    public ArrayList<Item> getItemsSold(int transactionId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("select transaction.*,  receipt.*, product.* from " + databaseName + ".transaction  " +
                        "left join " + databaseName + ".receipt on transaction.receiptId = receipt.receiptId " +
                        "left join " + databaseName + ".product on transaction.productId = product.productId where " +
                        "transaction.receiptid = ?;");

        preparedStatement.setInt(1, transactionId);

        resultSet = preparedStatement.executeQuery();
        //TODO: may need to create a suitable object that extends item.
        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            item.setUnits(resultSet.getString("units"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            item.setProductId(resultSet.getInt("productId"));
            item.setReceiptId(resultSet.getInt("receiptId"));
            item.setTotalPrice(resultSet.getDouble("total"));
            item.setMargin(resultSet.getDouble("margin"));
            item.setSellername(resultSet.getString("username"));
            item.setTransactionId(resultSet.getInt("transactionid"));

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
                .prepareStatement("select product.productname, sum(item.total) as sum, sum(item.quantity) " +
                        "as productcount, sum(item.margin) as margin from " + databaseName + ".item left join " + databaseName + ".product on " +
                        "item.productid = product.productid " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "WHERE invoice.date_created > ? AND invoice.date_created < ? GROUP BY product.productname;");


        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            //item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("productcount"));
            //item.setProductId(resultSet.getInt("productId"));
            item.setTotalPrice(resultSet.getDouble("sum"));
            item.setMargin(resultSet.getDouble("margin"));
            items.add(item);
        }

        close();
        return items;
    }

    //Get the sales revenue for as specific for the period under consideration
    public ArrayList<Item> getItemsSoldSummary(String startDate, String endDate, int productId) throws SQLException, ClassNotFoundException {

        dbConnect();
        preparedStatement = connect
                .prepareStatement("select product.productname, sum(item.total) as sum, sum(item.quantity) " +
                        "as productcount, sum(item.margin) as margin from " + databaseName + ".item left join " + databaseName + ".product on " +
                        "item.productid = product.productid " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "WHERE product.productid = ? AND invoice.date_created > ? AND invoice.date_created < ? GROUP BY product.productname;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setInt(1, productId);
        preparedStatement.setTimestamp(2, startTimestamp);
        preparedStatement.setTimestamp(3, endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productName"));
            //item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("productcount"));
            //item.setProductId(resultSet.getInt("productId"));
            item.setTotalPrice(resultSet.getDouble("sum"));
            item.setMargin(resultSet.getDouble("margin"));
            items.add(item);
        }

        close();
        return items;
    }





    public Double getTotalRevenue(String startDate, String endDate) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("SELECT sum(total) AS totalrevenue  FROM " + databaseName + ".item " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id WHERE " +
                        "invoice.date_created > ? AND invoice.date_created < ?;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);

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
                .prepareStatement("select invoice.date_created::timestamp::date, sum(item.total) as sum, " +
                        "sum(item.quantity) as productquantity from " + databaseName + ".item " +
                        "left join " + databaseName + ".product on item.productid = product.productid " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "WHERE invoice.date_created > ? AND invoice.date_created < ? " +
                        "GROUP BY invoice.date_created::timestamp::date ORDER BY invoice.date_created::timestamp::date ASC;");

        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Hashtable<String, Object>> sales = new ArrayList<>();

        while (resultSet.next()) {
            Hashtable<String, Object> sale = new Hashtable<String, Object>();
            sale.put("date", resultSet.getDate("date_created"));
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
                .prepareStatement("select invoice.date_created::timestamp::date, sum(item.total) as sum, " +
                        "sum(item.quantity) as productquantity from " + databaseName + ".item " +
                        "left join " + databaseName + ".product on item.productid = product.productid " +
                        "left join " + databaseName + ".invoice on item.invoice_id = invoice.id " +
                        "WHERE invoice.date_created > ? AND invoice.date_created < ? AND item.productid=? " +
                        "GROUP BY invoice.date_created::timestamp::date ORDER BY invoice.date_created::timestamp::date ASC ;");



        //TODO: remove hard coded values
        Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
        Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

        preparedStatement.setTimestamp(1, startTimestamp);
        preparedStatement.setTimestamp(2, endTimestamp);
        preparedStatement.setInt(3, productid);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Hashtable<String, Object>> sales = new ArrayList<>();

        while (resultSet.next()) {
            Hashtable<String, Object> sale = new Hashtable<String, Object>();
            sale.put("date", resultSet.getDate("date_created"));
            sale.put("sum", resultSet.getDouble("sum"));
            sale.put("productquantity", resultSet.getInt("productquantity"));

            sales.add(sale);
        }

        close();
        return sales;
    }

    //Get a product with the given bardode
    public Product getProduct(String barcode) throws SQLException, ClassNotFoundException {
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
        product.setCostprice(resultSet.getDouble("costprice"));
        product.setPrice(resultSet.getDouble("price"));
        product.setUnits(resultSet.getString("units"));
        product.setStockLowThreshold(resultSet.getInt("stock_low_threshold"));
        product.setBarcode(resultSet.getString("barcode"));

        Timestamp timestamp = resultSet.getTimestamp("dateCreated");
        String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        product.setDateCreated(time_created);

        timestamp = resultSet.getTimestamp("lastModifiedDate");
        String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        product.setLastModifiedDate(time_modified);

        product.setComment(resultSet.getString("comments"));

        return product;
    }

    //Get a product with the given Id
    public Product getProduct(int productId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".product " +
                "where product.productid=?;");

        preparedStatement.setInt(1, productId);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        int categoryid = resultSet.getInt("categoryid");

        Product product = new Product();
        product.setProductId(resultSet.getInt("productId"));
        product.setProductName(resultSet.getString("ProductName"));
        product.setDescription(resultSet.getString("description"));
        product.setCostprice(resultSet.getDouble("costprice"));
        product.setPrice(resultSet.getDouble("price"));
        product.setUnits(resultSet.getString("units"));
        product.setStockLowThreshold(resultSet.getDouble("stock_low_threshold"));
        product.setBarcode(resultSet.getString("barcode"));

        Timestamp timestamp = resultSet.getTimestamp("dateCreated");
        String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        product.setDateCreated(time_created);

        timestamp = resultSet.getTimestamp("lastModifiedDate");
        String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        product.setLastModifiedDate(time_modified);

        product.setComment(resultSet.getString("comments"));

        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".category " +
                "where category.categoryid=?;");

        preparedStatement.setInt(1, categoryid);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Category category = new Category();
        category.setCategoryId(categoryid);
        category.setCategoryName(resultSet.getString("categoryname"));
        category.setDescription(resultSet.getString("description"));

        product.setCategory(category);

        return product;
    }

    //Get a category with a given Id
    public Category getCategory(int categoryId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".category " +
                "where category.categoryid=?;");

        preparedStatement.setInt(1, categoryId);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Category category = new Category();
        category.setCategoryId(resultSet.getInt("categoryid"));
        category.setCategoryName(resultSet.getString("categoryname"));
        category.setDescription(resultSet.getString("description"));

        return category;
    }

    //Get a Unit with a given unitName
    public Unit getUnit(String unitName) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".units " +
                "where units.unit=?;");

        preparedStatement.setString(1, unitName);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Unit unit = new Unit();
        unit.setUnitName(unitName);
        unit.setUnitId(resultSet.getInt("unitid"));

        return unit;
    }

    public void updateProduct(Product product) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".product set categoryid = ?," +
                            " productname = ?, description = ?, price = ?, barcode = ?," +
                            " lastmodifieddate = ?, comments = ?, units = ?, stock_low_threshold = ?, " +
                            " costprice = ? where productid = ?;");

            preparedStatement.setInt(1, product.getCategory().getCategoryId());
            preparedStatement.setString(2, product.getProductName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setDouble(4, product.getPrice());
            preparedStatement.setString(5, product.getBarcode());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, product.getComment());
            preparedStatement.setString(8, product.getUnits());
            preparedStatement.setDouble(9, product.getStockLowThreshold());
            preparedStatement.setDouble(10, product.getCostprice());
            preparedStatement.setInt(11, product.getProductId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public void updateCategory(Category category) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".category set categoryname = ?," +
                            " description = ? where categoryid = ?;");
            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());
            preparedStatement.setInt(3, category.getCategoryId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public int deleteCategory(int categoryId) throws Exception {

        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".category WHERE categoryid = ? ;");
            preparedStatement.setInt(1, categoryId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public void addSupplier(Supplier supplier) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".supplier " +
                            "(id, supplier_name, phone1, phone2, email, address, bank_name, account_number)values " +
                            "(default,?,?, ?, ? , ?, ?,?);");

            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getPhone1());
            preparedStatement.setString(3, supplier.getPhone2());
            preparedStatement.setString(4, supplier.getEmail());
            preparedStatement.setString(5, supplier.getAddress());
            preparedStatement.setString(6, supplier.getBankName());
            preparedStatement.setString(7, supplier.getAccountNumber());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }


    public ArrayList<Supplier> getSuppliers() throws Exception {
        ArrayList<Supplier> suppliers = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT * FROM  " + databaseName + ".supplier ORDER BY created DESC;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Supplier supplier = new Supplier();
                supplier.setSupplierName(resultSet.getString("supplier_name"));
                supplier.setId(resultSet.getInt("id"));
                supplier.setPhone1(resultSet.getString("phone1"));
                supplier.setPhone2(resultSet.getString("phone2"));
                supplier.setEmail(resultSet.getString("email"));
                supplier.setAddress(resultSet.getString("address"));
                supplier.setBankName(resultSet.getString("bank_name"));
                supplier.setAccountNumber(resultSet.getString("account_number"));

                Timestamp timestamp = resultSet.getTimestamp("created");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplier.setDateCreated(time_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplier.setDateModified(time_modified);

                suppliers.add(supplier);
            }

            return suppliers;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }

    public ArrayList<Item> getSuppliedItems() throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("select delivery.*, stransactions.transaction_type, supplier.supplier_name, product.productname from delivery " +
                        "left join " + databaseName + ".stransactions on stransactions.id = delivery.transaction_id " +
                        "left join " + databaseName + ".supplier on supplier.id = stransactions.supplier_id " +
                        "left join " + databaseName + ".product on product.productid = delivery.product_id " +
                        "ORDER BY date_supplied DESC;");

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productname"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            //item.setProductId(resultSet.getInt("productid"));
            item.setInvoiceNumber(resultSet.getInt("supplier_invoice"));

            item.setTotalPrice(resultSet.getDouble("total"));
            item.setSellername(resultSet.getString("supplier_name"));
            item.setTransactionId(resultSet.getInt("id")); //deliveryId

            Timestamp timestamp = resultSet.getTimestamp("date_supplied");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }
        close();
        return items;

    }

    public ArrayList<Item> getSuppliedItems(int transactionId) throws SQLException, ClassNotFoundException {
        dbConnect();
        preparedStatement = connect
                .prepareStatement("select delivery.*, stransactions.transaction_type, supplier.supplier_name, product.productname, product.units from delivery " +
                        "left join " + databaseName + ".stransactions on stransactions.id = delivery.transaction_id " +
                        "left join " + databaseName + ".supplier on supplier.id = stransactions.supplier_id " +
                        "left join " + databaseName + ".product on product.productid = delivery.product_id " +
                        "WHERE delivery.transaction_id=? ORDER BY date_supplied DESC ;");
        preparedStatement.setInt(1, transactionId);

        resultSet = preparedStatement.executeQuery();

        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productname"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            //item.setProductId(resultSet.getInt("productid"));
            item.setInvoiceNumber(resultSet.getInt("supplier_invoice"));
            item.setUnits(resultSet.getString("units"));

            item.setTotalPrice(resultSet.getDouble("total"));
            item.setSellername(resultSet.getString("supplier_name"));
            item.setTransactionId(resultSet.getInt("id")); //deliveryId

            Timestamp timestamp = resultSet.getTimestamp("date_supplied");
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
            item.setTime(time);

            items.add(item);
        }
        close();
        return items;

    }

    public void submitPayment(Payment payment) throws Exception {

        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".stransactions " +
                            "(supplier_id,description, amount, transaction_type)values " +
                            "(?,?, ?, ? );");

            preparedStatement.setInt(1, payment.getSupplier_id());
            preparedStatement.setString(2, payment.getDescription());
            preparedStatement.setDouble(3, payment.getAmount());
            preparedStatement.setString(4, payment.getPaymentType());

            preparedStatement.executeUpdate();

//            preparedStatement = connect
//                    .prepareStatement("update " + databaseName + ".delivery " +
//                            "set cash_received = ? where id = ? " +
//                            "(?);");
//
//            preparedStatement.setInt(1, payment.getSupplier_id());
//            preparedStatement.setString(2, payment.getDescription());
//            preparedStatement.setDouble(3, payment.getAmount());
//            preparedStatement.setString(4, payment.getPaymentType());
//
//            preparedStatement.executeUpdate();


        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public int deleteSales(String startDate, String endDate, int productId) throws Exception {
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".transaction " +
                            "WHERE productid = ? AND receiptid IN (select receiptid from receipt where lastModifiedDate > ? AND lastModifiedDate < ?) ;");


            //TODO REMOVE HARD CODED TIME VALUES
            Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
            Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");

            preparedStatement.setInt(1, productId);
            preparedStatement.setTimestamp(2, startTimestamp);
            preparedStatement.setTimestamp(3, endTimestamp);

            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }


    public void deleteSales(int[] itemIds) throws Exception {
        try {
            dbConnect();
            for (int i = 0; i < itemIds.length; i++) {
                preparedStatement = connect
                        .prepareStatement("DELETE FROM " + databaseName + ".item " +
                                "WHERE id = ?;");

                preparedStatement.setInt(1, itemIds[i]);

                preparedStatement.executeUpdate();
            }

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public void updateStock(int i, int productId, int quantity, String comment, int transactionId) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".stock set userid = ?, quantity = ?, comments = ?" +
                            " where transactionid = ?;");
            preparedStatement.setInt(1, 1);
            preparedStatement.setInt(2, quantity);
            preparedStatement.setString(3, comment);
            preparedStatement.setInt(4, transactionId);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }

    }

    public void updateDeliveredQuantity(double quantity, int delivery_id) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".delivery set quantity = ? " +
                            "where id = ?;");
            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, delivery_id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }


    public void updateDeliveredPrice(double price, int delivery_id) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".delivery set price = ? " +
                            "where id = ?;");
            preparedStatement.setDouble(1, price);
            preparedStatement.setInt(2, delivery_id);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<Customer> getCustomers() throws Exception {
        ArrayList<Customer> customers = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT * FROM  " + databaseName + ".customer;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Customer customer = new Customer();
                customer.setFirstname(resultSet.getString("firstname"));
                customer.setLastname(resultSet.getString("lastname"));
                customer.setId(resultSet.getInt("id"));
                customer.setSex(resultSet.getString("sex"));
                customer.setBarcode(resultSet.getString("barcode"));
                customer.setBirthday(resultSet.getString("birthday"));
                customer.setPhone1(resultSet.getString("phone1"));
                customer.setPhone2(resultSet.getString("phone2"));
                customer.setEmail(resultSet.getString("email"));
                customer.setAddress(resultSet.getString("address"));

                Timestamp timestamp = resultSet.getTimestamp("date_supplied");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customer.setDateCreated(time_created);
                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customer.setDateModified(time_modified);

                customers.add(customer);
            }

            return customers;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    // only registered customers
    public ArrayList<CustomerTransaction> getCustomerTransactions() throws Exception {
        ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
        try {
            dbConnect();
            //TODO: RENAME table receipt to transctions since it contains cash, credit and  payment transactions
            //TODO: RENAME transaction table to transaction details table and rename receiptid field to transactionid and primary key to id
            preparedStatement = connect
                    .prepareStatement("SELECT receipt.*, customer.* FROM  " + databaseName + ".receipt " +
                            "left join " + databaseName + ".customer on customer.id = receipt.customer_id " +
                            "where transaction_type in ('credit', 'pay');");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CustomerTransaction customerTransaction = new CustomerTransaction();
                customerTransaction.setCustomerId(resultSet.getInt("customer_id"));
                customerTransaction.setBalance(resultSet.getDouble("balance"));
                customerTransaction.setReceiptId(resultSet.getInt("receiptid"));
                customerTransaction.setUserName(resultSet.getString("username"));
                customerTransaction.setAmount(resultSet.getDouble("amount"));
                customerTransaction.setFirstName(resultSet.getString("firstname"));
                customerTransaction.setLastName(resultSet.getString("lastname"));
                customerTransaction.setTransaction_type(resultSet.getString("transaction_type"));

                //TODO: rename lastmodifieddate in reciept table to date created
                Timestamp timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_created(time_created);
                timestamp = resultSet.getTimestamp("date_updated");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_modified(time_modified);

                customerTransactions.add(customerTransaction);
            }

            return customerTransactions;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public ArrayList<CustomerTransaction> getAllCustomerTransactions() throws Exception {
        ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
        try {
            dbConnect();
            //TODO: RENAME table receipt to transctions since it contains cash, credit and  payment transactions
            //TODO: RENAME transaction table to transaction details table and rename receiptid field to transactionid and primary key to id
            preparedStatement = connect
                    .prepareStatement("SELECT receipt.* FROM  " + databaseName + ".receipt " +
                            "where transaction_type in ('credit', 'cash') ORDER BY lastmodifieddate DESC;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CustomerTransaction customerTransaction = new CustomerTransaction();
                //customerTransaction.setCustomerId(resultSet.getInt("customer_id"));
                //customerTransaction.setBalance(resultSet.getDouble("balance"));
                customerTransaction.setReceiptId(resultSet.getInt("receiptid"));
                customerTransaction.setUserName(resultSet.getString("username"));
                customerTransaction.setAmount(resultSet.getDouble("amount"));
                //customerTransaction.setFirstName(resultSet.getString("firstname"));
                //customerTransaction.setLastName(resultSet.getString("lastname"));
                //customerTransaction.setTransaction_type(resultSet.getString("transaction_type"));
                customerTransaction.setCashReceived(resultSet.getDouble("cash_received"));
                customerTransaction.setChange(resultSet.getDouble("change"));

                //TODO: rename lastmodifieddate in reciept table to date created
                Timestamp timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_created(time_created);
                timestamp = resultSet.getTimestamp("date_updated");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_modified(time_modified);

                customerTransactions.add(customerTransaction);
            }

            return customerTransactions;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }


    public Customer getCustomer(int customerId) throws ClassNotFoundException, SQLException {

        dbConnect();
        preparedStatement = connect.prepareStatement("select * from " + databaseName + ".customer " +
                "where customer.id=?;");

        preparedStatement.setInt(1, customerId);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setFirstname(resultSet.getString("firstname"));
        customer.setLastname(resultSet.getString("lastname"));
        customer.setSex(resultSet.getString("sex"));
        customer.setPhone1(resultSet.getString("phone1"));
        customer.setPhone2(resultSet.getString("phone2"));
        customer.setAddress(resultSet.getString("address"));
        customer.setEmail(resultSet.getString("email"));
        customer.setBirthday(resultSet.getString("birthday"));
        customer.setBarcode(resultSet.getString("barcode"));

        close();
        return customer;

    }

    public Double getCustomerBalance(int customerId) throws ClassNotFoundException, SQLException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select balance from " + databaseName + ".customer_statement " +
                "where customer_id=? order by date_entered desc limit 1;");

        preparedStatement.setInt(1, customerId);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Double balance = resultSet.getDouble("balance");
        close();
        return balance;
    }

    public void submitCustomerPayment(String userName, CustomerPayment customerPayment) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".receipt " +
                            "(username,transaction_type, amount, customer_id)values " +
                            "(?,?, ?,? );");

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, "pay");
            preparedStatement.setDouble(3, customerPayment.getAmount());
            preparedStatement.setInt(4, customerPayment.getCustomerId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<Distributor> getDistributors() throws Exception {
        ArrayList<Distributor> distributors = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT distribution.* , product.productName, product.stock_low_threshold FROM " +
                            databaseName + ".distribution " +
                            "LEFT JOIN " + databaseName + ".product ON distribution.product_id = product.productid ORDER " +
                            "BY lastmodifieddate DESC;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int productId = resultSet.getInt("product_id");
                Distributor distributor = new Distributor(productId,serverIp);
                distributor.setProductName(resultSet.getString("productname"));
                distributor.setTotalQuantityInStock(resultSet.getDouble("quantity"));
                distributor.setStockLowThreshold(resultSet.getDouble("stock_low_threshold"));

                Timestamp timestamp = resultSet.getTimestamp("date_added");
                String date_added = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                distributor.setDate_time_created(date_added);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                distributor.setDate_time_modified(date_modified);

                distributors.add(distributor);
            }

            return distributors;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }



    public void updateDistribution(String source, String dest, double source_qty, double dest_qty, int productId)
            throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".distribution set " + source + "=?, " + dest + "=? " +
                            "where product_id = ?;");

            preparedStatement.setDouble(1, source_qty);
            preparedStatement.setDouble(2, dest_qty);
            preparedStatement.setInt(3, productId);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<CustomerTransaction> getCustomerTransactions(int customerId) throws Exception {
        ArrayList<CustomerTransaction> customerTransactions = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT customer_statement.*, customer.*, statement_invoice.invoice_id FROM  " + databaseName + ".customer_statement " +
                            "left join " + databaseName + ".customer on customer.id = customer_statement.customer_id " +
                            "left join " + databaseName + ".statement_invoice on customer_statement.id = statement_invoice.statement_id " +
                            "where customer_statement.customer_id = ? order by customer_statement.date_entered asc;");
            preparedStatement.setInt(1, customerId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CustomerTransaction customerTransaction = new CustomerTransaction();
                customerTransaction.setCustomerId(resultSet.getInt("customer_id"));
                customerTransaction.setBalance(resultSet.getDouble("balance"));
                customerTransaction.setReceiptId(resultSet.getInt("invoice_id")); //TODO: should be renamed as it is the invoice_id
                //customerTransaction.setUserName(resultSet.getString("username"));
                customerTransaction.setAmount(resultSet.getDouble("amount"));
                customerTransaction.setFirstName(resultSet.getString("firstname"));
                customerTransaction.setLastName(resultSet.getString("lastname"));
                customerTransaction.setTransaction_type(resultSet.getString("type"));

                //TODO: rename lastmodifieddate in reciept table to date created
                Timestamp timestamp = resultSet.getTimestamp("date_entered");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_created(time_created);
                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerTransaction.setDate_modified(time_modified);

                customerTransactions.add(customerTransaction);
            }

            return customerTransactions;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public void addCustomer(Customer customer) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".customer " +
                            "(firstname, lastname, sex, birthday, phone1, phone2, email, address) " +
                            "values (?,?, ?, ? , ?, ?,?,?);");

            preparedStatement.setString(1, customer.getFirstname());
            preparedStatement.setString(2, customer.getLastname());
            preparedStatement.setString(3, customer.getSex());
            preparedStatement.setString(4, customer.getBirthday());
            //preparedStatement.setString(5, customer.getBarcode());
            preparedStatement.setString(5, customer.getPhone1());
            preparedStatement.setString(6, customer.getPhone2());
            preparedStatement.setString(7, customer.getEmail());
            preparedStatement.setString(8, customer.getAddress());

            preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public double getSupplierBalance(int supplier_id) throws ClassNotFoundException, SQLException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select balance from " + databaseName + ".stransactions " +
                "where supplier_id=? order by date_entered desc limit 1;");

        preparedStatement.setInt(1, supplier_id);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Double balance = resultSet.getDouble("balance");
        close();
        return balance;
    }

    public ArrayList<SupplierTransaction> getSupplierTransactions() throws Exception {
        ArrayList<SupplierTransaction> supplierTransactions = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT stransactions.*, supplier.* FROM  " + databaseName + ".stransactions " +
                            "left join " + databaseName + ".supplier on supplier.id = stransactions.supplier_id ORDER BY stransactions.date_entered DESC ");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SupplierTransaction supplierTransaction = new SupplierTransaction();
                supplierTransaction.setId(resultSet.getInt("id"));
                supplierTransaction.setBalance(resultSet.getDouble("balance"));
                supplierTransaction.setDescription(resultSet.getString("description"));
                supplierTransaction.setSupplierId(resultSet.getInt("supplier_id"));
                supplierTransaction.setSupplierName(resultSet.getString("supplier_name"));
                supplierTransaction.setAmount(resultSet.getDouble("amount"));
                supplierTransaction.setTransaction_type(resultSet.getString("transaction_type"));

                Timestamp timestamp = resultSet.getTimestamp("date_entered");
                String date_entered = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplierTransaction.setDate_created(date_entered);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String lastmodifieddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplierTransaction.setDate_created(lastmodifieddate);

                supplierTransactions.add(supplierTransaction);
            }

            return supplierTransactions;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }


    public ArrayList<SupplierTransaction> getSupplierTransactions(int supplierId, String startDate, String endDate) throws Exception {
        ArrayList<SupplierTransaction> supplierTransactions = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT stransactions.*, supplier.* FROM  " + databaseName + ".stransactions " +
                            "left join " + databaseName + ".supplier on supplier.id = stransactions.supplier_id " +
                            "WHERE supplier_id = ? AND (stransactions.lastmodifieddate >= ? AND stransactions.lastmodifieddate <=?) ORDER BY stransactions.lastmodifieddate DESC ");
            preparedStatement.setInt(1, supplierId);

            //TODO: remove hard coded values
            Timestamp startTimestamp = Timestamp.valueOf(startDate + " 00:00:00");
            Timestamp endTimestamp = Timestamp.valueOf(endDate + " 23:59:59");


            preparedStatement.setTimestamp(2, startTimestamp);
            preparedStatement.setTimestamp(3, endTimestamp);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                SupplierTransaction supplierTransaction = new SupplierTransaction();
                supplierTransaction.setId(resultSet.getInt("id"));
                supplierTransaction.setBalance(resultSet.getDouble("balance"));
                supplierTransaction.setDescription(resultSet.getString("description"));
                supplierTransaction.setSupplierId(resultSet.getInt("supplier_id"));
                supplierTransaction.setSupplierName(resultSet.getString("supplier_name"));
                supplierTransaction.setAmount(resultSet.getDouble("amount"));
                supplierTransaction.setTransaction_type(resultSet.getString("transaction_type"));

                Timestamp timestamp = resultSet.getTimestamp("date_entered");
                String date_entered = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplierTransaction.setDate_created(date_entered);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String lastmodifieddate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                supplierTransaction.setDate_created(lastmodifieddate);

                supplierTransactions.add(supplierTransaction);
            }

            return supplierTransactions;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public void updateStockStatus(int productId, int new_pack_qty) throws Exception {
        //load the mysql driver
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".stockstatus set quantity = ? " +
                            " where productid = ?;");
            preparedStatement.setInt(1, new_pack_qty);
            preparedStatement.setInt(2, productId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public int generateReceipt(Receipt receipt) throws Exception  {
        int receipt_id = 0;
        try {
            //load the mysql driver
            dbConnect();
            String sql = "insert into  " + databaseName + ".receipt " + //for a registered customer
                    "(cash_received, change, balance, invoice_id,  rec_type, customer_id, description,tillnumber, username) " +
                    "values (?, ?, ?, ?,?, ?, ?, ?,?)";

            String sql2 = "insert into  " + databaseName + ".receipt " +
                    "(cash_received, change, balance, invoice_id,  rec_type, description, tillnumber, username) " +
                    "values (?, ?, ?, ?,?, ?, ?,?)";

            if(receipt.getCustomerId() !=0) {//for a registered customer
                preparedStatement = connect
                        .prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setDouble(1, receipt.getCashReceived());
                preparedStatement.setDouble(2, receipt.getChange());
                preparedStatement.setDouble(3, receipt.getBalance());
                preparedStatement.setInt(4, receipt.getInvoice_id());
                preparedStatement.setString(5, receipt.getReceiptType().toString());
                preparedStatement.setInt(6, receipt.getCustomerId());
                preparedStatement.setString(7, receipt.getDescription());
                preparedStatement.setInt(8, receipt.getTillnumber());
                preparedStatement.setString(9, receipt.getCashierName());
            }else {//for a non-registered customer

                preparedStatement = connect
                        .prepareStatement(sql2, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setDouble(1, receipt.getCashReceived());
                preparedStatement.setDouble(2, receipt.getChange());
                preparedStatement.setDouble(3, receipt.getBalance());
                preparedStatement.setInt(4, receipt.getInvoice_id());
                preparedStatement.setString(5, receipt.getReceiptType().toString());
                preparedStatement.setString(6, receipt.getDescription());
                preparedStatement.setInt(7, receipt.getTillnumber());
                preparedStatement.setString(8, receipt.getCashierName());
            }


            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.next();
            receipt_id = resultSet.getInt(1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            close();
            return receipt_id;
        }
    }


    //get both partial and unpaid invoices
    public ArrayList<Invoice> getUnpaidInvoices(int customerId) throws Exception {
        ArrayList<Invoice> invoices = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT invoice.*, customer.* FROM  " + databaseName + ".invoice left join " +
                            databaseName + ".customer on customer.id = invoice.customer_id where invoice.status != 'paid' " +
                            "and invoice.customer_id=?  order by date_created desc;");
            //preparedStatement.setString(1,status);
            preparedStatement.setInt(1,customerId);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Invoice invoice = new Invoice();
                int invoiceId = resultSet.getInt("id");
                invoice.setId(invoiceId);
                invoice.setAmount(resultSet.getDouble("amount"));
                invoice.setFirstName(resultSet.getString("firstname"));
                invoice.setFirstName(resultSet.getString("lastname"));
                invoice.setStatus(resultSet.getString("status"));
                invoice.setUsername(resultSet.getString("username"));

                Timestamp timestamp = resultSet.getTimestamp("date_created");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                invoice.setDate_created(time_created);
                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                invoice.setDateUpdated(time_modified);

                ArrayList<Item> items = getInvoiceItems(invoiceId);
                invoice.addAllItems(items);
                invoices.add(invoice);
            }

            return invoices;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public ArrayList<Item> getInvoiceItems(int invoiceId) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");

        //set up db connection
        Connection connect = DriverManager
                .getConnection(databaseUrl, "postgres", "Dreamalive1");

        PreparedStatement preparedStatement = connect
                .prepareStatement("select item.*, product.* from " + databaseName + ".item  " +
                        "left join " + databaseName + ".product on item.productid = product.productid where " +
                        "item.invoice_id = ?;");

        preparedStatement.setInt(1, invoiceId);

        ResultSet resultSet  = preparedStatement.executeQuery();
        ArrayList<Item> items = new ArrayList<>();

        while (resultSet.next()) {
            Item item = new Item();
            item.setProductName(resultSet.getString("productname"));
            item.setUnits(resultSet.getString("units"));
            item.setPrice(resultSet.getDouble("price"));
            item.setQuantity(resultSet.getDouble("quantity"));
            item.setProductId(resultSet.getInt("productid"));
            item.setInvoiceNumber(resultSet.getInt("invoice_id"));
            item.setTotalPrice(resultSet.getDouble("total"));
            item.setMargin(resultSet.getDouble("margin"));
            items.add(item);
        }

        resultSet.close();
        connect.close();
        return items;
    }

    public ArrayList<Integer> getTillNumbers() throws Exception {
        ArrayList<Integer> tills = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT distinct(tillnumber) FROM  " + databaseName + ".invoice " +
                            "order by tillnumber asc;");
            //preparedStatement.setString(1,status);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                tills.add(resultSet.getInt("tillnumber"));
            }

            return tills;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public void updateInvoiceStatus(int invoiceId, InvoiceStatus invoiceStatus) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".invoice set status = ? " +
                            " where id = ?;");
            preparedStatement.setString(1, invoiceStatus.toString());
            preparedStatement.setInt(2, invoiceId);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }


    public Double getInvoiceBalance(int invoiceId) throws ClassNotFoundException, SQLException {
        dbConnect();
        preparedStatement = connect.prepareStatement("select balance from " + databaseName + ".receipt " +
                "where invoice_id=? order by date_created desc limit 1;");

        preparedStatement.setInt(1, invoiceId);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();

        Double balance = resultSet.getDouble("balance");
        close();
        return balance;
    }

    public Invoice getInvoice(int invoiceId) throws Exception {
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT invoice.*, customer.* FROM  " + databaseName + ".invoice left join " +
                            databaseName + ".customer on customer.id = invoice.customer_id where " +
                            "invoice.id = ?  order by date_created desc;");
            preparedStatement.setInt(1,invoiceId);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

                Invoice invoice = new Invoice();
                invoice.setId(invoiceId);
                invoice.setAmount(resultSet.getDouble("amount"));
                invoice.setFirstName(resultSet.getString("firstname"));
                invoice.setFirstName(resultSet.getString("lastname"));
                invoice.setStatus(resultSet.getString("status"));
                invoice.setUsername(resultSet.getString("username"));

                Timestamp timestamp = resultSet.getTimestamp("date_created");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                invoice.setDate_created(time_created);
                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                invoice.setDateUpdated(time_modified);

                ArrayList<Item> items = getInvoiceItems(invoiceId);
                invoice.addAllItems(items);

            return invoice;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public ArrayList<Receipt> getReceipts(Query query) throws Exception {
        ArrayList<Receipt> receipts = new ArrayList<>();
        try {
            dbConnect();

            String sql;
            if (query.getUsername()=="" && query.getTillnumber()==0){
                sql = "SELECT receipt.* FROM " +
                        databaseName + ".receipt WHERE receipt.date_created >? AND receipt.date_created <? " +
                        " ORDER BY date_created DESC;";
                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
            }else if(query.getTillnumber()!=0 && query.getUsername()=="" ){
                sql = "SELECT receipt.* FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        "tillnumber = ? " +
                        " ORDER BY date_created DESC;";
                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setInt(3, query.getTillnumber());
            }else if(query.getTillnumber()==0 && query.getUsername()!=""){
                sql = "SELECT receipt.* FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        "username = ? " +
                        " ORDER BY date_created DESC;";
                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setString(3, query.getUsername());
            }else {
                sql = "SELECT receipt.* FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        " tillnumber= ? AND username = ?" +
                        " ORDER BY date_created DESC;";

                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setInt(3, query.getTillnumber());
                preparedStatement.setString(4, query.getUsername());
            }


            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Receipt receipt = new Receipt();
                receipt.setReceiptId(resultSet.getInt("id"));
                receipt.setReceiptType(ReceiptType.valueOf(resultSet.getString("rec_type")));
                receipt.setChange(resultSet.getDouble("change"));
                receipt.setBalance(resultSet.getDouble("balance"));
                receipt.setCustomerId(resultSet.getInt("customer_id"));
                receipt.setCashReceived(resultSet.getDouble("cash_received"));
                receipt.setDescription(resultSet.getString("description"));
                receipt.setInvoice_id(resultSet.getInt("invoice_id"));
                receipt.setTillnumber(resultSet.getInt("tillnumber"));
                receipt.setCashierName(resultSet.getString("username"));

                Timestamp timestamp = resultSet.getTimestamp("date_created");
                String date_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                receipt.setDate_created(date_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                receipt.setDate_modified(date_modified);

                receipts.add(receipt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return receipts;
        }
    }

    public ArrayList<Receipt> getReceipts() throws Exception {
        ArrayList<Receipt> receipts = new ArrayList<>();
        try {
            dbConnect();

            String sql;
            sql = "SELECT receipt.* FROM " +
                        databaseName + ".receipt  " +
                        " ORDER BY date_created DESC limit 10000;";
                preparedStatement = connect
                        .prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Receipt receipt = new Receipt();
                receipt.setReceiptId(resultSet.getInt("id"));
                receipt.setReceiptType(ReceiptType.valueOf(resultSet.getString("rec_type")));
                receipt.setChange(resultSet.getDouble("change"));
                receipt.setBalance(resultSet.getDouble("balance"));
                receipt.setCustomerId(resultSet.getInt("customer_id"));
                receipt.setCashReceived(resultSet.getDouble("cash_received"));
                receipt.setDescription(resultSet.getString("description"));
                receipt.setInvoice_id(resultSet.getInt("invoice_id"));
                receipt.setTillnumber(resultSet.getInt("tillnumber"));
                receipt.setCashierName(resultSet.getString("username"));

                Timestamp timestamp = resultSet.getTimestamp("date_created");
                String date_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                receipt.setDate_created(date_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String date_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                receipt.setDate_modified(date_modified);

                receipts.add(receipt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return receipts;
        }
    }

    public double getCashReceived(Query query) throws Exception {
        double cash = 0.0;
        try {
            dbConnect();

            String sql;
            if (query.getUsername()=="" && query.getTillnumber()==0){
                sql = "SELECT (sum(cash_received) - sum(change)) as cash FROM " +
                        databaseName + ".receipt WHERE receipt.date_created >? AND receipt.date_created <? " ;

                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
            }else if(query.getTillnumber()!=0 && query.getUsername()=="" ){
                sql = "SELECT (sum(cash_received) - sum(change)) as cash FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        "tillnumber = ? ";
                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setInt(3, query.getTillnumber());
            }else if(query.getTillnumber()==0 && query.getUsername()!=""){
                sql = "SELECT (sum(cash_received) - sum(change)) as cash FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        "username = ? ";
                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setString(3, query.getUsername());
            }else {
                sql = "SELECT (sum(cash_received) - sum(change)) as cash FROM " +
                        databaseName + ".receipt WHERE (receipt.date_created >? AND receipt.date_created <?) AND " +
                        " tillnumber= ? AND username = ?" ;

                preparedStatement = connect
                        .prepareStatement(sql);
                Timestamp startTimestamp = Timestamp.valueOf(query.getStartDate() + " 00:00:00");
                Timestamp endTimestamp = Timestamp.valueOf(query.getEndDate() + " 23:59:59");
                preparedStatement.setTimestamp(1, startTimestamp);
                preparedStatement.setTimestamp(2, endTimestamp);
                preparedStatement.setInt(3, query.getTillnumber());
                preparedStatement.setString(4, query.getUsername());
            }

            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            cash = resultSet.getDouble("cash");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return cash;
        }
    }

    public ArrayList<CustomerStatement> getCustomerStatements() throws Exception{
        ArrayList<CustomerStatement> customerStatements = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT customer_statement.*, customer.* FROM  " + databaseName + ".customer_statement " +
                            "left join " + databaseName + ".customer on customer.id = customer_statement.customer_id " +
                            "where customer_id IS NOT NULL order by date_entered desc ;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                CustomerStatement customerStatement = new CustomerStatement();
                customerStatement.setId(resultSet.getInt("id"));
                customerStatement.setAmount(resultSet.getDouble("amount"));
                customerStatement.setBalance(resultSet.getDouble("balance"));
                customerStatement.setCustomerId(resultSet.getInt("customer_id"));
                customerStatement.setFirstname(resultSet.getString("firstname"));
                customerStatement.setLastname(resultSet.getString("lastname"));
                customerStatement.setType(resultSet.getString("type"));

                //TODO: rename lastmodifieddate in reciept table to date created
                Timestamp timestamp = resultSet.getTimestamp("date_entered");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerStatement.setDate_entered(time_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                customerStatement.setDate_modified(time_modified);

                customerStatements.add(customerStatement);
            }

            return customerStatements;
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }
    }

    public int getInvoiceId(int statement_id) throws Exception {
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT invoice_id FROM  " + databaseName + ".statement_invoice where statement_id = ?;");
            preparedStatement.setInt(1, statement_id);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt("invoice_id");
        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }

//    public double getQuantityInStock(int productId, Location location) throws Exception {
//
//        double balance = 0.0;
//        try {
//            dbConnect();
//            preparedStatement = connect
//                    .prepareStatement("SELECT balance FROM  " + databaseName + ".stock where productid = ? " +
//                            " and location = ? order by datecreated DESC limit 1;");
//            preparedStatement.setInt(1, productId);
//            preparedStatement.setString(2, location.toString());
//            resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            balance = resultSet.getDouble("balance");
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            close();
//            return  balance;
//        }
//    }

    public double getQuantityInStock(int productId, Site location) throws Exception {

        double balance = 0.0;
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT balance FROM  " + databaseName + ".stock where productid = ? " +
                            " and location_id = ? order by datecreated DESC limit 1;");
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, location.getId());
            resultSet = preparedStatement.executeQuery();
            resultSet.next();
            balance = resultSet.getDouble("balance");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return  balance;
        }
    }

    // deletes a customer and all related information namely: invoices, receipts, and statement info.
    public int deleteCustomer (int customerId) throws Exception {

        try {
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".customer WHERE id = ? ;");
            preparedStatement.setInt(1, customerId);
            return preparedStatement.executeUpdate();

        } catch (Exception e) {
            throw e;
        } finally {
            close();

        }

    }

    public void updateCustomer(Customer customer) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".customer set firstname = ?, lastname = ?, " +
                            "birthday = ?, phone1 = ?, phone2 = ?, email = ?, address = ?, sex=? where id = ?;");
            preparedStatement.setString(1, customer.getFirstname());
            preparedStatement.setString(2, customer.getLastname());
            preparedStatement.setString(3, customer.getBirthday());
            preparedStatement.setString(4, customer.getPhone1());
            preparedStatement.setString(5, customer.getPhone2());
            preparedStatement.setString(6, customer.getEmail());
            preparedStatement.setString(7, customer.getAddress());
            preparedStatement.setString(8, customer.getSex());
            preparedStatement.setInt(9, customer.getId());

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void updateDelivery(int deliveryId, Item item) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".delivery set quantity = ?, price = ?, " +
                            "supplier_invoice = ? where id = ?;");
            preparedStatement.setDouble(1, item.getQuantity());
            preparedStatement.setDouble(2, item.getPrice());
            preparedStatement.setInt(3, item.getInvoiceNumber());
            preparedStatement.setInt(4, deliveryId);

            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public int deleteSuppliedItem(int deliveryId) {
        int result = 0;
        try {
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".delivery WHERE id = ? ;");
            preparedStatement.setInt(1, deliveryId);
            result = preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return  result;
        }
    }

    public int deleteSupplierTransaction(int transactionId) {
        int result = 0;
        try {
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".stransactions WHERE id = ? ;");
            preparedStatement.setInt(1, transactionId);
            result = preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return  result;
        }
    }

    public int deleteSupplier(int supplierId) {
        int result = 0;
        try {
            dbConnect();

            preparedStatement = connect
                    .prepareStatement("DELETE FROM " + databaseName + ".supplier WHERE id = ? ;");
            preparedStatement.setInt(1, supplierId);
            result = preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return  result;
        }
    }

    public void updateSupplier(Supplier supplier) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".supplier set supplier_name = ?, " +
                            "phone1 = ?, phone2 = ?, email = ?, address = ?, account_number=?, bank_name=? where id = ?;");
            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getPhone1());
            preparedStatement.setString(3, supplier.getPhone2());
            preparedStatement.setString(4, supplier.getEmail());
            preparedStatement.setString(5, supplier.getAddress());
            preparedStatement.setString(6, supplier.getAccountNumber());
            preparedStatement.setString(7, supplier.getBankName());
            preparedStatement.setInt(8, supplier.getId());


            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT * FROM  " + databaseName + ".users order by date_entered desc ;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setUserName(resultSet.getString("username"));
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setAdmin(resultSet.getBoolean("isadmin"));

                Timestamp timestamp = resultSet.getTimestamp("date_entered");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                user.setDate_created(time_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                user.setDate_modified(time_modified);
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return users;
        }
    }

    public void refund(Refund refund) {
        //int transaction_id;
        try {
            //load the mysql driver
            dbConnect();
                preparedStatement = connect
                        .prepareStatement("insert into  " + databaseName + ".refund " +
                                "(id, amount, comment) " +
                                "values (?, ?, ?)");
                preparedStatement.setDouble(1, refund.getId());
                preparedStatement.setDouble(2, refund.getAmount());
                preparedStatement.setString(3, refund.getComment());
            preparedStatement.executeUpdate();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            close();
        }
    }

    public ArrayList<Refund> getRefunds() {
        ArrayList<Refund> refunds = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT * FROM  " + databaseName + ".refund " +
                            "order by date_paid desc ;");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Refund refund = new Refund();
                //refund.setProductName(resultSet.getString("productname"));
                refund.setId(resultSet.getInt("id"));
                refund.setAmount(resultSet.getDouble("amount"));
                refund.setComment(resultSet.getString("comment"));

                Timestamp timestamp = resultSet.getTimestamp("date_paid");
                String time_created = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                refund.setDatePaid(time_created);

                timestamp = resultSet.getTimestamp("lastmodifieddate");
                String time_modified = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
                refund.setLastModifiedDate(time_modified);
                refunds.add(refund);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return refunds;
        }
    }

    public void changePassword(String username, String password,byte[] salt ) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".users set password = ? , salt = ? " +
                            "where username = ?;");
            preparedStatement.setString(1, password);
            preparedStatement.setBytes(2, salt);
            preparedStatement.setString(3, username);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public void updateUser(User user) {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("update " + databaseName + ".users set firstname = ?, " +
                            "lastname = ?, isadmin = ? where username = ?;");
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setBoolean(3, user.isAdmin());
            preparedStatement.setString(4, user.getUserName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }



    public void addLocation(Site site) throws Exception {
        try {
            //load the mysql driver
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".location " +
                            "(id, location)values " +
                            "(default,?);");


            preparedStatement.setString(1, site.getName());
            preparedStatement.executeUpdate();

            preparedStatement = connect
                    .prepareStatement("insert into  " + databaseName + ".source_dest " +
                            "(id, source_dest)values " +
                            "(default,?);");
            preparedStatement.setString(1, site.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            close();
        }
    }

    public ArrayList<Site> getLocations() {
        ArrayList<Site> sites = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT *  FROM " + databaseName + ".location");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Site site = new Site();
                site.setId(resultSet.getInt("id"));
                site.setName(resultSet.getString("location"));
                sites.add(site);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return sites;

        }

    }

    public ArrayList<Site> getSource_dests() {
        ArrayList<Site> sites = new ArrayList<>();
        try {
            dbConnect();
            preparedStatement = connect
                    .prepareStatement("SELECT *  FROM " + databaseName + ".source_dest");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Site site = new Site();
                site.setId(resultSet.getInt("id"));
                site.setName(resultSet.getString("source_dest"));
                sites.add(site);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close();
            return sites;

        }

    }


    public void deleteLocation(Site location) throws Exception {
        try {
            //should not be able to delete receiving center and shop //TODO: need to rethink the implementation of this
            if(location.getId()!=1 && location.getId()!=2) {//locationids for receiving center and shop
                dbConnect();
                connect.setAutoCommit(false);
                preparedStatement = connect
                        .prepareStatement("DELETE FROM " + databaseName + ".location WHERE id = ? ;");

                preparedStatement2 = connect
                        .prepareStatement("DELETE FROM " + databaseName + ".source_dest WHERE id = ? ;");


                preparedStatement.setInt(1, location.getId());
                preparedStatement2.setInt(1, location.getId());

                preparedStatement.executeUpdate();
                preparedStatement2.executeUpdate();

                connect.commit();
            }

        } catch (Exception e) {
            throw e;
        } finally {
            connect.setAutoCommit(true);
            close();


        }
    }
}