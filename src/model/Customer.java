package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Customer extends User {

    //---
    private final List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        if (account == null || account.getOwner() != this) {
            throw new IllegalArgumentException("Account must be owned by this customer.");
        }
        if (!accounts.contains(account)) accounts.add(account);
    }

    public List<Account> getAccounts() { return Collections.unmodifiableList(accounts); }

    private String customerId;

    protected Customer(String username, String password, String address, String phone, String email) {

        super(username,password,Role.CUSTOMER,address,phone,email);
    }

    {
        this.customerId = IdGenerator.next("CUST");
    }

    public abstract String getDisplayName();
    public String getCustomerId() {return customerId;}

    // Used when re-loading a stored customer (from the database)
    protected Customer(String customerId, String userID, String userName, String passwordHash,
                       String address, String phone, String email,
                       int failedAttempts, boolean locked, boolean active) {
        super(userID, userName, passwordHash, Role.CUSTOMER, address, phone, email,
                failedAttempts, locked, active);
        this.customerId = customerId;
    }

    @Override
    public String toString() {return getDisplayName() + " [" + customerId + "]";}
}
