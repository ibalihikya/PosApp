package model;

import java.util.Arrays;

public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private byte[] salt;
    private boolean admin;
    private String date_created;
    private String date_modified;


    public User() {
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }


    public String getDate_modified() {
        return date_modified;
    }


    public void setDate_modified(String date_modified) {
        this.date_modified = date_modified;
    }

    @Override
    public String toString() {
        return userName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (admin != user.admin) return false;
        if (!userName.equals(user.userName)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (!password.equals(user.password)) return false;
        return Arrays.equals(salt, user.salt);
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
//        result = 31 * result + password.hashCode();
        result = 31 * result + Arrays.hashCode(salt);
        result = 31 * result + (admin ? 1 : 0);
        return result;
    }
}
