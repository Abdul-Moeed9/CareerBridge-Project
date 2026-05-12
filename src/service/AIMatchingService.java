package service;

import dao.ApplicationDAO;
import dao.OfferingDAO;
import dao.ProfileDAO;
import domain.Application;
import domain.ModelTrainer;
import domain.Offering;
import domain.Profile;
import interfaces.IRankable;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class AIMatchingService {

    private static final String MODEL_FILE = utility.AppPaths.getModelFile().getAbsolutePath();

    private ModelTrainer modelTrainer;
    private IRankable applicantRanker;
    private IRankable offeringRanker;
    private ApplicationDAO applicationDAO;
    private ProfileDAO profileDAO;
    private OfferingDAO offeringDAO;
    private SmileOLSModel smileModel;
    private boolean modelTrained;

    private static final String[] HARD_SKILLS = {
        "java", "python", "javascript", "react", "angular", "node.js",
        "spring boot", "django", "sql", "mysql", "postgresql", "mongodb",
        "html", "css", "typescript", "c++", "c#", ".net", "php",
        "flutter", "kotlin", "go", "rest api", "microservices",
        "machine learning", "deep learning", "data science", "r",
        "scala", "ruby", "swift", "objective-c", "rust", "vue.js",
        "next.js", "express.js", "graphql", "redis", "elasticsearch"
    };

    private static final String[] SOFT_SKILLS = {
        "communication", "leadership", "teamwork", "problem-solving",
        "analytical", "adaptability", "critical thinking", "presentation",
        "negotiation", "mentoring", "collaboration", "creativity",
        "time management", "decision making", "conflict resolution"
    };

    private static final String[] EDUCATION_TERMS = {
        "bachelor", "master", "phd", "bsc", "bba", "mba",
        "computer science", "software engineering", "information technology",
        "electrical engineering", "data science", "mathematics",
        "associate degree", "diploma", "certification"
    };

    private static final String[] TOOLS_TECHNOLOGIES = {
        "jira", "github", "jenkins", "figma", "postman", "docker",
        "kubernetes", "excel", "powerbi", "tableau", "slack", "trello",
        "confluence", "bitbucket", "gitlab", "terraform", "ansible",
        "maven", "gradle", "webpack", "npm", "yarn", "vs code",
        "intellij", "eclipse", "android studio", "xcode"
    };

    private static final String[] CERTIFICATIONS = {
        "aws certified", "azure certified", "pmp", "scrum master",
        "cissp", "ccna", "google certified", "oracle certified",
        "comptia", "itil", "six sigma", "ceh", "ccnp"
    };

    private static final String[] EXPERIENCE_INDICATORS = {
        "fresh graduate", "entry level", "junior", "mid level",
        "senior", "lead", "manager", "director", "principal",
        "1-2 years", "3-5 years", "5-7 years", "7-10 years",
        "10+ years", "internship", "co-op", "fresher"
    };

    private static final String[] JOB_TYPE = {
        "full-time", "part-time", "contract", "remote", "hybrid",
        "on-site", "freelance", "temporary", "permanent", "internship"
    };

    private static final String[] ACTION_VERBS = {
        "develop", "design", "implement", "manage", "lead", "analyze",
        "deploy", "build", "create", "maintain", "optimize", "integrate",
        "test", "debug", "architect", "configure", "automate", "monitor",
        "collaborate", "coordinate", "deliver", "execute", "plan", "research"
    };

    private static final String[][] ALL_CATEGORIES = {
        HARD_SKILLS, SOFT_SKILLS, EDUCATION_TERMS, TOOLS_TECHNOLOGIES,
        CERTIFICATIONS, EXPERIENCE_INDICATORS, JOB_TYPE, ACTION_VERBS
    };

    public AIMatchingService(ModelTrainer modelTrainer, IRankable applicantRanker, IRankable offeringRanker) {
        this.modelTrainer = modelTrainer;
        this.applicantRanker = applicantRanker;
        this.offeringRanker = offeringRanker;
        this.applicationDAO = new ApplicationDAO();
        this.profileDAO = new ProfileDAO();
        this.offeringDAO = new OfferingDAO();
        this.modelTrained = false;
        initializeModel();
    }

    public AIMatchingService() {
        this.applicantRanker = new domain.ApplicantRanker();
        this.offeringRanker = new domain.OfferingRanker();
        this.modelTrainer = (ModelTrainer) this.applicantRanker;
        this.applicationDAO = new ApplicationDAO();
        this.profileDAO = new ProfileDAO();
        this.offeringDAO = new OfferingDAO();
        this.modelTrained = false;
        initializeModel();
    }

    private void initializeModel() {
        File savedModel = new File(MODEL_FILE);
        if (savedModel.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(savedModel))) {
                smileModel = (SmileOLSModel) ois.readObject();
                modelTrained = true;
                System.out.println("Loaded trained model from " + MODEL_FILE);
                System.out.println("Model info: " + smileModel);
            } catch (Exception e) {
                System.err.println("Failed to load saved model (may need retraining): " + e.getMessage());
                modelTrained = false;
            }
        } else {
            System.out.println("No saved model found. Using fallback scoring until model is trained.");
            modelTrained = false;
        }
    }

    public static boolean isModelTrained() {
        return new File(MODEL_FILE).exists();
    }

    public void trainFromDatabase() {
        try {
            List<Offering> offerings = offeringDAO.getAll();
            if (offerings.isEmpty()) {
                System.err.println("No offerings in database, model not trained.");
                return;
            }

            List<Profile> allProfiles = getAllProfiles();
            if (allProfiles.isEmpty()) {
                System.err.println("No profiles in database, model not trained.");
                return;
            }

            List<double[]> featureRows = new ArrayList<>();
            List<Double> targets = new ArrayList<>();

            for (Profile profile : allProfiles) {
                for (Offering offering : offerings) {
                    double cgpaRatio = computeCGPARatio(profile, offering);
                    double skillOverlap = computeSkillOverlap(profile, offering);
                    double experienceMatch = computeExperienceMatch(profile, offering);
                    double locationMatch = computeLocationMatch(profile, offering);
                    double cosine = computeCosineSimilarity(profile, offering);

                    double target = (cgpaRatio * 0.20) + (skillOverlap * 0.30) +
                                    (experienceMatch * 0.15) + (locationMatch * 0.10) +
                                    (cosine * 0.25);
                    target = Math.max(0.0, Math.min(1.0, target));

                    featureRows.add(new double[]{cgpaRatio, skillOverlap, experienceMatch, locationMatch, cosine});
                    targets.add(target);
                }
            }

            if (featureRows.size() < 6) {
                System.err.println("Not enough training pairs (" + featureRows.size() + "), need at least 6.");
                return;
            }

            double[][] X = featureRows.toArray(new double[0][]);
            double[] y = targets.stream().mapToDouble(Double::doubleValue).toArray();

            smileModel = new SmileOLSModel(X, y);
            modelTrained = true;
            System.out.println("Smile OLS model trained on " + X.length + " instances.");
            System.out.println("Model info: " + smileModel);

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODEL_FILE))) {
                oos.writeObject(smileModel);
                System.out.println("Saved trained model to " + MODEL_FILE);
            }

        } catch (Exception e) {
            System.err.println("Training from DB failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private List<Profile> getAllProfiles() {
        List<Profile> profiles = new ArrayList<>();
        List<Application> apps = applicationDAO.getAll();
        Set<Integer> seenSeekers = new HashSet<>();
        for (Application app : apps) {
            if (seenSeekers.add(app.getSeekerID())) {
                Profile p = profileDAO.findBySeekerID(app.getSeekerID());
                if (p != null) profiles.add(p);
            }
        }
        if (profiles.isEmpty()) {
            for (int id = 1; id <= 100; id++) {
                Profile p = profileDAO.findBySeekerID(id);
                if (p != null) profiles.add(p);
            }
        }
        return profiles;
    }

    private double[] extractFeatureVector(String text) {
        if (text == null) text = "";
        String lower = text.toLowerCase();

        int totalKeywords = 0;
        for (String[] cat : ALL_CATEGORIES) {
            totalKeywords += cat.length;
        }

        double[] vector = new double[totalKeywords];
        int idx = 0;
        for (String[] category : ALL_CATEGORIES) {
            for (String keyword : category) {
                vector[idx++] = lower.contains(keyword) ? 1.0 : 0.0;
            }
        }
        return vector;
    }

    private double computeCosineSimilarity(Profile profile, Offering offering) {
        String profileText = buildProfileText(profile);
        String offeringText = buildOfferingText(offering);

        double[] vecA = extractFeatureVector(profileText);
        double[] vecB = extractFeatureVector(offeringText);

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vecA.length; i++) {
            dotProduct += vecA[i] * vecB[i];
            normA += vecA[i] * vecA[i];
            normB += vecB[i] * vecB[i];
        }

        if (normA == 0.0 || normB == 0.0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private String buildProfileText(Profile profile) {
        StringBuilder sb = new StringBuilder();
        if (profile.getSkills() != null) sb.append(profile.getSkills()).append(" ");
        if (profile.getEducation() != null) sb.append(profile.getEducation()).append(" ");
        if (profile.getExperience() != null) sb.append(profile.getExperience()).append(" ");
        if (profile.getCvText() != null) sb.append(profile.getCvText()).append(" ");
        if (profile.getLocation() != null) sb.append(profile.getLocation());
        return sb.toString();
    }

    private String buildOfferingText(Offering offering) {
        StringBuilder sb = new StringBuilder();
        if (offering.getTitle() != null) sb.append(offering.getTitle()).append(" ");
        if (offering.getRole() != null) sb.append(offering.getRole()).append(" ");
        if (offering.getJobDescription() != null) sb.append(offering.getJobDescription()).append(" ");
        if (offering.getLocation() != null) sb.append(offering.getLocation()).append(" ");
        if (offering.getRequiredExperience() != null) sb.append(offering.getRequiredExperience()).append(" ");
        if (offering.getOfferingType() != null) sb.append(offering.getOfferingType());
        return sb.toString();
    }

    public double computeMatchScore(Profile profile, Offering offering) {
        if (profile == null || offering == null) return 0.0;

        double cgpaRatio = computeCGPARatio(profile, offering);
        double skillOverlap = computeSkillOverlap(profile, offering);
        double experienceMatch = computeExperienceMatch(profile, offering);
        double locationMatch = computeLocationMatch(profile, offering);
        double cosine = computeCosineSimilarity(profile, offering);

        if (modelTrained && smileModel != null) {
            try {
                double[] features = {cgpaRatio, skillOverlap, experienceMatch, locationMatch, cosine};
                double predicted = smileModel.predict(features);
                predicted = Math.max(0.0, Math.min(1.0, predicted));
                return Math.round(predicted * 100.0) / 100.0;
            } catch (Exception e) {
                System.err.println("Smile prediction error: " + e.getMessage());
            }
        }

        return Math.round(((cgpaRatio * 0.20) + (skillOverlap * 0.30) +
                (experienceMatch * 0.15) + (locationMatch * 0.10) +
                (cosine * 0.25)) * 100.0) / 100.0;
    }

    private double computeCGPARatio(Profile profile, Offering offering) {
        if (offering.getRequiredCGPA() != null && offering.getRequiredCGPA() > 0 && profile.getCGPA() > 0) {
            return Math.min(profile.getCGPA() / offering.getRequiredCGPA(), 1.0);
        }
        return 1.0;
    }

    private double computeSkillOverlap(Profile profile, Offering offering) {
        if (profile.getSkills() == null || offering.getJobDescription() == null) return 0.0;
        List<String> profileSkills = Arrays.stream(profile.getSkills().toLowerCase().split("[,;\\s]+"))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        String combined = offering.getJobDescription().toLowerCase() + " " +
                (offering.getRole() != null ? offering.getRole().toLowerCase() : "");
        if (profileSkills.isEmpty()) return 0.0;
        long matched = profileSkills.stream().filter(combined::contains).count();
        return (double) matched / profileSkills.size();
    }

    private double computeExperienceMatch(Profile profile, Offering offering) {
        if (profile.getExperience() == null || offering.getRequiredExperience() == null) return 1.0;
        String profExp = profile.getExperience().toLowerCase();
        String reqExp = offering.getRequiredExperience().toLowerCase();
        if (profExp.contains(reqExp) || reqExp.contains("fresh") || reqExp.contains("0")) return 1.0;
        return 0.5;
    }

    private double computeLocationMatch(Profile profile, Offering offering) {
        if (profile.getLocation() == null || offering.getLocation() == null) return 0.0;
        return profile.getLocation().toLowerCase()
                .contains(offering.getLocation().toLowerCase()) ? 1.0 : 0.0;
    }

    public List<String> identifySkillGaps(Profile profile, Offering offering) {
        List<String> gaps = new ArrayList<>();
        if (profile == null || offering == null) return gaps;

        String combined = (offering.getJobDescription() != null ? offering.getJobDescription().toLowerCase() : "") +
                " " + (offering.getRole() != null ? offering.getRole().toLowerCase() : "");

        List<String> allKeywords = new ArrayList<>();
        for (String[] category : ALL_CATEGORIES) {
            Collections.addAll(allKeywords, category);
        }

        List<String> profileSkills = profile.getSkills() != null ?
                Arrays.stream(profile.getSkills().toLowerCase().split("[,;\\s]+"))
                        .filter(s -> !s.isEmpty()).collect(Collectors.toList()) : new ArrayList<>();

        String profileText = buildProfileText(profile).toLowerCase();

        for (String keyword : allKeywords) {
            if (combined.contains(keyword) && !profileText.contains(keyword)) {
                gaps.add(keyword);
            }
        }

        if (offering.getRequiredCGPA() != null && offering.getRequiredCGPA() > 0 && profile.getCGPA() < offering.getRequiredCGPA()) {
            gaps.add("CGPA requirement: " + offering.getRequiredCGPA() + " (yours: " + profile.getCGPA() + ")");
        }

        return gaps;
    }

    public List<Offering> rankOfferingsForSeeker(int seekerID, List<Offering> offerings) {
        Profile profile = profileDAO.findBySeekerID(seekerID);
        if (profile == null) return offerings;

        for (Offering offering : offerings) {
            offering.setMatchScore(computeMatchScore(profile, offering));
        }

        List<Object> items = new ArrayList<>(offerings);
        List<Object> ranked = offeringRanker.rank(items);

        List<Offering> result = new ArrayList<>();
        for (Object obj : ranked) {
            if (obj instanceof Offering) result.add((Offering) obj);
        }
        return result;
    }

    public List<Application> rankApplicantsForOffering(int offeringID) {
        List<Application> applications = applicationDAO.getByOffering(offeringID);

        for (Application app : applications) {
            Profile profile = profileDAO.findBySeekerID(app.getSeekerID());
            app.setProfile(profile);
        }

        List<Object> items = new ArrayList<>(applications);
        List<Object> ranked = applicantRanker.rank(items);

        List<Application> result = new ArrayList<>();
        for (Object obj : ranked) {
            if (obj instanceof Application) result.add((Application) obj);
        }
        return result;
    }
}
