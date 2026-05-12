package domain;


import java.util.Date;
import java.util.List;
import java.util.ArrayList;


public class JobFeed {


    private String source;
    private Date lastFetchedDate;
    private String rawXML;
    private List<Offering> jobs;


    public JobFeed() {
        this.jobs = new ArrayList<>();
    }


    public JobFeed(String source) {
        this.source = source;
        this.jobs = new ArrayList<>();
    }


    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }


    public Date getLastFetchedDate() { return lastFetchedDate; }
    public void setLastFetchedDate(Date lastFetchedDate) { this.lastFetchedDate = lastFetchedDate; }


    public String getRawXML() { return rawXML; }
    public void setRawXML(String rawXML) { this.rawXML = rawXML; }


    public List<Offering> getJobs() { return jobs; }
    public void setJobs(List<Offering> jobs) { this.jobs = jobs; }
}
