package domain;
import java.util.Date;

public class Offering {
    // Attributes
    private int offeringID;
    private String offeringType;
    private String title;
    private String role;
    private String location;
    private Double requiredCGPA;
    private String requiredExperience;
    private double stipend;
    private String salaryText;
    private String jobDescription;
    private Date postedDate;
    private String status;
    private Integer companyID;
    private double matchScore;
    private String companyName;
    private String url;
    private String source;
    private String postedAtText;

    // Constructors
    public Offering() {
    }


    public Offering(int offeringID, String title, String location, String status) {
        this.offeringID = offeringID;
        this.title = title;
        this.location = location;
        this.status = status;
    }


    // Getters
    public int getOfferingID() {
        return offeringID;
    }


    public String getTitle() {
        return title;
    }


    public String getRole() {
        return role;
    }


    public String getLocation() {
        return location;
    }


    public Double getRequiredCGPA() {
        return requiredCGPA;
    }


    public double getStipend() {
        return stipend;
    }


    public String getStatus() {
        return status;
    }


    public double getMatchScore() {
        return matchScore;
    }


    public String getJobDescription() {
        return jobDescription;
    }


    public Integer getCompanyID() {
        return companyID;
    }


    public String getRequiredExperience() {
        return requiredExperience;
    }


    public String getOfferingType() {
        return offeringType;
    }


    public Date getPostedDate() {
        return postedDate;
    }


    // Setters
    public void setOfferingID(int offeringID) {
        this.offeringID = offeringID;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public void setRequiredCGPA(Double requiredCGPA) {
        this.requiredCGPA = requiredCGPA;
    }


    public void setStipend(double stipend) {
        this.stipend = stipend;
    }

    public String getSalaryText() {
        return salaryText;
    }

    public void setSalaryText(String salaryText) {
        this.salaryText = salaryText;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }


    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }


    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }


    public void setRequiredExperience(String requiredExperience) {
        this.requiredExperience = requiredExperience;
    }


    public void setOfferingType(String offeringType) {
        this.offeringType = offeringType;
    }


    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }


    @Override
    public String toString() {
        return title + " - " + location + " (CGPA: " + (requiredCGPA != null ? requiredCGPA : "None") + ")";
    }

    
public String getCompanyName() { return companyName; }
public void setCompanyName(String companyName) { this.companyName = companyName; }
public String getUrl() { return url; }
public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getPostedAtText() { return postedAtText; }
    public void setPostedAtText(String postedAtText) { this.postedAtText = postedAtText; }
}

