package dao;

import domain.Application;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    private DatabaseConnection dbConnection;

    public ApplicationDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public Application findByID(int applicationID) {
        String sql = "SELECT * FROM applications WHERE application_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, applicationID);
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

    public List<Application> getAll() {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications ORDER BY application_date DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<Application> getByOffering(int offeringID) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE offering_id = ? ORDER BY weighted_rank DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, offeringID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<Application> getBySeeker(int seekerID) {
        List<Application> applications = new ArrayList<>();
        String sql = "SELECT * FROM applications WHERE seeker_id = ? ORDER BY application_date DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, seekerID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public boolean save(Application application) {
        String sql = "INSERT INTO applications (application_date, resume_file, status, weighted_rank, seeker_id, offering_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, new Timestamp(application.getApplicationDate().getTime()));
            stmt.setString(2, application.getResumeFile());
            stmt.setString(3, application.getStatus());
            stmt.setDouble(4, application.getWeightedRank());
            stmt.setInt(5, application.getSeekerID());
            stmt.setInt(6, application.getOfferingID());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        application.setApplicationID(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int applicationID, String status) {
        String sql = "UPDATE applications SET status = ? WHERE application_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, applicationID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateRank(int applicationID, double rank) {
        String sql = "UPDATE applications SET weighted_rank = ? WHERE application_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setDouble(1, rank);
            stmt.setInt(2, applicationID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int applicationID) {
        String sql = "DELETE FROM applications WHERE application_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, applicationID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByOffering(int offeringID) {
        String sql = "DELETE FROM applications WHERE offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, offeringID);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkDuplicate(int seekerID, int offeringID) {
        String sql = "SELECT application_id FROM applications WHERE seeker_id = ? AND offering_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, seekerID);
            stmt.setInt(2, offeringID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Application mapResultSet(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setApplicationID(rs.getInt("application_id"));
        app.setApplicationDate(rs.getTimestamp("application_date"));
        app.setResumeFile(rs.getString("resume_file"));
        app.setStatus(rs.getString("status"));
        app.setWeightedRank(rs.getDouble("weighted_rank"));
        app.setSeekerID(rs.getInt("seeker_id"));
        app.setOfferingID(rs.getInt("offering_id"));
        return app;
    }
}