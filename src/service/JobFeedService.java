package service;

import dao.OfferingDAO;
import domain.Offering;
import interfaces.IFeedFetchable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import domain.JobFeed;

public class JobFeedService {

    private IFeedFetchable rssParser;
    private IFeedFetchable jobAPIClient;
    private Date lastFetchedDate;
    private OfferingDAO offeringDAO;
    private JobFeed jobFeed;

    public JobFeedService(IFeedFetchable rssParser, IFeedFetchable jobAPIClient) {
        this.rssParser = rssParser;
        this.jobAPIClient = jobAPIClient;
        this.offeringDAO = new OfferingDAO();
        this.jobFeed = new JobFeed();
    }

public List<Offering> fetchFromRozee() {
    List<Offering> offerings = rssParser.fetch();
    for (Offering o : offerings) {
        o.setSource("scraped");
    }
    this.lastFetchedDate = new Date();
    jobFeed.setSource("rozee");
    jobFeed.setJobs(offerings);
    jobFeed.setLastFetchedDate(this.lastFetchedDate);
    return offerings;
}

public List<Offering> fetchFromJobDataAPI() {
    List<Offering> offerings = jobAPIClient.fetch();
    for (Offering o : offerings) {
        o.setSource("scraped");
    }
    this.lastFetchedDate = new Date();
    jobFeed.setSource("jobdataapi");
    jobFeed.setJobs(offerings);
    jobFeed.setLastFetchedDate(this.lastFetchedDate);
    return offerings;
}

    private List<Offering> mergeAndDeduplicate(List<Offering> list1, List<Offering> list2) {
        List<Offering> merged = new ArrayList<>(list1);
        Set<String> titles = new HashSet<>();
        for (Offering o : list1) {
            if (o.getTitle() != null) titles.add(o.getTitle().toLowerCase().trim());
        }
        for (Offering o : list2) {
            if (o.getTitle() != null && !titles.contains(o.getTitle().toLowerCase().trim())) {
                merged.add(o);
                titles.add(o.getTitle().toLowerCase().trim());
            }
        }
        return merged;
    }

    public List<Offering> fetchAll() {
    List<Offering> rozee = fetchFromRozee();
    List<Offering> api = fetchFromJobDataAPI();
    return mergeAndDeduplicate(rozee, api);
    }

    public void saveToDatabase(List<Offering> offerings) {
        List<Offering> allExisting = offeringDAO.getAll();
        for (Offering offering : offerings) {
            boolean duplicate = false;
            for (Offering e : allExisting) {
                if (e.getTitle() != null && offering.getTitle() != null &&
                    e.getTitle().equalsIgnoreCase(offering.getTitle()) &&
                    java.util.Objects.equals(e.getCompanyID(), offering.getCompanyID())) {
                    duplicate = true;
                    break;
                }
            }
            if (!duplicate) {
                offeringDAO.save(offering);
                allExisting.add(offering);
            }
        }
        this.lastFetchedDate = new Date();
    }

    public Date getLastFetchedDate() {
        return lastFetchedDate;
    }
}