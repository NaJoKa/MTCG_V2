package mtcg.repository;

import mtcg.database.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TradeRepository {
    public boolean createTrade(int sellerId, int cardId, int minDamage) {
        String sql = "INSERT INTO trades (seller_id, card_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sellerId);
            ps.setInt(2, cardId);
            ps.executeUpdate();
            System.out.println("TradeRepository: Trade erstellt für SellerID " + sellerId + " mit CardID " + cardId);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getAllTrades() {
        List<String> trades = new ArrayList<>();
        String sql = "SELECT id, seller_id, card_id FROM trades";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String trade = "TradeID: " + rs.getInt("id") + ", SellerID: " + rs.getInt("seller_id") + ", CardID: " + rs.getInt("card_id");
                trades.add(trade);
            }
            System.out.println("TradeRepository: Alle Trades abgerufen.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trades;
    }

    public boolean deleteTrade(int tradeId, int userId) {
        String sql = "DELETE FROM trades WHERE id = ? AND seller_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tradeId);
            ps.setInt(2, userId);
            int affected = ps.executeUpdate();
            System.out.println("TradeRepository: Trade " + tradeId + " gelöscht.");
            return affected > 0;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean executeTrade(int tradeId, int buyerId, int offeredCardId) {
        String selectSql = "SELECT seller_id FROM trades WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, tradeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int sellerId = rs.getInt("seller_id");
                if (sellerId == buyerId){
                    System.out.println("TradeRepository: Handel mit sich selbst nicht erlaubt.");
                    return false;
                }
            } else return false;
        } catch(SQLException e) {
            e.printStackTrace();
            return false;
        }
        return deleteTrade(tradeId, buyerId) || true; // Vereinfachte Umsetzung
    }
}
