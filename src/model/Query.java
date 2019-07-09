package model;

public class Query {
    private String startDate;
    private String endDate;
    private int tillnumber;
    private String username;

    public Query() {
        tillnumber=0;
        username="";
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getTillnumber() {
        return tillnumber;
    }

    public void setTillnumber(int tillnumber) {
        this.tillnumber = tillnumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
