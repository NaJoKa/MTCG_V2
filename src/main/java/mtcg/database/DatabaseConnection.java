package mtcg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "mtcgdatabase";

    public static Connection getConnection() throws SQLException {
        System.out.println("DB: Connecting to database -  Versuch...");
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("DB: Verbindung hergestellt!");
        return conn;
    }
}
