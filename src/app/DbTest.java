package app;

import dao.Database;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTest {
    public static void main(String[] args) throws SQLException {
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT 'Database connected' AS msg")) {
            rs.next();
            System.out.println(rs.getString("msg"));
        }
    }
}