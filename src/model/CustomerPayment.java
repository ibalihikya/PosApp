package model;

public class CustomerPayment extends Payment {
    int customerId;

    public CustomerPayment() {
    }

    public CustomerPayment(int customerId) {
        this.customerId = customerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
