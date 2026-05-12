package controller;

import dao.ApplicationDAO;
import dao.CompanyDAO;
import dao.OfferingDAO;
import dao.ProfileDAO;
import domain.Application;
import domain.ApplicantRanker;
import domain.Offering;
import domain.Profile;
import service.AIMatchingService;
import utility.AnalyticsData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompanyController {

    private CompanyDAO companyDAO;
    private OfferingDAO offeringDAO;
    private ApplicationDAO applicationDAO;
    private ProfileDAO profileDAO;
    private AIMatchingService aiMatchingService;
    private ApplicantRanker applicantRanker;

    public CompanyController(AIMatchingService aiMatchingService) {
        this.companyDAO = new CompanyDAO();
        this.offeringDAO = new OfferingDAO();
        this.applicationDAO = new ApplicationDAO();
        this.profileDAO = new ProfileDAO();
        this.aiMatchingService = aiMatchingService;
        this.applicantRanker = new ApplicantRanker();
    }

    public boolean postOffering(Offering offering) {
        return offeringDAO.save(offering);
    }

    public List<Offering> getPostedOfferings(int companyID) {
        return offeringDAO.getByCompany(companyID);
    }

    public List<Application> getApplicants(int offeringID) {
        return applicationDAO.getByOffering(offeringID);
    }

    public List<Application> getRankedApplicants(int offeringID) {
        List<Application> applications = applicationDAO.getByOffering(offeringID);
        Offering offering = offeringDAO.findByID(offeringID);

        for (Application app : applications) {
            Profile profile = profileDAO.findBySeekerID(app.getSeekerID());
            app.setProfile(profile);
        }

        if (offering != null) {
            List<Application> ranked = applicantRanker.rankApplicantsForOffering(offering, applications);
            for (Application app : ranked) {
                applicationDAO.updateRank(app.getApplicationID(), app.getWeightedRank());
            }
            return ranked;
        }

        return applications;
    }

    public boolean closeOffering(int offeringID) {
        applicationDAO.deleteByOffering(offeringID);
        return offeringDAO.delete(offeringID);
    }

    public AnalyticsData getAnalytics(int companyID) {
        List<Offering> offerings = offeringDAO.getByCompany(companyID);
        if (offerings == null) offerings = new ArrayList<>();

        int totalApplications = 0;
        double cgpaSum = 0.0;
        int cgpaCount = 0;
        Map<String, Integer> matchDistribution = new HashMap<>();
        matchDistribution.put("0-20", 0);
        matchDistribution.put("21-40", 0);
        matchDistribution.put("41-60", 0);
        matchDistribution.put("61-80", 0);
        matchDistribution.put("81-100", 0);
        List<String> topSkills = new ArrayList<>();
        Map<String, Integer> applicationsByOffering = new HashMap<>();

        for (Offering offering : offerings) {
            List<Application> apps = applicationDAO.getByOffering(offering.getOfferingID());
            totalApplications += apps.size();
            applicationsByOffering.put(offering.getTitle(), apps.size());

            for (Application app : apps) {
                Profile profile = profileDAO.findBySeekerID(app.getSeekerID());
                if (profile != null) {
                    if (profile.getCGPA() > 0) {
                        cgpaSum += profile.getCGPA();
                        cgpaCount++;
                    }
                    double score = app.getWeightedRank() * 100;
                    if (score <= 20) matchDistribution.merge("0-20", 1, Integer::sum);
                    else if (score <= 40) matchDistribution.merge("21-40", 1, Integer::sum);
                    else if (score <= 60) matchDistribution.merge("41-60", 1, Integer::sum);
                    else if (score <= 80) matchDistribution.merge("61-80", 1, Integer::sum);
                    else matchDistribution.merge("81-100", 1, Integer::sum);

                    if (profile.getSkills() != null && !profile.getSkills().isEmpty()) {
                        String[] skills = profile.getSkills().split("[,;]+");
                        for (String skill : skills) {
                            String trimmed = skill.trim();
                            if (!trimmed.isEmpty() && !topSkills.contains(trimmed) && topSkills.size() < 10) {
                                topSkills.add(trimmed);
                            }
                        }
                    }
                }
            }
        }

        double avgCGPA = cgpaCount > 0 ? Math.round((cgpaSum / cgpaCount) * 100.0) / 100.0 : 0.0;

        return new AnalyticsData(avgCGPA, totalApplications, matchDistribution, topSkills, applicationsByOffering);
    }
}