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
                ps.setInt(3, user1Id);
            ps.executeUpdate();
            System.out.println("BattleRepository: Battle zwischen User " + user1Id + " und User " + user2Id + " gespeichert. Gewinner: " + winner);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
