package model;

public class StockItem extends Product {
private int quantityStocked;

    public StockItem() {
    }

    public int getQuantityStocked() {
        return quantityStocked;
    }

    public void setQuantityStocked(int quantityStocked) {
        this.quantityStocked = quantityStocked;
    }

    @Override
    public Object[] toArray() {
        Object[] stockItem = {
                productId,
                productName,
                quantityStocked,
                dateCreated,
                lastModifiedDate,
                comment
        };
        return stockItem;
    }

    //TODO : May need to create a separate stockitem class for objects retrieved from stock status table to be used in Reports section
    public Object[] toArray2(){
        Object[] stockItem = {
                productName,
                quantityStocked,
                stockLowThreshold,
                lastModifiedDate
        };
        return  stockItem;
    }
}
