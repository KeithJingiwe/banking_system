package controller;

import dao.AccountDAO;
import model.Account;
import model.Customer;
import model.Transaction;
import view.CustomerView;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Connects a customer's screen to the model. A customer can only reach their own accounts. */
public class CustomerController {
    private final CustomerView view;
    private final Customer customer;
    private final AccountDAO accountDAO = new AccountDAO();

    public CustomerController(CustomerView view, Customer customer) {
        this.view = view;
        this.customer = customer;
        view.setWelcome("Welcome, " + customer.getDisplayName());
        view.setOnDeposit(e -> handleDeposit());
        view.setOnWithdraw(e -> handleWithdraw());
        view.setOnBalance(e -> handleBalance());
        refresh();
    }

    private void handleDeposit() {
        Account account = selectedAccount();
        BigDecimal amount = amount();
        if (account == null || amount == null) return;
        if (account.deposit(amount)) {
            save(account, "Deposit successful.");
        } else {
            view.showMessage(account.getLastRejectionReason());
        }
    }

    private void handleWithdraw() {
        Account account = selectedAccount();
        BigDecimal amount = amount();
        if (account == null || amount == null) return;
        if (account.withdraw(amount)) {
            save(account, "Withdrawal successful.");
        } else {
            view.showMessage(account.getLastRejectionReason());
        }
    }

    private void handleBalance() {
        Account account = selectedAccount();
        if (account == null) return;
        view.showMessage(account.getAccountType() + " account " + account.getAccountNumber()
                + " balance: BWP " + account.getBalance());
        refresh();
    }

    private void save(Account account, String message) {
        try {
            accountDAO.update(account);
            view.showMessage(message + " New balance: BWP " + account.getBalance());
            view.clearAmount();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
        refresh();
    }

    private Account selectedAccount() {
        String number = view.getSelectedAccount();
        if (number == null) {
            view.showMessage("You have no accounts yet. Please visit a teller to open one.");
            return null;
        }
        for (Account a : customer.getAccounts()) {
            if (a.getAccountNumber().equals(number)) return a;
        }
        view.showMessage("Account not found.");
        return null;
    }

    private BigDecimal amount() {
        try {
            return new BigDecimal(view.getAmountText().trim());
        } catch (NumberFormatException e) {
            view.showMessage("Enter a valid amount, for example 250.00");
            return null;
        }
    }

    private void refresh() {
        List<String> numbers = new ArrayList<>();
        StringBuilder summary = new StringBuilder();
        for (Account a : customer.getAccounts()) {
            numbers.add(a.getAccountNumber());
            summary.append(a).append("\n");
        }
        if (numbers.isEmpty()) summary.append("You have no accounts yet.");
        view.setAccountNumbers(numbers);
        view.showAccounts(summary.toString());

        StringBuilder history = new StringBuilder();
        Account selected = null;
        for (Account a : customer.getAccounts()) {
            if (a.getAccountNumber().equals(view.getSelectedAccount())) selected = a;
        }
        if (selected != null) {
            for (Transaction t : selected.getTransactions()) history.append(t).append("\n");
        }
        view.showHistory(history.toString());
    }
}
