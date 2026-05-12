package dao;

import domain.Offering;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class OfferingDAO {

    private DatabaseConnection dbConnection;

    public OfferingDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Offering findByID(int offeringID) {
        String sql = "SELECT * FROM offerings WHERE offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, offeringID);
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

    public List<Offering> getAll() {
        List<Offering> offerings = new ArrayList<>();
        String sql = "SELECT * FROM offerings ORDER BY posted_date DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    offerings.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offerings;
    }

    public List<Offering> getByCompany(int companyID) {
        List<Offering> offerings = new ArrayList<>();
        String sql = "SELECT * FROM offerings WHERE company_id = ? ORDER BY posted_date DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, companyID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    offerings.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offerings;
    }

public boolean save(Offering offering) {
    String sql = "INSERT INTO offerings (offering_type, title, role, location, required_cgpa, required_experience, " +
         "stipend, salary_text, job_description, posted_date, status, company_id, company_name, url, source, posted_at_text) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, offering.getOfferingType());
        stmt.setString(2, offering.getTitle());
        stmt.setString(3, offering.getRole());
        stmt.setString(4, offering.getLocation());
        if (offering.getRequiredCGPA() != null)
            stmt.setDouble(5, offering.getRequiredCGPA());
        else
            stmt.setNull(5, java.sql.Types.DECIMAL);
        stmt.setString(6, offering.getRequiredExperience());
        stmt.setDouble(7, offering.getStipend());
        stmt.setString(8, offering.getSalaryText());
        stmt.setString(9, offering.getJobDescription());
        stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
        stmt.setString(11, offering.getStatus());
        if (offering.getCompanyID() != null)
            stmt.setInt(12, offering.getCompanyID());
        else
            stmt.setNull(12, java.sql.Types.INTEGER);
        stmt.setString(13, offering.getCompanyName());
        stmt.setString(14, offering.getUrl());
        stmt.setString(15, offering.getSource() != null ? offering.getSource() : "scraped");
        stmt.setString(16, offering.getPostedAtText());
        int rows = stmt.executeUpdate();
        if (rows > 0) {
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    offering.setOfferingID(keys.getInt(1));
                }
            }
            return true;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    public boolean update(Offering offering) {
        String sql = "UPDATE offerings SET offering_type = ?, title = ?, role = ?, location = ?, required_cgpa = ?, " +
                     "required_experience = ?, stipend = ?, salary_text = ?, job_description = ?, status = ? WHERE offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, offering.getOfferingType());
            stmt.setString(2, offering.getTitle());
            stmt.setString(3, offering.getRole());
            stmt.setString(4, offering.getLocation());
            if (offering.getRequiredCGPA() != null)
                stmt.setDouble(5, offering.getRequiredCGPA());
            else
                stmt.setNull(5, java.sql.Types.DECIMAL);
            stmt.setString(6, offering.getRequiredExperience());
            stmt.setDouble(7, offering.getStipend());
            stmt.setString(8, offering.getSalaryText());
            stmt.setString(9, offering.getJobDescription());
            stmt.setString(10, offering.getStatus());
            stmt.setInt(11, offering.getOfferingID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int offeringID, String status) {
        String sql = "UPDATE offerings SET status = ? WHERE offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, offeringID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int offeringID) {
        String sql = "DELETE FROM offerings WHERE offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, offeringID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Offering> searchByKeyword(String keyword) {
        List<Offering> offerings = new ArrayList<>();
        String sql = "SELECT * FROM offerings WHERE title LIKE ? OR role LIKE ? OR job_description LIKE ? ORDER BY posted_date DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            stmt.setString(1, pattern);
            stmt.setString(2, pattern);
            stmt.setString(3, pattern);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    offerings.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offerings;
    }

    private Offering mapResultSet(ResultSet rs) throws SQLException {
        Offering offering = new Offering();
        offering.setOfferingID(rs.getInt("offering_id"));
        offering.setOfferingType(rs.getString("offering_type"));
        offering.setTitle(rs.getString("title"));
        offering.setRole(rs.getString("role"));
        offering.setLocation(rs.getString("location"));
        double cgpa = rs.getDouble("required_cgpa");
        offering.setRequiredCGPA(rs.wasNull() ? null : cgpa);
        offering.setRequiredExperience(rs.getString("required_experience"));
        offering.setStipend(rs.getDouble("stipend"));
        offering.setSalaryText(rs.getString("salary_text"));
        offering.setJobDescription(rs.getString("job_description"));
        offering.setPostedDate(new Date(rs.getTimestamp("posted_date").getTime()));
        offering.setStatus(rs.getString("status"));
        int cid = rs.getInt("company_id");
        offering.setCompanyID(rs.wasNull() ? null : cid);
        offering.setCompanyName(rs.getString("company_name"));
        offering.setUrl(rs.getString("url"));
        offering.setSource(rs.getString("source"));
        offering.setPostedAtText(rs.getString("posted_at_text"));
        return offering;
    }
}