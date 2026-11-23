package dao;

import model.InterestPayment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Stores the record of each monthly interest run. */
public class InterestPaymentDAO {

    public void save(InterestPayment p) throws SQLException {
        String sql = "INSERT INTO interest_payments (payment_id, run_date, status, accounts_processed, "
                + "total_paid, failure_reason) VALUES (?,?,?,?,?,?)";
        try (Connection c = Database.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getPaymentId());
            ps.setDate(2, p.getRunDate() == null ? null : Date.valueOf(p.getRunDate()));
            ps.setString(3, p.getStatus().name());
            ps.setInt(4, p.getAccountsProcessed());
            ps.setBigDecimal(5, p.getTotalPaid());
            ps.setString(6, p.getFailureReason());
            ps.executeUpdate();
        }
    }
}
