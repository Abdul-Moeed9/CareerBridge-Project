package domain;


import java.util.Date;


public class TrendingSkill {
    private int trendID;
    private String skillName;
    private int frequency;
    private Date calculatedDate;


    // Getters and Setters
    public int getTrendID() { return trendID; }
    public void setTrendID(int id) { this.trendID = id; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String name) { this.skillName = name; }
    public int getFrequency() { return frequency; }
    public void setFrequency(int freq) { this.frequency = freq; }
    public Date getCalculatedDate() { return calculatedDate; }
    public void setCalculatedDate(Date date) { this.calculatedDate = date; }


    @Override
    public String toString() {
        return skillName;
    }
}
