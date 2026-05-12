package controller;

import dao.OfferingDAO;
import domain.FilterCandidate;
import domain.Offering;
import interfaces.IFilterable;
import interfaces.IRankable;
import service.AIMatchingService;

import java.util.ArrayList;
import java.util.List;

public class OfferingController {

    private OfferingDAO offeringDAO;
    private IFilterable filterService;
    private IRankable offeringRanker;
    private AIMatchingService aiMatchingService;

    public OfferingController(IFilterable filterService, IRankable offeringRanker, AIMatchingService aiMatchingService) {
        this.offeringDAO = new OfferingDAO();
        this.filterService = filterService;
        this.offeringRanker = offeringRanker;
        this.aiMatchingService = aiMatchingService;
    }

    public List<Offering> getAllOfferings() {
        return offeringDAO.getAll();
    }

    public List<Offering> getFilteredOfferings(FilterCandidate filter, int seekerID) {
        List<Offering> all = offeringDAO.getAll();
        List<Object> items = new ArrayList<>(all);
        List<Object> filtered = filterService.applyFilter(items, filter);
        List<Offering> result = new ArrayList<>();
        for (Object obj : filtered) {
            if (obj instanceof Offering) result.add((Offering) obj);
        }
        return aiMatchingService.rankOfferingsForSeeker(seekerID, result);
    }

    public List<Offering> getRankedOfferings(int seekerID) {
        List<Offering> offerings = offeringDAO.getAll();
        return aiMatchingService.rankOfferingsForSeeker(seekerID, offerings);
    }

    public Offering getOfferingByID(int offeringID) {
        return offeringDAO.findByID(offeringID);
    }

    public List<Offering> searchOfferings(String keyword) {
        return offeringDAO.searchByKeyword(keyword);
    }
}