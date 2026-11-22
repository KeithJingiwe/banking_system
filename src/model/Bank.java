package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank {
    private static final String DEFAULT_BRANCH = "Main Branch";

    private final String bankName;
    private final List<Customer> customers = new ArrayList<>();

    public Bank(String bankName) { this.bankName = bankName; }

    public void registerCustomer(Customer c) {
        if (c == null) throw new IllegalArgumentException("Customer is required.");
        if (!customers.contains(c)) customers.add(c);
    }

    // Overloaded: simple version, as in the class diagram
    public Account openAccount(Customer c, String type) {
        return openAccount(c, type, DEFAULT_BRANCH, Money.ZERO, null);
    }

    // Full version: branch, opening deposit and employer can be supplied
    public Account openAccount(Customer c, String type, String branch, BigDecimal initialDeposit, Employer employer) {
        if (c == null || !customers.contains(c)) {
            throw new IllegalArgumentException("Customer must be registered before opening an account.");
        }
        String number = IdGenerator.next("ACC");
        Account account;
        switch (type.trim().toUpperCase()) {
            case "SAVINGS" -> account = new SavingsAccount(number, branch, c);
            case "INVESTMENT" -> account = new InvestmentAccount(number, branch, c, initialDeposit);
            case "CHEQUE" -> account = new ChequeAccount(number, branch, c, employer);
            default -> throw new IllegalArgumentException("Unknown account type: " + type);
        }
        c.addAccount(account);
        if (!(account instanceof InvestmentAccount) && initialDeposit != null && initialDeposit.signum() > 0) {
            account.deposit(initialDeposit, "Opening deposit");
        }
        return account;
    }

    public List<Customer> searchCustomers(String keyword) {
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Customer> found = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getCustomerId().toLowerCase().contains(k)
                    || c.getDisplayName().toLowerCase().contains(k)) {
                found.add(c);
            }
        }
        return found;
    }

    public Account findAccount(String accountNumber) {
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a.getAccountNumber().equalsIgnoreCase(accountNumber)) return a;
            }
        }
        return null;
    }

    public InterestPayment runMonthlyInterest() {
        List<InterestBearing> eligible = new ArrayList<>();
        for (Customer c : customers) {
            for (Account a : c.getAccounts()) {
                if (a instanceof InterestBearing ib && a.isActive()) {
                    eligible.add(ib);
                }
            }
        }
        InterestPayment payment = new InterestPayment();
        payment.execute(eligible);
        return payment;
    }

    public String getBankName() { return bankName; }
    public List<Customer> getCustomers() { return Collections.unmodifiableList(customers); }
}
