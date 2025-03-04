package mtcg.repository;

import mtcg.model.Card;
import mtcg.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardRepository {
    public void addCard(Card card) {
        String sql = "INSERT INTO cards (user_id, name, element_type, damage) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, card.getUserId());
            ps.setString(2, card.getName());
            ps.setString(3, card.getElementType());
            ps.setInt(4, card.getDamage());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next())
                card.setId(rs.getInt(1));
            System.out.println("CardRepository: Karte " + card.getName() + " hinzugefügt für UserID " + card.getUserId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Card> getCardsByUserId(int userId) {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT id, user_id, name, element_type, damage FROM cards WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                cards.add(new Card(rs.getInt("id"), rs.getInt("user_id"), rs.getString("name"), rs.getString("element_type"), rs.getInt("damage")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public Card getCardById(int id) {
        String sql = "SELECT id, user_id, name, element_type, damage FROM cards WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return new Card(rs.getInt("id"), rs.getInt("user_id"), rs.getString("name"), rs.getString("element_type"), rs.getInt("damage"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
