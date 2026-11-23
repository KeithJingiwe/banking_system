package dao;

import model.*;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private static final String SELECT =
            "SELECT u.*, c.customer_id, c.customer_type, c.first_name, c.surname, c.omang_number, "
                    + "c.date_of_birth, c.employed, c.company_name, c.registration_number, c.contact_person "
                    + "FROM customers c JOIN users u ON u.user_id = c.user_id ";

    private final UserDAO userDAO = new UserDAO();

    // CREATE: both rows in ONE transaction
    public void save(Customer cust) throws SQLException {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try {
                userDAO.save(cust, c);
                insertCustomerRow(cust, c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private void insertCustomerRow(Customer cust, Connection c) throws SQLException {
        String sql = "INSERT INTO customers (customer_id, user_id, customer_type, first_name, surname, "
                + "omang_number, date_of_birth, employed, company_name, registration_number, contact_person) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cust.getCustomerId());
            ps.setString(2, cust.getUserID());
            if (cust instanceof IndividualCustomer ind) {
                ps.setString(3, "INDIVIDUAL");
                ps.setString(4, ind.getFirstName());
                ps.setString(5, ind.getSurname());
                ps.setString(6, ind.getOmangNumber());
                ps.setDate(7, Date.valueOf(ind.getDateOfBirth()));
                ps.setBoolean(8, ind.isEmployed());
                ps.setNull(9, Types.VARCHAR);
                ps.setNull(10, Types.VARCHAR);
                ps.setNull(11, Types.VARCHAR);
            } else if (cust instanceof CompanyCustomer co) {
                ps.setString(3, "COMPANY");
                ps.setNull(4, Types.VARCHAR);
                ps.setNull(5, Types.VARCHAR);
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.DATE);
                ps.setNull(8, Types.BOOLEAN);
                ps.setString(9, co.getCompanyName());
                ps.setString(10, co.getRegistrationNumber());
                ps.setString(11, co.getContactPerson());
            } else {
                throw new IllegalArgumentException("Unknown customer type: " + cust.getClass());
            }
            ps.executeUpdate();
        }
    }

    // READ
    public Customer findById(String customerId) throws SQLException {
        return findOne(SELECT + "WHERE c.customer_id = ?", customerId);
    }

    public Customer findByUsername(String username) throws SQLException {
        return findOne(SELECT + "WHERE LOWER(u.username) = LOWER(?)", username);
    }

    private Customer findOne(String sql, String value) throws SQLException {
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Customer> findAll() throws SQLException {
        List<Customer> list = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY c.customer_id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // UPDATE: contact details and lock state (users row) plus the customer's own fields
    public void update(Customer cust) throws SQLException {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try {
                userDAO.update(cust, c);
                if (cust instanceof IndividualCustomer ind) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE customers SET first_name = ?, surname = ?, employed = ? WHERE customer_id = ?")) {
                        ps.setString(1, ind.getFirstName());
                        ps.setString(2, ind.getSurname());
                        ps.setBoolean(3, ind.isEmployed());
                        ps.setString(4, ind.getCustomerId());
                        ps.executeUpdate();
                    }
                } else if (cust instanceof CompanyCustomer co) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE customers SET contact_person = ? WHERE customer_id = ?")) {
                        ps.setString(1, co.getContactPerson());
                        ps.setString(2, co.getCustomerId());
                        ps.executeUpdate();
                    }
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // DELETE: fails (and rolls back) if the customer still has accounts
    public void delete(String customerId) throws SQLException {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try {
                String userId;
                try (PreparedStatement ps = c.prepareStatement(
                        "SELECT user_id FROM customers WHERE customer_id = ?")) {
                    ps.setString(1, customerId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) return;
                        userId = rs.getString("user_id");
                    }
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
                    ps.setString(1, customerId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                    ps.setString(1, userId);
                    ps.executeUpdate();
                }
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // Turns one joined row into the right kind of Customer
    private Customer map(ResultSet rs) throws SQLException {
        String customerId = rs.getString("customer_id");
        String userId = rs.getString("user_id");
        IdGenerator.syncFrom(customerId);
        IdGenerator.syncFrom(userId);

        String userName = rs.getString("username");
        String hash = rs.getString("password_hash");
        String address = rs.getString("address");
        String phone = rs.getString("phone");
        String email = rs.getString("email");
        int failed = rs.getInt("failed_attempts");
        boolean locked = rs.getBoolean("locked");
        boolean active = rs.getBoolean("active");

        if ("INDIVIDUAL".equals(rs.getString("customer_type"))) {
            return new IndividualCustomer(customerId, userId, userName, hash,
                    rs.getString("first_name"), rs.getString("surname"), rs.getString("omang_number"),
                    rs.getDate("date_of_birth").toLocalDate(), rs.getBoolean("employed"),
                    address, phone, email, failed, locked, active);
        }
        return new CompanyCustomer(customerId, userId, userName, hash,
                rs.getString("company_name"), rs.getString("registration_number"),
                rs.getString("contact_person"), address, phone, email, failed, locked, active);
    }
}