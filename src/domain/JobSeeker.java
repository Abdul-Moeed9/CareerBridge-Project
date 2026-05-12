package domain;


public class JobSeeker extends User {


    private int seekerID;
    private String profileStatus;


    public JobSeeker() {}


    public JobSeeker(int userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = "job_seeker";
        this.accountStatus = "active";
        this.profileStatus = "incomplete";
    }


    @Override
    public boolean login() { return false; }


    @Override
    public void logout() {}


    @Override
    public void updateProfile() {}


    public int getSeekerID() { return seekerID; }
    public void setSeekerID(int seekerID) { this.seekerID = seekerID; }


    public String getProfileStatus() { return profileStatus; }
    public void setProfileStatus(String profileStatus) { this.profileStatus = profileStatus; }
}
