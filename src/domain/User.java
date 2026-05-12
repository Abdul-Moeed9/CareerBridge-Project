package domain;


import java.util.Date;


public abstract class User {


    protected int userID;
    protected String name;
    protected String email;
    protected String password;
    protected String role;
    protected String accountStatus;
    protected Date registrationDate;


    public abstract boolean login();
    public abstract void logout();
    public abstract void updateProfile();


    public int getUserID() { return userID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getAccountStatus() { return accountStatus; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setAccountStatus(String status) { this.accountStatus = status; }
    public String getPassword() { return password; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setRole(String role) { this.role = role; }
    public void setRegistrationDate(Date registrationDate) { this.registrationDate = registrationDate; }
    public Date getRegistrationDate() { return registrationDate; }

}
