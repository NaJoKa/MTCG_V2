package mtcg.repository;

import mtcg.database.DatabaseConnection;
import java.sql.*;

public class BattleRepository {
    public void saveBattle(int user1Id, int user2Id, String winner) {
        String sql = "INSERT INTO battles (user1_id, user2_id, winner_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user1Id);
            ps.setInt(2, user2Id);
            if ("Unentschieden".equals(winner))
                ps.setNull(3, java.sql.Types.INTEGER);
            else
                // Vereinfachung: Setze Gewinner als user1Id, wenn nicht anders ermittelt
                ps.setInt(3, user1Id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
