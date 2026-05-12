package domain;


public class FilterCandidate {


    private String position;
    private String location;
    private String role;
    private double CGPA;
    private String experience;
    private double stipend;


    public FilterCandidate() {}


    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }


    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }


    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }


    public double getCGPA() { return CGPA; }
    public void setCGPA(double CGPA) { this.CGPA = CGPA; }


    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }


    public double getStipend() { return stipend; }
    public void setStipend(double stipend) { this.stipend = stipend; }
}
