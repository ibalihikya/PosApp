package model;

public class Category {
    private String categoryName;
    private String description;
    private int categoryId;
    //private ArrayList<Product> products;


    public Category() {
        //categoryId=0;
    }

    public Category(String categoryName){
        this.categoryName = categoryName;
        //categoryId=0;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return categoryName;
    }

    public Object[] toArray() {
        Object[] category = {
                //categoryId,
                categoryName,
                description
        };
        return category;
    }

    //    public ArrayList<Product> getProducts() {
//        return products;
//    }
//
//    public void setProducts(ArrayList<Product> products) {
//        this.products = products;
//    }
}
