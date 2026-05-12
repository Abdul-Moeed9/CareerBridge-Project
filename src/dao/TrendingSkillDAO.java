package dao;

import domain.TrendingSkill;
import utility.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class TrendingSkillDAO {

    private DatabaseConnection dbConnection;

    public TrendingSkillDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    public List<TrendingSkill> getAll() {
        List<TrendingSkill> skills = new ArrayList<>();
        String sql = "SELECT * FROM trending_skills ORDER BY frequency DESC";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    skills.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skills;
    }

    public List<TrendingSkill> getTopN(int n) {
        List<TrendingSkill> skills = new ArrayList<>();
        String sql = "SELECT * FROM trending_skills ORDER BY frequency DESC LIMIT ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, n);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    skills.add(mapResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return skills;
    }

    public boolean save(TrendingSkill skill) {
        String sql = "INSERT INTO trending_skills (skill_name, frequency, calculated_date) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, skill.getSkillName());
            stmt.setInt(2, skill.getFrequency());
            stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        skill.setTrendID(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void clearAll() {
        String sql = "DELETE FROM trending_skills";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TrendingSkill findByName(String skillName) {
        String sql = "SELECT * FROM trending_skills WHERE skill_name = ?";
        try (PreparedStatement stmt = dbConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, skillName);
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

    private TrendingSkill mapResultSet(ResultSet rs) throws SQLException {
        TrendingSkill skill = new TrendingSkill();
        skill.setTrendID(rs.getInt("trend_id"));
        skill.setSkillName(rs.getString("skill_name"));
        skill.setFrequency(rs.getInt("frequency"));
        skill.setCalculatedDate(new Date(rs.getTimestamp("calculated_date").getTime()));
        return skill;
    }
}