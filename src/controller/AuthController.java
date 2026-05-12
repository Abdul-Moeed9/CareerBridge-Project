package controller;

import dao.CompanyDAO;
import dao.JobSeekerDAO;
import dao.UserDAO;
import domain.Company;
import domain.JobSeeker;
import domain.User;
import utility.SessionManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthController {

    private static volatile AuthController instance;
    private UserDAO userDAO;

    private AuthController() {
        this.userDAO = new UserDAO();
    }

    public static AuthController getInstance() {
        if (instance == null) {
            synchronized (AuthController.class) {
                if (instance == null) {
                    instance = new AuthController();
                }
            }
        }
        return instance;
    }

public User login(String email, String password) {
    User user = userDAO.findByEmail(email);
    if (user == null) return null;
    if (!"active".equals(user.getAccountStatus())) return null;
    String hashed = hashPassword(password);
    if (!hashed.equals(user.getPassword())) return null;
    if (user instanceof Company) {
        CompanyDAO companyDAO = new CompanyDAO();
        Company fullCompany = (Company) companyDAO.findByID(user.getUserID());
        if (fullCompany == null || !"approved".equals(fullCompany.getApprovalStatus())) return null;
        user = fullCompany;
    } else if (user instanceof JobSeeker) {
        JobSeekerDAO seekerDAO = new JobSeekerDAO();
        JobSeeker fullSeeker = (JobSeeker) seekerDAO.findByID(user.getUserID());
        if (fullSeeker != null) user = fullSeeker;
    }
    SessionManager.getInstance().setCurrentUser(user);
    return user;
}

    public boolean register(String name, String email, String password, String role) {
        User existing = userDAO.findByEmail(email);
        if (existing != null) return false;

        String hashed = hashPassword(password);

        if ("job_seeker".equals(role)) {
            JobSeeker seeker = new JobSeeker(0, name, email, hashed);
            boolean saved = userDAO.save(seeker);
            if (saved) {
                User created = userDAO.findByEmail(email);
                if (created != null) {
                    JobSeeker js = new JobSeeker(created.getUserID(), name, email, hashed);
                    new JobSeekerDAO().save(js);
                }
            }
            return saved;
        } else if ("company".equals(role)) {
            Company company = new Company(0, name, email, hashed, name, "", email);
            
            boolean saved = userDAO.save(company);
            if (saved) {
                User created = userDAO.findByEmail(email);
                if (created != null) {
                    Company c = new Company(created.getUserID(), name, email, hashed, name, "", email);
                    
                    new CompanyDAO().save(c);
                }
            }
            return saved;
        }

        return false;
    }

    public void logout() {
        SessionManager.getInstance().clearSession();
    }

    public boolean deactivateAccount(int userID) {
        return userDAO.updateStatus(userID, "deactivated");
    }

    public User getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}