package model;

import java.util.ArrayList;

//TODO : transaction is actually an invoice; consider how to merge invoice ,transaction, and customertransaction classes
public class Transaction {
    protected ArrayList<Item> items;
    protected String userName;  //of cashier
    protected int sellerId; //of supplier
    protected int receiptId; //should be changed to invoice_id
    protected  int recId; // this is the true receiptId
    protected double amount;
    protected String date_created;
    protected String date_modified;
    protected String transaction_type;


    public Transaction(String userName){

        items = new ArrayList<>();
        this.userName = userName;

    }

    public Transaction(int sellerId) {
        items = new ArrayList<>();
        this.sellerId = sellerId;
    }

    public Transaction() {
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public int getRecId() {
        return recId;
    }

    public void setRecId(int recId) {
        this.recId = recId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_modified() {
        return date_modified;
    }

    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void addItem(Item item){
        items.add(item);
    }

    public void addAllItems(ArrayList<Item> trItems){
        items = new ArrayList<>();
        items.addAll(trItems);
    }

    public boolean removeItem(Item item){
       return items.remove(item);
    }

    public double computeGrandTotal(){
        double totalPrice = 0.0;
        for(Item item : items){
          totalPrice +=  item.computeTotalPrice();
        }
        return  totalPrice;
    }

    public double computeGrandTotal2(){//TODO: fix error in the item.computeTotalPrice function
        double totalPrice = 0.0;
        for(Item item : items){
            totalPrice +=  item.getPrice()*item.getQuantity();
        }
        return  totalPrice;
    }
/*
    public void commitTransaction(){
        MySqlAccess mAccess = new MySqlAccess();
        try {
            mAccess.insertTransaction(items);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

//    private int generateReceiptId(){
//        MySqlAccess mAccess = new MySqlAccess();
//        int receiptId =0;
//        try{
//          receiptId =  mAccess.insertReceipt(userId);
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//        return receiptId;
//    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
