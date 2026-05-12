package utility;


import java.util.List;
import java.util.Map;


public class AnalyticsData {


    private double avgCGPA;
    private int totalApplications;
    private Map<String, Integer> matchScoreDistribution;
    private List<String> topSkills;
    private Map<String, Integer> applicationsByOffering;


    public AnalyticsData(double avgCGPA, int totalApplications,
                         Map<String, Integer> matchScoreDistribution,
                         List<String> topSkills,
                         Map<String, Integer> applicationsByOffering) {
        this.avgCGPA = avgCGPA;
        this.totalApplications = totalApplications;
        this.matchScoreDistribution = matchScoreDistribution;
        this.topSkills = topSkills;
        this.applicationsByOffering = applicationsByOffering;
    }


    public double getAvgCGPA() { return avgCGPA; }
    public int getTotalApplications() { return totalApplications; }
    public Map<String, Integer> getMatchDistribution() { return matchScoreDistribution; }
    public List<String> getTopSkills() { return topSkills; }
    public Map<String, Integer> getApplicationsByOffering() { return applicationsByOffering; }
}
