package domain;


public class Company extends User {


    private int companyID;
    private String companyName;
    private String industry;
    private String contactEmail;
    private String approvalStatus;


    public Company() {}


    public Company(int userID, String name, String email, String password,
                   String companyName, String industry, String contactEmail) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "company";
        this.accountStatus = "active";
        this.companyName = companyName;
        this.industry = industry;
        this.contactEmail = contactEmail;
        this.approvalStatus = "pending";
    }


    @Override
    public boolean login() { return false; }


    @Override
    public void logout() {}


    @Override
    public void updateProfile() {}


    public int getCompanyID() { return companyID; }
    public void setCompanyID(int companyID) { this.companyID = companyID; }


    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }


    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }


    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }


    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
}
