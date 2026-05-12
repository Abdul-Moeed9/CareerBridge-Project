package service;

import dao.OfferingDAO;
import dao.TrendingSkillDAO;
import domain.Offering;
import domain.TrendingSkill;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendingSkillService {

    private TrendingSkillDAO trendingSkillDAO;
    private OfferingDAO offeringDAO;

    public TrendingSkillService() {
        this.trendingSkillDAO = new TrendingSkillDAO();
        this.offeringDAO = new OfferingDAO();
    }

    public List<TrendingSkill> computeTrendingSkills() {
        List<Offering> offerings = offeringDAO.getAll();
        Map<String, Integer> skillFrequency = extractSkillsFromOfferings(offerings);

        List<TrendingSkill> skills = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : skillFrequency.entrySet()) {
            TrendingSkill skill = new TrendingSkill();
            skill.setSkillName(entry.getKey());
            skill.setFrequency(entry.getValue());
            skill.setCalculatedDate(new Date());
            skills.add(skill);
        }

        skills.sort((a, b) -> Integer.compare(b.getFrequency(), a.getFrequency()));
        return skills;
    }

    private Map<String, Integer> extractSkillsFromOfferings(List<Offering> offerings) {
        Map<String, Integer> skillFrequency = new HashMap<>();
        for (Offering offering : offerings) {
            String description = offering.getJobDescription();
            String role = offering.getRole();
            String combined = "";
            if (description != null) combined += description.toLowerCase();
            if (role != null) combined += " " + role.toLowerCase();

            String[] keywords = {
                "java", "python", "javascript", "react", "angular", "node.js",
                "spring boot", "django", "flask", "sql", "mysql", "postgresql",
                "mongodb", "docker", "kubernetes", "aws", "azure", "git",
                "machine learning", "deep learning", "data analysis", "tensorflow",
                "html", "css", "typescript", "c++", "c#", ".net", "php",
                "flutter", "swift", "kotlin", "go", "rust", "redis",
                "graphql", "rest api", "microservices", "agile", "scrum",
                "figma", "tableau", "power bi", "excel", "linux"
            };

            for (String keyword : keywords) {
                int index = 0;
                while ((index = combined.indexOf(keyword, index)) != -1) {
                    skillFrequency.merge(keyword, 1, Integer::sum);
                    index += keyword.length();
                }
            }
        }
        return skillFrequency;
    }

    public void saveComputedSkills(List<TrendingSkill> skills) {
        trendingSkillDAO.clearAll();
        for (TrendingSkill skill : skills) {
            trendingSkillDAO.save(skill);
        }
    }

    public List<TrendingSkill> getTopNSkills(int n) {
        return trendingSkillDAO.getTopN(n);
    }
}