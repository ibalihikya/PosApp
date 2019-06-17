package model;

import java.util.ArrayList;

public class CustomerTransaction extends Transaction {
    private int customerId;
    private String firstName;
    private String lastName;
    private double balance;
    private double cashReceived;//may need to remove this, it belongs to receipt
    private double change; //may need to remove this, it belongs to receipt
    //private InvoiceStatus invoiceStatus;

    public CustomerTransaction(String userName) {
        items = new ArrayList<>();
        this.userName = userName;
    }

    public CustomerTransaction(){
    }


    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public double getCashReceived() {
        return cashReceived;
    }

    public void setCashReceived(double cashReceived) {
        this.cashReceived = cashReceived;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

//    public InvoiceStatus getInvoiceStatus() {
//        return invoiceStatus;
//    }
//
//    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
//        this.invoiceStatus = invoiceStatus;
//    }
}
