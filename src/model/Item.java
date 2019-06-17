package model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

//This is an item being sold
public class Item  {

    private int productId;
    private String productName;
    private Double quantity;
    private  double price;
    private double discount;
    private double totalPrice;
    private int receiptId; // should remove this in favour of invoiceNumber below
    private String time; // time sold
    private static int itemCount = 0;
    private double margin;
    private String sellername; //username for logged in user or supplier name
    private int sellerid; //supplier
    private int invoiceNumber;
    private String units;
    private int transactionId; //TODO: rename this to id; this is the primary key in the item table
    private InvoiceStatus invoiceStatus;

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public double getMargin() {
        return margin;
    }

    public void setMargin(double margin) {
        this.margin = margin;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Item(){
        this.discount = 0.0;
        this.totalPrice = 0.0;
        this.quantity=0.0;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        time  = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        this.itemCount ++;
    }

    public String getSellername() {
        return sellername;
    }

    public void setSellername(String sellername) {
        this.sellername = sellername;
    }

    public Item(int productId, Double quantity, double price ){
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

    public Double getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
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

    public double computeTotalPrice(){//TODO: fix error in the item.computeTotalPrice function
        if(quantity<1){
            this.totalPrice =  price;
        }else {
            this.totalPrice = quantity * price;
        }
        //this.totalPrice = quantity * price;
        return totalPrice;
    }

    public double getTotalPrice(){
        return totalPrice;
    }

//    @Override
//    public String toString() {
//        return    String.format("%,-6.2f %-4s %,-7.0f %,10.0f\n", quantity, units, price, totalPrice);
//    }

    // for detailed sales report
    public Object[] toArray() {
        Object[] item = {
                productName,
                quantity,
                price,
                totalPrice,
                margin,
                invoiceNumber,
                invoiceStatus,
                sellername,
                time,
                transactionId
        };
        return item;
    }

    //for summary sales report
    public Object[] toArray_summary() {
        Object[] item = {
                productName,
                quantity,
                totalPrice,
                margin
        };
        return item;
    }

    // for detailed deliveries table in supplier module
    public Object[] toArray_deliveries() {
        Object[] item = {
                sellername,
                productName,
                quantity,
                price,
                totalPrice,
                invoiceNumber,
                time
        };
        return item;
    }
}
