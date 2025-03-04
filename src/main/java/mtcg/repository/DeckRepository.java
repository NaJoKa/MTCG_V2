package mtcg.repository;

import mtcg.database.DatabaseConnection;
import mtcg.model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DeckRepository {
    public void setDeck(int userId, List<Integer> cardIds) {
        String deleteSql = "DELETE FROM deck WHERE user_id = ?";
        String insertSql = "INSERT INTO deck (user_id, card_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            for (Integer cardId : cardIds) {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, cardId);
                    insertStmt.executeUpdate();
                }
            }
            System.out.println("DeckRepository: Deck für UserID " + userId + " konfiguriert.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getDeck(int userId) {
        List<Card> deck = new ArrayList<>();
        String sql = "SELECT c.id, c.user_id, c.name, c.element_type, c.damage FROM deck d JOIN cards c ON d.card_id = c.id WHERE d.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                deck.add(new Card(rs.getInt("id"), rs.getInt("user_id"), rs.getString("name"), rs.getString("element_type"), rs.getInt("damage")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deck;
    }
}
