package Tests;

import authentication.SaltedMD5;
import model.*;
import model.databaseUtility.MySqlAccess;
import model.databaseUtility.SqlStrings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMySqlAccess {
    private MySqlAccess mAccess;
    private Product cement;
    private Transaction transaction1;
    private Transaction oneCementSaleTransaction;
    //ArrayList<Item> items = new ArrayList<>();
    private Item icement1;
    private Item icement2;
    private Category paintCategory;
    private Category pipeCategory;
    private Category cementCategory;
    private Unit kgUnit;
    private User user;

    @BeforeEach
    public void testSetup(){
        mAccess = new MySqlAccess(SqlStrings.TEST_DB_NAME);
        try {
            mAccess.createDatabase(SqlStrings.sqlCreateDbStatement);
            mAccess.createTable(SqlStrings.sqlCreateCategoryTable);
            mAccess.createTable(SqlStrings.sqlCreateProductTable);
            mAccess.createTable(SqlStrings.sqlCreateReceiptTable);
            mAccess.createTable(SqlStrings.sqlCreateStockTable);
            mAccess.createTable(SqlStrings.sqlCreateTransactionTable);
            mAccess.createTable(SqlStrings.sqlCreateUnitsTable);
            mAccess.createTable(SqlStrings.sqlCreateUsersTable);
            mAccess.createTable(SqlStrings.sqlCreateStockStatusTable);
            mAccess.createTable(SqlStrings.sqlCreateTriggerStockStatus_transaction);
            mAccess.createTable(SqlStrings.sqlCreateTriggerStockStatus_stock);
            mAccess.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try{
            byte[] salt = SaltedMD5.getSalt();
            String securePassword = SaltedMD5.getSecurePassword("password",salt);

            user = new User();
            user.setUserName("ibalihikya");
            user.setFirstName("Ivan");
            user.setLastName("Balihikya");
            user.setPassword(securePassword);
            user.setSalt(salt);
            user.setAdmin(true);

        }catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (NoSuchProviderException e1) {
            e1.printStackTrace();
        }



        paintCategory = new Category("Paints");
        paintCategory.setDescription("all paints");
        pipeCategory = new Category("Pipes");
        pipeCategory.setDescription("all pipes");
        cementCategory = new Category("cement");
        cementCategory.setCategoryId(1);

        cement = new Product("cement");
        cement.setDescription("just cement");
        cement.setPrice(30000);
        cement.setBarcode("YU786");
        cement.setComment("defining new cement product in system");
        cement.setUnits("Kg");
        cement.setCategory(cementCategory);


        kgUnit = new Unit("Kg");


        //TODO 10: Remove hardcoded user name
        transaction1 = new Transaction("ibalihikya");
        icement1 = new Item(1,2,30000);
        icement2 = new Item(2,5,30000);
        transaction1.addItem(icement1);
        transaction1.addItem(icement2);

        oneCementSaleTransaction = new Transaction("ibalihikya");
        oneCementSaleTransaction.addItem(icement1);

    }



    @AfterEach
    public void testTearDown(){

        try {
            mAccess.dropDatabase(SqlStrings.sqlDropDatabase);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void getNumberOfTables(){

    }


    @Test
    public void InsertProductTest(){
        //TODO 3: incomplete test
        try {
            mAccess.insertProduct(cement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void GetProductCountTest(){

        try {
            int productCount = mAccess.getProductCount();
            mAccess.insertProduct(cement);
            assertEquals(mAccess.getProductCount(), (productCount + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void RemoveProductTest(){
        try{
            //TODO 1: improve this test by working withough hardcoded productid(22); try using productid last inserted product
           // mAccess.insertProduct(cement);
          assertEquals(mAccess.removeProduct(22),1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /*
   @Test
    public void UpdateProductQuantityTest(){
        try{
            //TODO 2: remove hardcoded arguments
            assertEquals(mAccess.updateProductQuantity(6, 9),1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }*/

    @Test
    public void InsertTransactionTest(){
        try{
            //TODO 6: test incomplete; add assertions
            mAccess.insertTransaction(transaction1);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void createProductCategoryTest(){
        try{
            //TODO : incomplete test

            mAccess.createProductCategory(paintCategory);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getProductCategoriesTest(){
        try{
            //TODO : incomplete test
            mAccess.createProductCategory(paintCategory);
            assertEquals(mAccess.getProductCategories().size(),1);
            mAccess.createProductCategory(pipeCategory);
            assertEquals(mAccess.getProductCategories().size(),2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void addStockTest(){
        try{
            //TODO : incomplete test

            mAccess.addStock(1,2,5, "additional cement");
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    public void addUnitTest(){
        try{
            //TODO : incomplete test

            mAccess.addUnit(kgUnit);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getAllUnitsTest(){
        try{
            //TODO : incomplete test
            mAccess.addUnit(new Unit("Ltrs"));
            assertEquals(1, mAccess.getAllUnits().size());
            mAccess.addUnit(new Unit("metres"));
            assertEquals(2, mAccess.getAllUnits().size());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void registerUserTest(){
        try{
            assertEquals(1,mAccess.registerUser(user));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getUserTest(){
        try {
            mAccess.registerUser(user);
            assertTrue(user.equals(mAccess.getUser(user.getUserName())));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void getItemsSoldTest(){
        try {
            int receiptId = mAccess.generateReceiptId(transaction1.getUserName());
            transaction1.setReceiptId(receiptId);
            mAccess.insertTransaction(transaction1);

            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plus(1, ChronoUnit.DAYS);

            assertEquals(2, mAccess.getItemsSold(today.toString(),tomorrow.toString()).size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getStockStatusTest(){
        try {
            mAccess.insertProduct(cement); //define cement in product table
            mAccess.addStock(1,1,5,"none"); //stock 5 bags of cement
            assertEquals(1, mAccess.getStockStatus().size()); //number of stockitems in sockstatus table
            assertEquals(5, (mAccess.getStockStatus()).get(0).getQuantityStocked());
            mAccess.insertTransaction(oneCementSaleTransaction); //2 bags of cement sold
            assertEquals(3,(mAccess.getStockStatus()).get(0).getQuantityStocked());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getProductTest(){
        try{
            mAccess.getProduct("xyz");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


   /* @Test
    public void InsertReceiptTest(){
        try {
           assertTrue(mAccess.insertReceipt(1) >= 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

}
