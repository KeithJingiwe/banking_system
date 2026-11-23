package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String URL = "jdbc:h2:./data/bank";

    private Database() { }

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, "sa", "");
    }
}