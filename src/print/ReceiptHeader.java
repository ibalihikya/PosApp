package print;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class ReceiptHeader {
    private String businessName;
    private String telephoneNumber;
    //private Timestamp timestamp;
    private String time;
    private String userName;
    private int receiptNumber;

    public ReceiptHeader() {
        businessName="";
        telephoneNumber="";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        time  = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(timestamp);
        userName = "";
        receiptNumber=0;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
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
