package controller;

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.UserDAO;
import model.Account;
import model.Bank;
import model.CompanyCustomer;
import model.Customer;
import model.Employer;
import model.IndividualCustomer;
import model.Money;
import model.Transaction;
import view.TellerView;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/** Connects the teller screen to the model and the database. */
public class TellerController {
    private final TellerView view;
    private final Bank bank;
    private final UserDAO userDAO = new UserDAO();
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    public TellerController(TellerView view, Bank bank) {
        this.view = view;
        this.bank = bank;
        view.setOnRegister(e -> handleRegister());
        view.setOnOpenAccount(e -> handleOpenAccount());
        view.setOnDeposit(e -> handleDeposit());
        view.setOnWithdraw(e -> handleWithdraw());
        view.setOnBalance(e -> handleBalance());
    }

    // ---------- register customer ----------
    private void handleRegister() {
        String username = view.getRegUsername().trim();
        String password = view.getRegPassword();
        String f1 = view.getField1().trim();
        String f2 = view.getField2().trim();
        String f3 = view.getField3().trim();
        String address = view.getRegAddress().trim();
        String phone = view.getRegPhone().trim();
        String email = view.getRegEmail().trim();

        if (username.isEmpty() || f1.isEmpty() || f2.isEmpty() || f3.isEmpty() || address.isEmpty()) {
            view.showMessage("Please fill in all the fields (phone and email are optional).");
            return;
        }
        if (password.length() < 6) {
            view.showMessage("The password must be at least 6 characters.");
            return;
        }
        if (view.isIndividual() && view.getDateOfBirth() == null) {
            view.showMessage("Please choose the date of birth.");
            return;
        }

        try {
            if (userDAO.findByUsername(username) != null) {
                view.showMessage("That username is already taken.");
                return;
            }
            Customer customer;
            if (view.isIndividual()) {
                customer = new IndividualCustomer(username, password, f1, f2, f3,
                        view.getDateOfBirth(), view.isEmployed(), address, phone, email);
            } else {
                customer = new CompanyCustomer(username, password, f1, f2, f3, address, phone, email);
            }
            customerDAO.save(customer);
            bank.registerCustomer(customer);
            view.showMessage("Customer registered.");
            view.showOutput("Customer ID : " + customer.getCustomerId()
                    + "\nName        : " + customer.getDisplayName()
                    + "\nLogin       : " + username + " (customer can now log in)");
            view.clearRegisterForm();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showMessage(e.getMessage());
        }
    }

    // ---------- open account ----------
    private void handleOpenAccount() {
        Customer customer = findCustomer(view.getOpenCustomer());
        if (customer == null) return;

        BigDecimal deposit = Money.ZERO;
        if (!view.getOpenDeposit().isBlank()) {
            deposit = parseAmount(view.getOpenDeposit());
            if (deposit == null) {
                view.showMessage("Enter a valid opening deposit, for example 500.00");
                return;
            }
        }
        String type = view.getAccountType();
        String branch = view.getOpenBranch().isBlank() ? "Gaborone" : view.getOpenBranch().trim();

        try {
            Employer employer = null;
            if ("CHEQUE".equals(type)) {
                if (view.getEmployerName().isBlank() || view.getEmployerAddress().isBlank()) {
                    view.showMessage("A Cheque account needs the employer name and address.");
                    return;
                }
                employer = new Employer(view.getEmployerName().trim(), view.getEmployerAddress().trim(),
                        view.getEmployeeNumber().trim());
            }
            Account account = bank.openAccount(customer, type, branch, deposit, employer);
            accountDAO.save(account);
            view.showMessage(type + " account opened.");
            view.showOutput("Account number : " + account.getAccountNumber()
                    + "\nOwner          : " + customer.getDisplayName()
                    + "\nType           : " + account.getAccountType()
                    + "\nBalance        : BWP " + account.getBalance());
            view.clearOpenForm();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            view.showMessage(e.getMessage());                  // the model's business rules
        }
    }

    // ---------- deposit ----------
    private void handleDeposit() {
        Account account = findAccount(view.getDepositAccount());
        if (account == null) return;
        BigDecimal amount = parseAmount(view.getDepositAmount());
        if (amount == null) {
            view.showMessage("Enter a valid amount, for example 250.00");
            return;
        }
        if (account.deposit(amount)) {
            finishTransaction(account, "Deposit successful.");
        } else {
            view.showMessage(account.getLastRejectionReason());
        }
    }

    // ---------- withdraw ----------
    private void handleWithdraw() {
        Account account = findAccount(view.getWithdrawAccount());
        if (account == null) return;
        BigDecimal amount = parseAmount(view.getWithdrawAmount());
        if (amount == null) {
            view.showMessage("Enter a valid amount, for example 250.00");
            return;
        }
        if (account.withdraw(amount)) {
            finishTransaction(account, "Withdrawal successful.");
        } else {
            view.showMessage(account.getLastRejectionReason());
        }
    }

    private void finishTransaction(Account account, String successMessage) {
        try {
            accountDAO.update(account);
            view.showMessage(successMessage);
            List<Transaction> history = account.getTransactions();
            view.showOutput(history.get(history.size() - 1).generateReceipt());
            view.clearAmounts();
        } catch (SQLException e) {
            view.showMessage("Database error: " + e.getMessage());
        }
    }

    // ---------- balance enquiry ----------
    private void handleBalance() {
        Account account = findAccount(view.getBalanceAccount());
        if (account == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("Account : ").append(account.getAccountNumber()).append(" (").append(account.getAccountType()).append(")\n");
        sb.append("Owner   : ").append(account.getOwner().getDisplayName()).append("\n");
        sb.append("Status  : ").append(account.getStatus()).append("\n");
        sb.append("Balance : BWP ").append(account.getBalance()).append("\n\nRecent transactions:\n");
        List<Transaction> history = account.getTransactions();
        for (int i = Math.max(0, history.size() - 5); i < history.size(); i++) {
            sb.append("  ").append(history.get(i)).append("\n");
        }
        view.showMessage("Balance: BWP " + account.getBalance());
        view.showOutput(sb.toString());
    }

    // ---------- helpers ----------
    private Account findAccount(String number) {
        if (number == null || number.isBlank()) {
            view.showMessage("Enter the account number.");
            return null;
        }
        Account account = bank.findAccount(number.trim());
        if (account == null) view.showMessage("Account not found.");
        return account;
    }

    private Customer findCustomer(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            view.showMessage("Enter the customer ID or name.");
            return null;
        }
        List<Customer> matches = bank.searchCustomers(keyword);
        for (Customer c : matches) {
            if (c.getCustomerId().equalsIgnoreCase(keyword.trim())) return c;
        }
        if (matches.isEmpty()) {
            view.showMessage("No customer found.");
            return null;
        }
        if (matches.size() > 1) {
            StringBuilder sb = new StringBuilder("More than one match - use the customer ID: ");
            for (Customer c : matches) sb.append(c.getCustomerId()).append(' ');
            view.showMessage(sb.toString());
            return null;
        }
        return matches.get(0);
    }

    private BigDecimal parseAmount(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
