package domain;

import java.util.Date;

public class Profile {
    private int profileID;
    private String education;
    private double CGPA;
    private String skills;
    private String experience;
    private String CVFile;
    private Date profileCreatedDate;
    private int seekerID;
    private String cvText;
    private String location;

    public int getProfileID() { return profileID; }
    public void setProfileID(int id) { this.profileID = id; }
    public String getEducation() { return education; }
    public void setEducation(String e) { this.education = e; }
    public double getCGPA() { return CGPA; }
    public void setCGPA(double c) { this.CGPA = c; }
    public String getSkills() { return skills; }
    public void setSkills(String s) { this.skills = s; }
    public String getExperience() { return experience; }
    public void setExperience(String e) { this.experience = e; }
    public String getCVFile() { return CVFile; }
    public void setCVFile(String f) { this.CVFile = f; }
    public Date getProfileCreatedDate() { return profileCreatedDate; }
    public void setProfileCreatedDate(Date d) { this.profileCreatedDate = d; }
    public int getSeekerID() { return seekerID; }
    public void setSeekerID(int id) { this.seekerID = id; }
    public String getCvText() { return cvText; }
    public void setCvText(String cvText) { this.cvText = cvText; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}