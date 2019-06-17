package print;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

/**
 * TODO: this class should be merged with Receipt class  as there is a lot of duplication or should extend Receipt
 * update: Header class has been created and should phase out this class. It will contain business particulars and should
 * correspond to a db table.
 */
public class ReceiptHeader {
    private String businessName;
    private String telephoneNumber1;
    private String telephoneNumber2;
    private String location;
    private String tin;
    //private Timestamp timestamp;
    private String time;
    private String userName;
    private int receiptNumber;
    private int invoiceNumber;
    private Double cash;
    private Double change;
    private Double balance;
    private String firstname;
    private String lastname;

    public ReceiptHeader() {
        businessName="";
        telephoneNumber1 ="";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        time  = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        userName = "";
        receiptNumber=0;

    }

    public String getTin() {
        return tin;
    }

    public int getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(int invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Double getCash() {
        return cash;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void setCash(Double cash) {
        this.cash = cash;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTelephoneNumber1() {
        return telephoneNumber1;
    }

    public void setTelephoneNumber1(String telephoneNumber1) {
        this.telephoneNumber1 = telephoneNumber1;
    }

    public String getTelephoneNumber2() {
        return telephoneNumber2;
    }

    public void setTelephoneNumber2(String telephoneNumber2) {
        this.telephoneNumber2 = telephoneNumber2;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    //    public Timestamp getTimestamp() {
//        return timestamp;
//    }

//    public void setTimestamp(Timestamp timestamp) {
//        this.timestamp = timestamp;
//    }


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(int receiptNumber) {
        this.receiptNumber = receiptNumber;
    }
}
