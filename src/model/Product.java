package model;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

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
    protected double stockLowThreshold;
    private double costprice;
    private  double price; // selling price
    private double markup;
    protected String comment;
//    protected Date dateCreated;
//    protected Date lastModifiedDate;
    protected String dateCreated;
    protected String lastModifiedDate;

    public Product() {
        category = new Category();
    }

    public Product(String productName) {
        this.productName = productName;
        this.stockLowThreshold = 0;
    }

    public double getCostprice() {
        return costprice;
    }

    public double getMarkup() {
        return markup;
    }

    public void setMarkup(double markup) {
        this.markup = markup;
    }

    public void setCostprice(double costprice) {
        this.costprice = costprice;
    }

    public double getStockLowThreshold() {
        return stockLowThreshold;
    }

    public void setStockLowThreshold(double stockLowThreshold) {
        this.stockLowThreshold = stockLowThreshold;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
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
                costprice,
                price,
                markup,
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

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(',');
        formatter.setDecimalFormatSymbols(symbols);

        if(this.productName == null){
            return "";
        }

        return this.productName + " | " + formatter.format(Math.round(this.price));
    }

    public static Product valueOf(String s){
        return new Product(s);
    }

}
