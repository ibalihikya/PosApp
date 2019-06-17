package model;
//TODO: this should extend product
public class StockItem {
    private int productId;
    private String productName;
    private double quantity;
    private double balance;
    private int transactionId;
    private String dateCreated;
    private String lastModifiedDate;
    //private double stockLowThreshold;
    //private Location location;
    private Site location;
    private Direction direction;
    private Site source_dest ;
    private String comment;


    public StockItem() {
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Site getLocation() {
        return location;
    }

    public void setLocation(Site location) {
        this.location = location;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Site getSource_dest() {
        return source_dest;
    }

    public void setSource_dest(Site source_dest) {
        this.source_dest = source_dest;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Object[] toArray() {
        Object[] stockItem = {
                productId,
                productName,
                quantity,
                dateCreated,
                lastModifiedDate,
                transactionId,
                comment

        };
        return stockItem;
    }


}
