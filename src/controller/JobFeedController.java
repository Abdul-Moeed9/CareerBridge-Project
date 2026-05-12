package controller;

import dao.OfferingDAO;
import domain.Offering;
import domain.TrendingSkill;
import service.JobFeedService;
import service.TrendingSkillService;

import java.util.Date;
import java.util.List;

public class JobFeedController {

    private JobFeedService jobFeedService;
    private OfferingDAO offeringDAO;
    private TrendingSkillService trendingSkillService;

    public JobFeedController(JobFeedService jobFeedService) {
        this.jobFeedService = jobFeedService;
        this.offeringDAO = new OfferingDAO();
        this.trendingSkillService = new TrendingSkillService();
    }

public boolean refreshFeed() {
    try {
        List<Offering> combined = jobFeedService.fetchAll();
        jobFeedService.saveToDatabase(combined);

        List<TrendingSkill> skills = trendingSkillService.computeTrendingSkills();
        trendingSkillService.saveComputedSkills(skills);

        return true;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    public List<Offering> getLatestFeedOfferings() {
        return offeringDAO.getAll();
    }

    public Date getLastFetchedTime() {
        return jobFeedService.getLastFetchedDate();
    }

    
}