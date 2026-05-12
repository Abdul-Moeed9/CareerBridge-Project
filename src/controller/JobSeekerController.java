package controller;

import dao.ApplicationDAO;
import dao.JobSeekerDAO;
import dao.OfferingDAO;
import dao.ProfileDAO;
import domain.Application;
import domain.JobSeeker;
import domain.Offering;
import domain.Profile;
import service.AIMatchingService;

import java.util.List;

public class JobSeekerController {

    private JobSeekerDAO seekerDAO;
    private ProfileDAO profileDAO;
    private OfferingDAO offeringDAO;
    private ApplicationDAO applicationDAO;
    private AIMatchingService aiMatchingService;

    public JobSeekerController(AIMatchingService aiMatchingService) {
        this.seekerDAO = new JobSeekerDAO();
        this.profileDAO = new ProfileDAO();
        this.offeringDAO = new OfferingDAO();
        this.applicationDAO = new ApplicationDAO();
        this.aiMatchingService = aiMatchingService;
    }

    public Profile getProfile(int seekerID) {
        return profileDAO.findBySeekerID(seekerID);
    }

    public JobSeeker findSeekerByEmail(String email) {
        return seekerDAO.findByEmail(email);
    }

    public JobSeeker findSeekerBySeekerID(int seekerID) {
        return seekerDAO.findBySeekerID(seekerID);
    }

    public boolean saveProfile(Profile profile) {
        Profile existing = profileDAO.findBySeekerID(profile.getSeekerID());
        if (existing != null) {
            profile.setProfileID(existing.getProfileID());
            return profileDAO.update(profile);
        }
        return profileDAO.save(profile);
    }

    public List<Offering> getRecommendedOfferings(int seekerID) {
        List<Offering> offerings = offeringDAO.getAll();
        return aiMatchingService.rankOfferingsForSeeker(seekerID, offerings);
    }

public boolean applyForOffering(int seekerID, int offeringID) {
    Offering offering = offeringDAO.findByID(offeringID);
    if (offering == null) return false;
    if ("scraped".equals(offering.getSource())) return false;
    if (applicationDAO.checkDuplicate(seekerID, offeringID)) return false;
    Profile profile = profileDAO.findBySeekerID(seekerID);
    String resumeFile = (profile != null) ? profile.getCVFile() : "";
    Application application = new Application(seekerID, offeringID, resumeFile);
    return applicationDAO.save(application);
}

    public boolean withdrawApplication(int applicationID, int seekerID) {
        Application app = applicationDAO.findByID(applicationID);
        if (app == null || app.getSeekerID() != seekerID) return false;
        if ("accepted".equalsIgnoreCase(app.getStatus())) return false;
        return applicationDAO.delete(applicationID);
    }

    public List<Application> getApplicationHistory(int seekerID) {
        return applicationDAO.getBySeeker(seekerID);
    }

    public double getMatchScore(int seekerID, int offeringID) {
        Profile profile = profileDAO.findBySeekerID(seekerID);
        Offering offering = offeringDAO.findByID(offeringID);
        if (profile == null || offering == null) return 0.0;
        return aiMatchingService.computeMatchScore(profile, offering);
    }
}