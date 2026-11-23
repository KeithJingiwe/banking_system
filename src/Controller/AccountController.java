package Controller;

import dao.AccountDAO;
import model.Account;
import model.Bank;
import model.Transaction;
import view.AccountView;

import java.math.BigDecimal;
import java.sql.SQLException;

public class AccountController {
    private final AccountView view;
    private final Bank bank;
    private final AccountDAO accountDAO = new AccountDAO();
    private Account current;

    public AccountController(AccountView view, Bank bank) {
        this.view = view;
        this.bank = bank;
        view.setOnFind(e -> handleFind());
        view.setOnDeposit(e -> handleDeposit());
        view.setOnWithdraw(e -> handleWithdraw());
    }

    private void handleFind() {
        Account found = bank.findAccount(view.getAccountNumber().trim());
        if (found == null) {
            current = null;
            view.showDetails("No account selected");
            view.showHistory("");
            view.showMessage("Account not found.");
            return;
        }
        current = found;
        view.showMessage("");
        refresh();
    }

    private void handleDeposit() {
        BigDecimal amount = readAmount();
        if (amount == null) return;
        if (current.deposit(amount)) {
            persist("Deposit successful.");
        } else {
            view.showMessage(current.getLastRejectionReason());
        }
    }

    private void handleWithdraw() {
        BigDecimal amount = readAmount();
        if (amount == null) return;
        if (current.withdraw(amount)) {
            persist("Withdrawal successful.");
        } else {
            view.showMessage(current.getLastRejectionReason());
        }
    }

    private void persist(String successMessage) {
        try {
            accountDAO.update(current);
            view.showMessage(successMessage);
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
        view.clearAmount();
        refresh();
    }

    private BigDecimal readAmount() {
        if (current == null) {
            view.showMessage("Find an account first.");
            return null;
        }
        try {
            return new BigDecimal(view.getAmountText().trim());
        } catch (NumberFormatException e) {
            view.showMessage("Enter a valid amount, for example 250.00");
            return null;
        }
    }

    private void refresh() {
        view.showDetails(current.getOwner().getDisplayName() + " | " + current);
        StringBuilder sb = new StringBuilder();
        for (Transaction t : current.getTransactions()) {
            sb.append(t).append("\n");
        }
        view.showHistory(sb.toString());
    }
}