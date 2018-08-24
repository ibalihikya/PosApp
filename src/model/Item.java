package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

//This is an item being sold
public class Item  {

    private int productId;
    private String productName;
    private int quantity;
    private  double price;
    private double discount;
    private double totalPrice;
    private int receiptId;
    private String time; // time sold
    private static int itemCount = 0;

    public Item(){
        this.discount = 0.0;
        this.totalPrice = 0.0;
        this.quantity=0;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        time  = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        this.itemCount ++;
    }

    public Item(int productId, int quantity, double price ){
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.discount = 0.0;
        this.totalPrice = 0.0;
        this.itemCount ++;

    }

    public String getProductName() {
        return productName;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static void setItemCount(int itemCount) {
        Item.itemCount = itemCount;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public int getProductId() {
        return productId;
    }

    public static int getItemCount() {
        return itemCount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double computeTotalPrice(){
        this.totalPrice = quantity * price;
        return totalPrice;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

    @Override
    public String toString() {
//        return    String.format("%-15s %5d %10.2f %10.2f\n", Integer.toString(productId),
//                quantity, price, totalPrice);

      //  return    String.format("%20d %10.2f %10.2f\n",quantity, price, totalPrice);

        return    String.format("%25d %,10.0f %,10.0f\n",quantity, price, totalPrice);


    }

    // for detailed sales report
    public Object[] toArray() {
        Object[] item = {
                productName,
                quantity,
                price,
                totalPrice,
                receiptId,
                time
        };
        return item;
    }

    //for summary sales report
    public Object[] toArray_summary() {
        Object[] item = {
                productName,
                quantity,
                totalPrice
        };
        return item;
    }
}
