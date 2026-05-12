package domain;

import interfaces.IRankable;
import utility.CVTextExtractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicantRanker extends ModelTrainer implements IRankable {

    private double skillsWeight;
    private double cvWeight;
    private double educationWeight;
    private double experienceWeight;

    private Offering offering;

    public ApplicantRanker() {
        this.skillsWeight = 0.25;
        this.cvWeight = 0.30;
        this.educationWeight = 0.20;
        this.experienceWeight = 0.25;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
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
        List<Application> applications = new ArrayList<>();
        for (Object obj : items) {
            if (obj instanceof Application) applications.add((Application) obj);
        }
        for (Application app : applications) {
            app.setWeightedRank(computeWeightedScore(app));
        }
        List<Application> ranked = sortByDescendingRank(applications);
        return new ArrayList<>(ranked);
    }

    @Override
    public double computeScore(Object item) {
        if (item instanceof Application) return computeWeightedScore((Application) item);
        return 0.0;
    }

    @Override
    public List<Object> sortByScore(List<Object> items) {
        return rank(items);
    }

    public List<Application> rankApplicantsForOffering(Offering offering, List<Application> applications) {
        this.offering = offering;
        for (Application app : applications) {
            app.setWeightedRank(computeWeightedScore(app));
        }
        return sortByDescendingRank(applications);
    }

    private double computeWeightedScore(Application application) {
        Profile profile = application.getProfile();
        if (profile == null) return 0.0;

        double cgpaScore = computeCGPAScore(profile);
        double cvScore = computeCVScore(profile);
        double skillsScore = computeSkillsScore(profile);
        double experienceScore = computeExperienceScore(profile);

        double total = (cgpaScore * educationWeight)
                     + (cvScore * cvWeight)
                     + (skillsScore * skillsWeight)
                     + (experienceScore * experienceWeight);

        return Math.min(1.0, Math.max(0.0, total));
    }

    private double computeCGPAScore(Profile profile) {
        double applicantCGPA = profile.getCGPA();
        if (applicantCGPA <= 0) return 0.0;

        if (offering != null && offering.getRequiredCGPA() != null && offering.getRequiredCGPA() > 0) {
            double required = offering.getRequiredCGPA();
            if (applicantCGPA >= required) {
                double excess = applicantCGPA - required;
                double maxExcess = 4.0 - required;
                return maxExcess > 0 ? 0.7 + 0.3 * (excess / maxExcess) : 1.0;
            } else {
                return Math.max(0.0, applicantCGPA / required * 0.6);
            }
        }
        return applicantCGPA / 4.0;
    }

    private double computeCVScore(Profile profile) {
        String cvText = profile.getCvText();
        String cvFile = profile.getCVFile();

        if ((cvText == null || cvText.trim().isEmpty()) && cvFile != null && !cvFile.trim().isEmpty()) {
            cvText = CVTextExtractor.extract(cvFile);
            if (cvText != null && !cvText.trim().isEmpty()) {
                profile.setCvText(cvText);
            }
        }

        if (cvText == null || cvText.trim().isEmpty()) {
            return 0.0;
        }

        if (offering == null || offering.getJobDescription() == null || offering.getJobDescription().trim().isEmpty()) {
            return 0.5;
        }

        String jobDesc = offering.getJobDescription().toLowerCase();
        String cvLower = cvText.toLowerCase();

        Set<String> jobKeywords = extractKeywords(jobDesc);
        if (jobKeywords.isEmpty()) return 0.5;

        int matched = 0;
        for (String keyword : jobKeywords) {
            if (cvLower.contains(keyword)) {
                matched++;
            }
        }

        return (double) matched / jobKeywords.size();
    }

    private double computeSkillsScore(Profile profile) {
        String skills = profile.getSkills();
        if (skills == null || skills.trim().isEmpty()) return 0.0;

        Set<String> applicantSkills = new HashSet<>();
        for (String skill : skills.toLowerCase().split("[,;]+")) {
            String trimmed = skill.trim();
            if (!trimmed.isEmpty()) applicantSkills.add(trimmed);
        }

        if (applicantSkills.isEmpty()) return 0.0;

        if (offering != null && offering.getJobDescription() != null && !offering.getJobDescription().trim().isEmpty()) {
            String jobDesc = offering.getJobDescription().toLowerCase();
            String title = offering.getTitle() != null ? offering.getTitle().toLowerCase() : "";
            String role = offering.getRole() != null ? offering.getRole().toLowerCase() : "";
            String combined = jobDesc + " " + title + " " + role;

            int matched = 0;
            for (String skill : applicantSkills) {
                if (combined.contains(skill)) {
                    matched++;
                }
            }

            double overlapRatio = (double) matched / applicantSkills.size();
            double coverageBonus = Math.min(matched / 5.0, 1.0) * 0.3;
            return Math.min(1.0, overlapRatio * 0.7 + coverageBonus);
        }

        return Math.min(applicantSkills.size() / 8.0, 1.0);
    }

    private double computeExperienceScore(Profile profile) {
        String applicantExp = profile.getExperience();
        if (applicantExp == null || applicantExp.trim().isEmpty()) return 0.0;

        if (offering == null || offering.getRequiredExperience() == null || offering.getRequiredExperience().trim().isEmpty()) {
            return estimateExperienceLevel(applicantExp) > 0 ? 0.6 : 0.3;
        }

        int requiredYears = extractYears(offering.getRequiredExperience());
        int applicantYears = extractYears(applicantExp);

        if (requiredYears <= 0 && applicantYears <= 0) {
            return fuzzyExperienceMatch(applicantExp, offering.getRequiredExperience());
        }

        if (requiredYears <= 0) {
            return Math.min(applicantYears / 5.0, 1.0);
        }

        if (applicantYears >= requiredYears) {
            double excess = applicantYears - requiredYears;
            return Math.min(1.0, 0.75 + 0.25 * Math.min(excess / 3.0, 1.0));
        } else {
            return Math.max(0.1, (double) applicantYears / requiredYears * 0.7);
        }
    }

    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        Set<String> stopWords = new HashSet<>(Arrays.asList(
            "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
            "have", "has", "had", "do", "does", "did", "will", "would", "could",
            "should", "may", "might", "shall", "can", "need", "must",
            "and", "or", "but", "if", "then", "else", "when", "at", "by",
            "for", "with", "about", "against", "between", "through", "during",
            "before", "after", "above", "below", "to", "from", "up", "down",
            "in", "out", "on", "off", "over", "under", "of", "into",
            "this", "that", "these", "those", "it", "its", "we", "our", "you",
            "your", "they", "their", "he", "she", "his", "her",
            "not", "no", "nor", "so", "too", "very", "just", "also",
            "than", "such", "as", "more", "most", "other", "some", "any",
            "all", "both", "each", "every", "own", "same", "able",
            "work", "working", "experience", "required", "looking", "role",
            "candidate", "position", "job", "apply", "application"
        ));

        String[] words = text.replaceAll("[^a-zA-Z0-9#+.\\-]", " ").split("\\s+");
        for (String word : words) {
            String w = word.trim().toLowerCase();
            if (w.length() >= 2 && !stopWords.contains(w)) {
                keywords.add(w);
            }
        }

        String[] techPhrases = {
            "machine learning", "deep learning", "data science", "rest api",
            "spring boot", "node.js", "react.js", "vue.js", "next.js",
            "angular.js", "express.js", "software engineering",
            "project management", "problem solving", "team lead",
            "full stack", "front end", "back end", "ui/ux", "ci/cd",
            "version control", "agile methodology", "scrum master",
            "cloud computing", "artificial intelligence", "natural language",
            "computer vision", "big data", "data analysis", "web development",
            "mobile development", "database management", "system design"
        };
        for (String phrase : techPhrases) {
            if (text.contains(phrase)) {
                keywords.add(phrase);
            }
        }

        return keywords;
    }

    private int extractYears(String text) {
        if (text == null) return 0;
        String lower = text.toLowerCase();

        Pattern pattern = Pattern.compile("(\\d+)\\s*[-+]?\\s*(?:years?|yrs?)");
        Matcher matcher = pattern.matcher(lower);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        Pattern rangePattern = Pattern.compile("(\\d+)\\s*[-to]+\\s*(\\d+)\\s*(?:years?|yrs?)");
        Matcher rangeMatcher = rangePattern.matcher(lower);
        if (rangeMatcher.find()) {
            int low = Integer.parseInt(rangeMatcher.group(1));
            int high = Integer.parseInt(rangeMatcher.group(2));
            return (low + high) / 2;
        }

        if (lower.contains("fresh") || lower.contains("entry") || lower.contains("intern")) return 0;
        if (lower.contains("junior")) return 1;
        if (lower.contains("mid")) return 3;
        if (lower.contains("senior")) return 5;
        if (lower.contains("lead") || lower.contains("principal")) return 7;
        if (lower.contains("director") || lower.contains("manager")) return 8;

        return 0;
    }

    private double estimateExperienceLevel(String text) {
        if (text == null) return 0;
        int years = extractYears(text);
        if (years > 0) return years;
        String lower = text.toLowerCase();
        if (lower.contains("senior") || lower.contains("lead")) return 5;
        if (lower.contains("mid")) return 3;
        if (lower.contains("junior") || lower.contains("entry")) return 1;
        if (lower.contains("fresh") || lower.contains("intern")) return 0;
        return 1;
    }

    private double fuzzyExperienceMatch(String applicantExp, String requiredExp) {
        String appLower = applicantExp.toLowerCase();
        String reqLower = requiredExp.toLowerCase();

        String[] levels = {"intern", "fresh", "entry", "junior", "mid", "senior", "lead", "principal", "director"};
        int appLevel = -1, reqLevel = -1;
        for (int i = 0; i < levels.length; i++) {
            if (appLower.contains(levels[i])) appLevel = i;
            if (reqLower.contains(levels[i])) reqLevel = i;
        }

        if (appLevel >= 0 && reqLevel >= 0) {
            if (appLevel >= reqLevel) return 1.0;
            int gap = reqLevel - appLevel;
            return Math.max(0.1, 1.0 - gap * 0.2);
        }

        String[] appWords = appLower.split("\\s+");
        String[] reqWords = reqLower.split("\\s+");
        int common = 0;
        for (String aw : appWords) {
            for (String rw : reqWords) {
                if (aw.equals(rw) && aw.length() > 2) common++;
            }
        }
        return Math.min(1.0, common * 0.25 + 0.2);
    }

    private List<Application> sortByDescendingRank(List<Application> applications) {
        applications.sort(Comparator.comparingDouble(Application::getWeightedRank).reversed());
        return applications;
    }

    public double getSkillsWeight() { return skillsWeight; }
    public double getCvWeight() { return cvWeight; }
    public double getEducationWeight() { return educationWeight; }
    public double getExperienceWeight() { return experienceWeight; }
}
