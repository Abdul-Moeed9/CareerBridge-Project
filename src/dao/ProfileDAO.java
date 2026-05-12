package dao;

import domain.Profile;
import interfaces.IUserDAO;
import java.sql.*;
import utility.DatabaseConnection;

public class ProfileDAO implements IUserDAO {

    private DatabaseConnection dbConnection;

    public ProfileDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Object findByID(int id) {
        String sql = "SELECT * FROM profiles WHERE profile_id = ?";
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
        if (!(obj instanceof Profile)) return false;
        Profile profile = (Profile) obj;
        String sql = "INSERT INTO profiles (education, cgpa, skills, experience, cv_file, profile_created_date, seeker_id, cv_text, location) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, profile.getEducation());
            stmt.setDouble(2, profile.getCGPA());
            stmt.setString(3, profile.getSkills());
            stmt.setString(4, profile.getExperience());
            stmt.setString(5, profile.getCVFile());
            stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(7, profile.getSeekerID());
            stmt.setString(8, profile.getCvText());
            stmt.setString(9, profile.getLocation());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        profile.setProfileID(keys.getInt(1));
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
        if (!(obj instanceof Profile)) return false;
        Profile profile = (Profile) obj;
        String sql = "UPDATE profiles SET education = ?, cgpa = ?, skills = ?, experience = ?, cv_file = ?, cv_text = ?, location = ? WHERE profile_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, profile.getEducation());
            stmt.setDouble(2, profile.getCGPA());
            stmt.setString(3, profile.getSkills());
            stmt.setString(4, profile.getExperience());
            stmt.setString(5, profile.getCVFile());
            stmt.setString(6, profile.getCvText());
            stmt.setString(7, profile.getLocation());
            stmt.setInt(8, profile.getProfileID());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM profiles WHERE profile_id = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Profile findBySeekerID(int seekerID) {
        String sql = "SELECT * FROM profiles WHERE seeker_id = ?";
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

    private Profile mapResultSet(ResultSet rs) throws SQLException {
        Profile profile = new Profile();
        profile.setProfileID(rs.getInt("profile_id"));
        profile.setEducation(rs.getString("education"));
        profile.setCGPA(rs.getDouble("cgpa"));
        profile.setSkills(rs.getString("skills"));
        profile.setExperience(rs.getString("experience"));
        profile.setCVFile(rs.getString("cv_file"));
        profile.setProfileCreatedDate(new Date(rs.getTimestamp("profile_created_date").getTime()));
        profile.setSeekerID(rs.getInt("seeker_id"));
        try {
            profile.setCvText(rs.getString("cv_text"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            profile.setLocation(rs.getString("location"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return profile;
    }
}