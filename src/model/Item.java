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
    private String time_deleted; // time deleted
    private static int itemCount = 0;
    private double margin;
    private String sellername; //username for logged in user or supplier name
    private int sellerid; //supplier
    private int invoiceNumber;
    private String units;
    private int transactionId; //TODO: rename this to id; this is the primary key in the item table
    private InvoiceStatus invoiceStatus;
    //TODO: may need to create a child class with the attributes below because these items are supplier items not the ones being bought by a customer
    private double amountPaid;
    private double balance;
    private String customerName;
    private double vat_amount;
    private int till;

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public String getTime_deleted() {
        return time_deleted;
    }

    public void setTime_deleted(String time_deleted) {
        this.time_deleted = time_deleted;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getTill() {
        return till;
    }

    public void setTill(int till) {
        this.till = till;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getVat_amount() {
        return vat_amount;
    }

    public void setVat_amount(double vat_amount) {
        this.vat_amount = vat_amount;
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

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
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
                transactionId,
                productName,
                quantity,
                price,
                totalPrice,
                vat_amount,
                margin,
                invoiceNumber,
                customerName,
                invoiceStatus,
                sellername,
                till,
                time,
                productId

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
