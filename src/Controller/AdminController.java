package controller;

import dao.AccountDAO;
import dao.InterestPaymentDAO;
import dao.UserDAO;
import model.Account;
import model.Bank;
import model.Customer;
import model.InterestBearing;
import model.InterestPayment;
import model.Role;
import model.User;
import view.AdminView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Connects the administrator screen: monthly interest run and user management. */
public class AdminController {
    private final AdminView view;
    private final Bank bank;
    private final UserDAO userDAO = new UserDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final InterestPaymentDAO paymentDAO = new InterestPaymentDAO();

    public AdminController(AdminView view, Bank bank) {
        this.view = view;
        this.bank = bank;
        view.setOnRunInterest(e -> handleRunInterest());
        view.setOnUnlock(e -> handleUnlock());
        view.setOnDeactivate(e -> handleDeactivate());
        view.setOnRefresh(e -> refreshUsers());
        view.setOnAddUser(e -> handleAddUser());
        refreshUsers();
    }

    // ---------- monthly interest ----------
    private void handleRunInterest() {
        try {
            InterestPayment run = bank.runMonthlyInterest();
            for (Customer c : bank.getCustomers()) {
                for (Account a : c.getAccounts()) {
                    if (a instanceof InterestBearing) accountDAO.update(a);   // save new balance + interest transaction
                }
            }
            paymentDAO.save(run);
            view.showOutput(run.toString()
                    + "\nSavings pays 0.05% and Investment pays 5% of the balance. Cheque accounts earn no interest."
                    + (run.getFailureReason() == null || run.getFailureReason().isEmpty()
                        ? "" : "\nFailure: " + run.getFailureReason()));
            view.showMessage("Monthly interest run " + run.getStatus() + ".");
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }

    // ---------- users ----------
    private void handleAddUser() {
        String username = view.getNewUsername().trim();
        String password = view.getNewPassword();
        if (username.isEmpty() || password.length() < 6) {
            view.showMessage("Enter a username and a password of at least 6 characters.");
            return;
        }
        try {
            if (userDAO.findByUsername(username) != null) {
                view.showMessage("That username is already taken.");
                return;
            }
            userDAO.save(new User(username, password, Role.valueOf(view.getNewRole())));
            view.showMessage("User '" + username + "' added.");
            view.clearNewUserForm();
            refreshUsers();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }

    private void handleUnlock() {
        User user = selectedUser();
        if (user == null) return;
        try {
            user.resetFailedAttempts();
            userDAO.update(user);
            view.showMessage("User '" + user.getUserName() + "' unlocked.");
            refreshUsers();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }

    private void handleDeactivate() {
        User user = selectedUser();
        if (user == null) return;
        try {
            user.deactivate();
            userDAO.update(user);
            view.showMessage("User '" + user.getUserName() + "' deactivated.");
            refreshUsers();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }

    private User selectedUser() {
        String line = view.getSelectedUserLine();
        if (line == null) {
            view.showMessage("Select a user in the list first.");
            return null;
        }
        String userId = line.split(" ")[0];
        try {
            User user = userDAO.findById(userId);
            if (user == null) view.showMessage("User not found.");
            return user;
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
            return null;
        }
    }

    private void refreshUsers() {
        try {
            List<String> lines = new ArrayList<>();
            for (User u : userDAO.findAll()) {
                lines.add(u.getUserID() + " | " + u.getUserName() + " | " + u.getRole()
                        + (u.isActive() ? "" : " | DEACTIVATED")
                        + (u.isLocked() ? " | LOCKED" : ""));
            }
            view.setUsers(lines);
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }
}
