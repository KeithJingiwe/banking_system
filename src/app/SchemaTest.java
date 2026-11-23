package app;

import dao.Database;
import dao.Schema;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaTest {
    public static void main(String[] args) throws SQLException {
        Schema.create();
        try (Connection c = Database.connect();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(
                     "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
                             "WHERE TABLE_SCHEMA = 'PUBLIC' ORDER BY TABLE_NAME")) {
            while (rs.next()) {
                System.out.println(rs.getString("TABLE_NAME"));
            }
        }
    }
}