package model;

import java.time.LocalDateTime;

public class Delivery {
    private int id;
    private double price;
    private double quantity;
    private int supplierId;
    private int productId;
    private int invoiceNumber;
    private LocalDateTime dateTime;
    private double totalPrice;

    public Delivery() {
    }

    public Delivery(int id, double price, double quantity, int supplierId, int productId) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double computeTotalPrice(){
        return quantity*price;
    }

}
