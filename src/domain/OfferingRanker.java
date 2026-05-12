package domain;


import interfaces.IRankable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class OfferingRanker extends ModelTrainer implements IRankable {


    private double locationWeight;
    private double matchingWeight;


    public OfferingRanker() {
        this.locationWeight = 0.30;
        this.matchingWeight = 0.70;
    }


    @Override
    public void train(List<Object> data) {
        this.trainingData = data;
        this.lastTrained = new Date();
    }


    @Override
    public double evaluate() { return 0.0; }


    @Override
    public List<Object> rank(List<Object> items) {
        List<Offering> offerings = new ArrayList<>();
        for (Object obj : items) {
            if (obj instanceof Offering) offerings.add((Offering) obj);
        }
        List<Offering> ranked = sortByDescendingScore(offerings);
        return new ArrayList<>(ranked);
    }


    @Override
    public double computeScore(Object item) {
        if (item instanceof Offering) {
            Offering offering = (Offering) item;
            return offering.getMatchScore() * matchingWeight;
        }
        return 0.0;
    }


    @Override
    public List<Object> sortByScore(List<Object> items) {
        return rank(items);
    }


public List<Offering> rankOfferingsForSeeker(Profile profile, List<Offering> offerings) {
    for (Offering offering : offerings) {
        offering.setMatchScore(computeOfferingScore(profile, offering));
    }
    return sortByDescendingScore(offerings);
}


    private double computeOfferingScore(Profile profile, Offering offering) {
        double score = offering.getMatchScore() * matchingWeight;
        if (profile != null && offering.getLocation() != null) {
            String profileLocation = profile.getLocation();
            if (profileLocation != null
                    && profileLocation.toLowerCase().contains(offering.getLocation().toLowerCase())) {
                score += locationWeight;
            }
        }
        return score;
    }


    private List<Offering> sortByDescendingScore(List<Offering> offerings) {
        offerings.sort(Comparator.comparingDouble(Offering::getMatchScore).reversed());
        return offerings;
    }


    public double getLocationWeight() { return locationWeight; }
    public double getMatchingWeight() { return matchingWeight; }
}


