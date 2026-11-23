package app;

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.UserDAO;
import model.*;

import java.sql.SQLException;
import java.time.LocalDate;

public class DataSeeder {
    private DataSeeder() { }

    public static void seedIfEmpty() throws SQLException {
        UserDAO userDAO = new UserDAO();
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();

        if (userDAO.findByUsername("teller1") == null) userDAO.save(new User("teller1", "Teller#123", Role.TELLER));
        if (userDAO.findByUsername("admin") == null) userDAO.save(new User("admin", "Admin#123", Role.ADMINISTRATOR));

        if (customerDAO.findAll().size() >= 10) return;

        Bank bank = new Bank("BAC Bank");
        Employer employer = new Employer("Debswana", "Orapa", "EMP-001");

        String[][] people = {
                {"kabo", "Kabo", "Sebego", "210111111", "true"},
                {"neo", "Neo", "Dikgang", "210222222", "true"},
                {"lesedi", "Lesedi", "Moeng", "210333333", "false"},
                {"tshepo", "Tshepo", "Ntsima", "210444444", "true"},
                {"boitumelo", "Boitumelo", "Seretse", "210555555", "false"},
                {"onkarabile", "Onkarabile", "Phiri", "210666666", "true"},
                {"mpho", "Mpho", "Kgosi", "210777777", "true"},
                {"gorata", "Gorata", "Tau", "210888888", "false"},
        };


        for (int i = 0; i < people.length; i++) {
            String[] p = people[i];
            if (customerDAO.findByUsername(p[0]) != null) continue;
            boolean employed = Boolean.parseBoolean(p[4]);

            IndividualCustomer c = new IndividualCustomer(p[0], "Pass#1234", p[1], p[2], p[3],
                    LocalDate.of(1990 + i, 1 + i, 10), employed,
                    "Plot " + (100 + i) + ", Gaborone", "7100000" + i, p[0] + "@mail.com");
            customerDAO.save(c);
            bank.registerCustomer(c);

            bank.openAccount(c, "SAVINGS", "Gaborone", Money.of(500 + i * 100), null);
            if (i % 2 == 0) {
                bank.openAccount(c, "INVESTMENT", "Gaborone", Money.of(500 + i * 50), null);
            }
            if (employed) {
                Account chq = bank.openAccount(c, "CHEQUE", "Gaborone", Money.ZERO, employer);
                chq.deposit(Money.of(4000 + i * 250), "Salary");
            }
            for (Account a : c.getAccounts()) accountDAO.save(a);
        }

        String[][] companies = {
                {"mmegi", "Mmegi Traders", "BW0002001", "Lorato Sithole"},
                {"kalahari", "Kalahari Supplies", "BW0003002", "Tebogo Mosweu"}};

        for (String[] q : companies) {
            if (customerDAO.findByUsername(q[0]) != null) continue;
            CompanyCustomer co = new CompanyCustomer(q[0], "Pass#5678", q[1], q[2], q[3],
                    "Plot 500, Francistown", "3900000", q[0] + "@company.co.bw");
            customerDAO.save(co);
            bank.registerCustomer(co);
            bank.openAccount(co, "SAVINGS", "Francistown", Money.of("5000"), null);
            for (Account a : co.getAccounts()) accountDAO.save(a);
        }
    }
}