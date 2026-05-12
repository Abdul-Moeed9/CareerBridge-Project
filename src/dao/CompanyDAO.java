package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import domain.Company;
import interfaces.IUserDAO;
import utility.DatabaseConnection;

public class CompanyDAO implements IUserDAO {

    private DatabaseConnection dbConnection;

    public CompanyDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Object findByID(int id) {
        String sql = "SELECT c.company_id, c.company_name, c.industry, c.contact_email, c.approval_status, " +
                     "u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM companies c JOIN users u ON c.user_id = u.user_id WHERE u.user_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean save(Object obj) {
        if (!(obj instanceof Company)) return false;
        Company company = (Company) obj;
        String sql = "INSERT INTO companies (user_id, company_name, industry, contact_email, approval_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, company.getUserID());
            stmt.setString(2, company.getCompanyName());
            stmt.setString(3, company.getIndustry());
            stmt.setString(4, company.getContactEmail());
            stmt.setString(5, company.getApprovalStatus());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        company.setCompanyID(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Object obj) {
        if (!(obj instanceof Company)) return false;
        Company company = (Company) obj;
        String sql = "UPDATE companies SET company_name = ?, industry = ?, contact_email = ?, approval_status = ? WHERE company_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, company.getCompanyName());
            stmt.setString(2, company.getIndustry());
            stmt.setString(3, company.getContactEmail());
            stmt.setString(4, company.getApprovalStatus());
            stmt.setInt(5, company.getCompanyID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM companies WHERE company_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Company> getPendingCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT c.company_id, c.company_name, c.industry, c.contact_email, c.approval_status, " +
                     "u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM companies c JOIN users u ON c.user_id = u.user_id WHERE c.approval_status = 'pending'";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    public boolean updateApprovalStatus(int companyID, String status) {
        String sql = "UPDATE companies SET approval_status = ? WHERE company_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, companyID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String sql = "SELECT c.company_id, c.company_name, c.industry, c.contact_email, c.approval_status, " +
                     "u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM companies c JOIN users u ON c.user_id = u.user_id";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies;
    }

    private Company mapResultSet(ResultSet rs) throws SQLException {
        Company company = new Company(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("company_name"),
            rs.getString("industry"),
            rs.getString("contact_email")
        );
        company.setCompanyID(rs.getInt("company_id"));
        company.setApprovalStatus(rs.getString("approval_status"));
        company.setAccountStatus(rs.getString("account_status"));
        return company;
    }
}