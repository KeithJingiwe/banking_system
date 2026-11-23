package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Account implements Transactable {

    private final Customer owner;

    private final String accountNumber;
    private BigDecimal balance =  Money.ZERO;
    private final String branch;
    private final LocalDate dateOpened;
    private AccountStatus status = AccountStatus.ACTIVE;
    private final List<Transaction> transactions = new ArrayList<>();
    private String lastRejectionReason = "";

    public Account(String accountNumber, String branch, Customer owner) {
        if (owner == null) {
            throw new IllegalArgumentException("An account cannot exist without a customer");
        }
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.owner = owner;
        this.dateOpened = LocalDate.now();
    }

    @Override
    public boolean deposit(BigDecimal amount) {
        return deposit(amount, "Deposit");
    }

    public boolean deposit(BigDecimal amount, String note) {
        if (!isActive()) return reject("Account is closed.");
        if (amount == null || amount.signum() < 0) {
            return reject("Deposit amount must be greater than Zero.");
        }
        applyCredit(Money.round(amount), TransactionType.DEPOSIT, note);
        return true;
    }

    @Override
    public abstract boolean withdraw(BigDecimal amount);
    public abstract String getAccountType();

    //helpers for subclasses
    protected boolean debit(BigDecimal amount) {
        if (!isActive()) return reject("Account is closed.");
        if (amount == null || amount.signum() <= 0) {
            return reject("Withdrawal amount must be greater than zero.");
        }
        BigDecimal value = Money.round(amount);
        if (value.compareTo(balance) > 0) {
            return reject("Insufficient funds. Available balance: BWP " + balance);
        }
        balance = balance.subtract(value);
        recordTransaction(new Transaction(TransactionType.WITHDRAWAL, value, balance, "Withdrawal"));
        return true;
    }

    protected Transaction applyCredit(BigDecimal amount, TransactionType type, String note) {
        balance = balance.add(amount);
        Transaction t = new Transaction(type, amount, balance, note);
        recordTransaction(t);
        return t;
    }

    protected void recordTransaction(Transaction t) { transactions.add(t); }

    protected boolean reject(String reason) {
        this.lastRejectionReason = reason;
        return false;
    }

    public void close() { status = AccountStatus.CLOSED; }
    public boolean isActive() { return status == AccountStatus.ACTIVE; }

    public BigDecimal getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public String getBranch() { return branch; }
    public LocalDate getDateOpened() { return dateOpened; }
    public AccountStatus getStatus() { return status; }
    public Customer getOwner() { return owner; }
    public String getLastRejectionReason() { return lastRejectionReason; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }

    // Used when re-loading a stored account (from the database):
    protected Account(String accountNumber, String branch, Customer owner, LocalDate dateOpened,
                      AccountStatus status, BigDecimal balance, List<Transaction> history) {
        if (owner == null) {
            throw new IllegalArgumentException("An account cannot exist without a customer.");
        }
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.owner = owner;
        this.dateOpened = dateOpened;
        this.status = status;
        this.balance = Money.round(balance);
        this.transactions.addAll(history);
    }


    @Override
    public String toString() {
        return String.format("%s [%s] Branch: %s | Balance: BWP %s | %s",
                getAccountType(), accountNumber, branch, balance, status);
    }

}
