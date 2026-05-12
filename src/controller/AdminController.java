package controller;

import dao.CompanyDAO;
import dao.TrendingSkillDAO;
import dao.UserDAO;
import domain.Company;
import domain.TrendingSkill;
import domain.User;
import service.TrendingSkillService;

import java.util.List;

public class AdminController {

    private UserDAO userDAO;
    private CompanyDAO companyDAO;
    private TrendingSkillDAO trendingSkillDAO;
    private TrendingSkillService trendingSkillService;

    public AdminController() {
        this.userDAO = new UserDAO();
        this.companyDAO = new CompanyDAO();
        this.trendingSkillDAO = new TrendingSkillDAO();
        this.trendingSkillService = new TrendingSkillService();
    }

    public List<Company> getPendingCompanies() {
        return companyDAO.getPendingCompanies();
    }

    public boolean approveCompany(int companyID) {
        return companyDAO.updateApprovalStatus(companyID, "approved");
    }

    public boolean rejectCompany(int companyID) {
        return companyDAO.updateApprovalStatus(companyID, "rejected");
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public boolean deactivateUser(int userID) {
        return userDAO.updateStatus(userID, "deactivated");
    }

    public List<TrendingSkill> getTrendingSkills() {
        return refreshTrendingSkills();
    }

    public List<TrendingSkill> refreshTrendingSkills() {
        List<TrendingSkill> skills = trendingSkillService.computeTrendingSkills();
        trendingSkillService.saveComputedSkills(skills);
        return skills;
    }
}