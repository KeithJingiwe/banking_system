package dao;

import model.IdGenerator;
import model.Role;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // CREATE
    public void save(User u) throws SQLException {
        try (Connection c = Database.connect()) { save(u, c); }
    }

    public void save(User u, Connection c) throws SQLException {
        String sql = "INSERT INTO users (user_id, username, password_hash, user_role, address, phone, "
                + "email, failed_attempts, locked, active) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getUserID());
            ps.setString(2, u.getUserName());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getRole().name());
            ps.setString(5, u.getAddress());
            ps.setString(6, u.getPhone());
            ps.setString(7, u.getEmail());
            ps.setInt(8, u.getFailedAttempts());
            ps.setBoolean(9, u.isLocked());
            ps.setBoolean(10, u.isActive());
            ps.executeUpdate();
        }
    }

    // READ
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER(?)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findById(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM users ORDER BY user_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(map(rs));
        }
        return users;
    }

    // UPDATE
    public void update(User u) throws SQLException {
        try (Connection c = Database.connect()) { update(u, c); }
    }

    public void update(User u, Connection c) throws SQLException {
        String sql = "UPDATE users SET address = ?, phone = ?, email = ?, failed_attempts = ?, "
                + "locked = ?, active = ? WHERE user_id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, u.getAddress());
            ps.setString(2, u.getPhone());
            ps.setString(3, u.getEmail());
            ps.setInt(4, u.getFailedAttempts());
            ps.setBoolean(5, u.isLocked());
            ps.setBoolean(6, u.isActive());
            ps.setString(7, u.getUserID());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(String userId) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
            ps.setString(1, userId);
            ps.executeUpdate();
        }
    }

    // Turns one database row into a User object
    private User map(ResultSet rs) throws SQLException {
        String id = rs.getString("user_id");
        IdGenerator.syncFrom(id);
        return new User(
                id,
                rs.getString("username"),
                rs.getString("password_hash"),
                Role.valueOf(rs.getString("user_role")),
                rs.getString("address"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getInt("failed_attempts"),
                rs.getBoolean("locked"),
                rs.getBoolean("active"));
    }
}