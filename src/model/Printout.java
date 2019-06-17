package model;

//to hold the invoice and receipt pairs
public class Printout {

    private Invoice invoice;
    private Receipt receipt;

    public Printout(Invoice invoice, Receipt receipt) {
        this.invoice = invoice;
        this.receipt = receipt;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }
}
