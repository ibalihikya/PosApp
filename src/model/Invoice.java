package model;

import java.util.ArrayList;

//TODO: Subclassing was done to accomodate the Invoice class so as to reuse the Printer class which uses Transaction.
// May need to Rethink this inheritance heirarchy

public class Invoice {
    private int id;
    private String firstName; // of customer
    private String lastName;
    private String username; // name of system user
    private int customerId;
    private double amount;
    ArrayList<Item> items;
    private String status;
    private String date_created;
    private String dateUpdated;


    public Invoice() {
        items = new ArrayList<>();
    }

    public Invoice(int id, String username) {
        this.id = id;
        this.username = username;
        items = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCustomerId() {
        return customerId;
    }



    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public void addAllItems(ArrayList<Item> soldItems){
        items.addAll(soldItems);
    }

    public double computeGrandTotal(){
        double totalAmount = 0.0;
        for(Item item : items){
            totalAmount +=  item.computeTotalPrice();
        }
        return  totalAmount;
    }


    @Override
    public String toString() {
        return "No. " + Integer.toString(id) + "  |  Amount: " + String.format("%,10.2f", amount) + "  |  " + status ;
    }
}
