package dao;

import domain.JobSeeker;
import interfaces.IUserDAO;
import utility.DatabaseConnection;

import java.sql.*;

public class JobSeekerDAO implements IUserDAO {

    private DatabaseConnection dbConnection;

    public JobSeekerDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Object findByID(int id) {
        String sql = "SELECT js.seeker_id, js.profile_status, u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.user_id WHERE u.user_id = ?";
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
        if (!(obj instanceof JobSeeker)) return false;
        JobSeeker seeker = (JobSeeker) obj;
        String sql = "INSERT INTO job_seekers (user_id, profile_status) VALUES (?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, seeker.getUserID());
            stmt.setString(2, seeker.getProfileStatus());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        seeker.setSeekerID(keys.getInt(1));
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
        if (!(obj instanceof JobSeeker)) return false;
        JobSeeker seeker = (JobSeeker) obj;
        String sql = "UPDATE job_seekers SET profile_status = ? WHERE seeker_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, seeker.getProfileStatus());
            stmt.setInt(2, seeker.getSeekerID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM job_seekers WHERE seeker_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public JobSeeker findBySeekerID(int seekerID) {
        String sql = "SELECT js.seeker_id, js.profile_status, u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.user_id WHERE js.seeker_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, seekerID);
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

    public JobSeeker findByEmail(String email) {
        String sql = "SELECT js.seeker_id, js.profile_status, u.user_id, u.name, u.email, u.password_hash, u.account_status " +
                     "FROM job_seekers js JOIN users u ON js.user_id = u.user_id WHERE u.email = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
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

    private JobSeeker mapResultSet(ResultSet rs) throws SQLException {
        JobSeeker seeker = new JobSeeker(
            rs.getInt("user_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password_hash")
        );
        seeker.setSeekerID(rs.getInt("seeker_id"));
        seeker.setProfileStatus(rs.getString("profile_status"));
        seeker.setAccountStatus(rs.getString("account_status"));
        return seeker;
    }
}