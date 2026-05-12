package domain;


public class Admin extends User {


    private int adminID;


    public Admin() {}


    public Admin(int userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "admin";
        this.accountStatus = "active";
    }


    @Override
    public boolean login() { return false; }


    @Override
    public void logout() {}


    @Override
    public void updateProfile() {}


    public int getAdminID() { return adminID; }
    public void setAdminID(int adminID) { this.adminID = adminID; }


    public boolean approveCompany(int companyID) { return false; }
    public boolean rejectCompany(int companyID) { return false; }
    public boolean deactivateUser(int userID) { return false; }
}
