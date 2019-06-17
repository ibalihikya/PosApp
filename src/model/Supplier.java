package model;

public class Supplier extends Partner {
    private String supplierName;
    private String bankName;
    private String accountNumber;

    public Supplier(String supplierName) {
        this.supplierName = supplierName;
    }

    public Supplier() {

    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public Object[] toArray() {
        Object[] supplier = {
                supplierName,
                phone1,
                phone2,
                email,
                address,
                bankName,
                accountNumber,
                dateCreated
        };
        return supplier;
    }

    @Override
    public String toString() {
        return supplierName ;
    }
}
