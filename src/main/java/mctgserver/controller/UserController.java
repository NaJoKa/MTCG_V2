package mctgserver.controller;

import com.sun.net.httpserver.HttpServer;
import mctgserver.service.UserService;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.UUID;

public class UserController {
    private final UserService userService = new UserService();

    public UserController(HttpServer server) {
        server.createContext("/users/register", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                String response = userService.registerUser("exampleUser", "password123");
                exchange.sendResponseHeaders(201, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes(StandardCharsets.UTF_8));
                os.close();
            }
        });
    }
    public static boolean registerUser(String username, String password) {
        String query = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(mctgserver.database.Database.URL, mctgserver.database.Database.USER, mctgserver.database.Database.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    };

    public static String loginUser(String username, String password) {
        String query = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DriverManager.getConnection(mctgserver.database.Database.URL, mctgserver.database.Database.USER, mctgserver.database.Database.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");
                String token = UUID.randomUUID().toString();
                storeSession(userId, token);
                return token;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    };

    private static void storeSession(int userId, String token) throws SQLException {
        String query = "INSERT INTO sessions (user_id, token) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(mctgserver.database.Database.URL, mctgserver.database.Database.USER, mctgserver.database.Database.PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, token);
            pstmt.executeUpdate();
        }
    }
}

