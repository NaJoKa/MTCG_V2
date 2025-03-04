package mtcg.repository;

import mtcg.model.User;
import mtcg.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public boolean userExists(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createUser(User user) {
        String sql = "INSERT INTO users (username, password, coins, elo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setInt(3, user.getCoins());
            ps.setInt(4, user.getElo());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                user.setId(rs.getInt(1));
            System.out.println("UserRepository: User " + user.getUsername() + " erstellt.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUser(String username) {
        String sql = "SELECT id, username, password, coins, elo FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getInt("coins"), rs.getInt("elo"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateUser(User user) {
        String sql = "UPDATE users SET password = ?, coins = ?, elo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getPassword());
            ps.setInt(2, user.getCoins());
            ps.setInt(3, user.getElo());
            ps.setInt(4, user.getId());
            ps.executeUpdate();
            System.out.println("UserRepository: User " + user.getUsername() + " aktualisiert.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getScoreboard() {
        List<String> scoreboard = new ArrayList<>();
        String sql = "SELECT username, elo FROM users ORDER BY elo DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                scoreboard.add(rs.getString("username") + " - ELO: " + rs.getInt("elo"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scoreboard;
    }
}
