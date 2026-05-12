package controller;

import dao.ApplicationDAO;
import dao.OfferingDAO;
import dao.ProfileDAO;
import domain.Application;
import domain.Offering;
import domain.Profile;
import domain.ApplicantRanker;
import service.AIMatchingService;

import java.util.ArrayList;
import java.util.List;

public class ApplicationController {

    private ApplicationDAO applicationDAO;
    private OfferingDAO offeringDAO;
    private ProfileDAO profileDAO;
    private ApplicantRanker applicantRanker;
    private AIMatchingService aiMatchingService;

    public ApplicationController(ApplicantRanker applicantRanker, AIMatchingService aiMatchingService) {
        this.applicationDAO = new ApplicationDAO();
        this.offeringDAO = new OfferingDAO();
        this.profileDAO = new ProfileDAO();
        this.applicantRanker = applicantRanker;
        this.aiMatchingService = aiMatchingService;
    }

    public boolean submitApplication(Application application) {
        if (applicationDAO.checkDuplicate(application.getSeekerID(), application.getOfferingID())) {
            return false;
        }
        return applicationDAO.save(application);
    }

    public List<Application> getApplicationsByOffering(int offeringID) {
        return applicationDAO.getByOffering(offeringID);
    }

    public List<Application> getApplicationsBySeeker(int seekerID) {
        return applicationDAO.getBySeeker(seekerID);
    }

    public List<Application> rankApplications(int offeringID) {
        List<Application> applications = applicationDAO.getByOffering(offeringID);
        Offering offering = offeringDAO.findByID(offeringID);

        for (Application app : applications) {
            Profile profile = profileDAO.findBySeekerID(app.getSeekerID());
            app.setProfile(profile);
        }

        List<Application> ranked;
        if (offering != null) {
            ranked = applicantRanker.rankApplicantsForOffering(offering, applications);
        } else {
            List<Object> items = new ArrayList<>(applications);
            List<Object> rankedObjects = applicantRanker.rank(items);
            ranked = new ArrayList<>();
            for (Object obj : rankedObjects) {
                if (obj instanceof Application) ranked.add((Application) obj);
            }
        }

        for (Application app : ranked) {
            applicationDAO.updateRank(app.getApplicationID(), app.getWeightedRank());
        }

        return ranked;
    }

    public boolean updateApplicationStatus(int applicationID, String status) {
        boolean success = applicationDAO.updateStatus(applicationID, status);
        if (success && "accepted".equalsIgnoreCase(status)) {
            Application accepted = applicationDAO.findByID(applicationID);
            if (accepted != null) {
                List<Application> others = applicationDAO.getByOffering(accepted.getOfferingID());
                for (Application app : others) {
                    if (app.getApplicationID() != applicationID) {
                        applicationDAO.updateStatus(app.getApplicationID(), "rejected");
                    }
                }
            }
        }
        return success;
    }
}