package domain;


import java.util.Date;


public class Application {


    private int applicationID;
    private Date applicationDate;
    private String resumeFile;
    private String status;
    private double weightedRank;
    private int seekerID;
    private int offeringID;
    private Profile profile;


    public Application() {}


    public Application(int seekerID, int offeringID, String resumeFile) {
        this.seekerID = seekerID;
        this.offeringID = offeringID;
        this.resumeFile = resumeFile;
        this.applicationDate = new Date();
        this.status = "pending";
        this.weightedRank = 0.0;
    }


    public int getApplicationID() { return applicationID; }
    public void setApplicationID(int applicationID) { this.applicationID = applicationID; }


    public Date getApplicationDate() { return applicationDate; }
    public void setApplicationDate(Date applicationDate) { this.applicationDate = applicationDate; }


    public String getResumeFile() { return resumeFile; }
    public void setResumeFile(String resumeFile) { this.resumeFile = resumeFile; }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }


    public double getWeightedRank() { return weightedRank; }
    public void setWeightedRank(double weightedRank) { this.weightedRank = weightedRank; }


    public int getSeekerID() { return seekerID; }
    public void setSeekerID(int seekerID) { this.seekerID = seekerID; }


    public int getOfferingID() { return offeringID; }
    public void setOfferingID(int offeringID) { this.offeringID = offeringID; }


    public Profile getProfile() { return profile; }
    public void setProfile(Profile profile) { this.profile = profile; }
}
