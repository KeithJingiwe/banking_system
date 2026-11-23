package dao;

import model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {
    private static final String SELECT =
            "SELECT a.*, e.employer_name, e.employer_address, e.employee_number "
                    + "FROM accounts a LEFT JOIN employers e ON e.employer_id = a.employer_id ";

    private final CustomerDAO customerDAO = new CustomerDAO();


    // CREATE: employer (if cheque), account row and its transactions in ONE database transaction
    public void save(Account a) throws SQLException {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try {
                Integer employerId = null;
                if (a instanceof ChequeAccount cq) employerId = insertEmployer(cq.getEmployer(), c);
                insertAccountRow(a, employerId, c);
                saveTransactions(a, c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    private int insertEmployer(Employer e, Connection c) throws SQLException {
        String sql = "INSERT INTO employers (employer_name, employer_address, employee_number) VALUES (?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getEmployerName());
            ps.setString(2, e.getEmployerAddress());
            ps.setString(3, e.getEmployeeNumber());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        }
    }

    private void insertAccountRow(Account a, Integer employerId, Connection c) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, account_type, branch, date_opened, status, "
                + "balance, customer_id, employer_id) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, a.getAccountNumber());
            ps.setString(2, a.getAccountType().toUpperCase());
            ps.setString(3, a.getBranch());
            ps.setDate(4, java.sql.Date.valueOf(a.getDateOpened()));
            ps.setString(5, a.getStatus().name());
            ps.setBigDecimal(6, a.getBalance());
            ps.setString(7, ((Customer) a.getOwner()).getCustomerId());
            if (employerId == null) ps.setNull(8, Types.INTEGER); else ps.setInt(8, employerId);
            ps.executeUpdate();
        }
    }



    // MERGE inserts a transaction if it is new and does nothing if it is already stored
    private void saveTransactions(Account a, Connection c) throws SQLException {
        String sql = "MERGE INTO transactions (transaction_id, account_number, txn_type, amount, "
                + "txn_time, balance_after, description) KEY (transaction_id) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            for (Transaction t : a.getTransactions()) {
                ps.setString(1, t.getTransactionID());
                ps.setString(2, a.getAccountNumber());
                ps.setString(3, t.getType().name());
                ps.setBigDecimal(4, t.getAmount());
                ps.setTimestamp(5, Timestamp.valueOf(t.getTimestamp()));
                ps.setBigDecimal(6, t.getBalanceAfter());
                ps.setString(7, t.getDescription());
                ps.executeUpdate();
            }
        }
    }

    // UPDATE: new balance/status plus any new transactions, atomically
    public void update(Account a) throws SQLException {
        try (Connection c = Database.connect()) {
            c.setAutoCommit(false);
            try {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE accounts SET status = ?, balance = ? WHERE account_number = ?")) {
                    ps.setString(1, a.getStatus().name());
                    ps.setBigDecimal(2, a.getBalance());
                    ps.setString(3, a.getAccountNumber());
                    ps.executeUpdate();
                }
                saveTransactions(a, c);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    // READ
    public Account findByNumber(String accountNumber) throws SQLException {
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE a.account_number = ?")) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Customer owner = customerDAO.findById(rs.getString("customer_id"));
                return map(rs, owner, c);
            }
        }
    }

    public List<Account> findByCustomer(Customer owner) throws SQLException {
        List<Account> list = new ArrayList<>();
        try (Connection c = Database.connect();
             PreparedStatement ps = c.prepareStatement(
                     SELECT + "WHERE a.customer_id = ? ORDER BY a.account_number")) {
            ps.setString(1, owner.getCustomerId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs, owner, c));
            }
        }
        return list;
    }

    // Loads a customer's stored accounts into the customer object (once)
    public void attachAccounts(Customer owner) throws SQLException {
        if (!owner.getAccounts().isEmpty()) return;
        for (Account a : findByCustomer(owner)) owner.addAccount(a);
    }

    private List<Transaction> loadTransactions(String accountNumber, Connection c) throws SQLException {
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM transactions WHERE account_number = ? ORDER BY txn_time, transaction_id")) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(
                            rs.getString("transaction_id"),
                            TransactionType.valueOf(rs.getString("txn_type")),
                            rs.getBigDecimal("amount"),
                            rs.getTimestamp("txn_time").toLocalDateTime(),
                            rs.getBigDecimal("balance_after"),
                            rs.getString("description")));
                }
            }
        }
        return list;
    }

    // Turns one row into the right kind of Account
    private Account map(ResultSet rs, Customer owner, Connection c) throws SQLException {
        String number = rs.getString("account_number");
        String branch = rs.getString("branch");
        LocalDate opened = rs.getDate("date_opened").toLocalDate();
        AccountStatus status = AccountStatus.valueOf(rs.getString("status"));
        BigDecimal balance = rs.getBigDecimal("balance");
        List<Transaction> history = loadTransactions(number, c);

        return switch (rs.getString("account_type")) {
            case "SAVINGS" -> new SavingsAccount(number, branch, owner, opened, status, balance, history);
            case "INVESTMENT" -> new InvestmentAccount(number, branch, owner, opened, status, balance, history);
            case "CHEQUE" -> new ChequeAccount(number, branch, owner,
                    new Employer(rs.getString("employer_name"), rs.getString("employer_address"),
                            rs.getString("employee_number")),
                    opened, status, balance, history);
            default -> throw new IllegalStateException("Unknown account type in database.");
        };
    }
}