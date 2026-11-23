package app;

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.Schema;
import model.*;

import java.sql.SQLException;

public class AccountDAOTest {
    public static void main(String[] args) throws SQLException {
        Schema.create();
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();

        IndividualCustomer thabo = (IndividualCustomer) customerDAO.findByUsername("thabo");
        // An earlier test set employed=false, and a Cheque account needs an employed customer
        if (!thabo.isEmployed()) {
            thabo.setEmployed(true);
            customerDAO.update(thabo);
        }
        accountDAO.attachAccounts(thabo);

        if (thabo.getAccounts().isEmpty()) {
            Bank bank = new Bank("BAC Bank");
            bank.registerCustomer(thabo);
            Employer employer = new Employer("Debswana", "Orapa", "EMP-778");
            bank.openAccount(thabo, "SAVINGS", "Palapye", Money.of("1000"), null);
            bank.openAccount(thabo, "INVESTMENT", "Palapye", Money.of("500"), null);
            Account chq = bank.openAccount(thabo, "CHEQUE", "Palapye", Money.ZERO, employer);
            chq.deposit(Money.of("8000"), "Salary");
            for (Account a : thabo.getAccounts()) accountDAO.save(a);
            System.out.println("Saved 3 accounts");

            // Start again from the database, as a new run of the program would
            thabo = (IndividualCustomer) customerDAO.findByUsername("thabo");
            accountDAO.attachAccounts(thabo);
        }

        Account cheque = null;
        for (Account a : thabo.getAccounts()) {
            System.out.println(a + " (" + a.getTransactions().size() + " transactions)");
            if (a.getAccountType().equals("Cheque")) cheque = a;
        }

        cheque.deposit(Money.of("100"), "Test deposit");
        accountDAO.update(cheque);

        Account reloaded = accountDAO.findByNumber(cheque.getAccountNumber());
        System.out.println("Cheque after test deposit (read from DB): " + reloaded.getBalance()
                + " (" + reloaded.getTransactions().size() + " transactions)");
        System.out.println("Owner: " + reloaded.getOwner().getDisplayName());
        System.out.println("Savings withdraw refused after reload: "
                + !thabo.getAccounts().get(0).withdraw(Money.of("10")));
    }
}