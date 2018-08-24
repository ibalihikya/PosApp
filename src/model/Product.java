package model;

import java.sql.Date;

/**
 * A product to be inserted or retrieved from the database products table
 */
public class Product {
    protected int productId;
    protected String productName;
    private String description;
    private Category category;
    private String barcode;
    private String units;
    protected int stockLowThreshold;
    private  double price;
    protected String comment;
    protected Date dateCreated;
    protected Date lastModifiedDate;

    public Product() {
        category = new Category();
    }

    public Product(String productName) {
        this.productName = productName;
        this.stockLowThreshold = 0;
    }

    public int getStockLowThreshold() {
        return stockLowThreshold;
    }

    public void setStockLowThreshold(int stockLowThreshold) {
        this.stockLowThreshold = stockLowThreshold;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }



    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

//    public int getQuantity() {
//        return quantity;
//    }

//    public void setQuantity(int quantity) {
//        this.quantity = quantity;
//    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public Object[] toArray(){
        Object [] product = {
                productId,
                productName,
                description,
                category,
                price,
                units,
                stockLowThreshold,
                barcode,
                dateCreated,
                lastModifiedDate,
                comment,
        };

        return product;
    }

    @Override
    public String toString() {

        //return this.productName + " | " + this.price;
        return this.productName;
    }

    public static Product valueOf(String s){
        return new Product(s);
    }

}
