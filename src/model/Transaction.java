package model;

import java.util.ArrayList;

public class Transaction {
    private ArrayList<Item> items;
    private String userName;  //of cashier
    private int receiptId;

    public Transaction(String userName){

        items = new ArrayList<>();
        this.userName = userName;

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
