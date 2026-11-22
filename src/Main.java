import model.*;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {

        Bank bank = new Bank("BAC Bank");

        IndividualCustomer thabo = new IndividualCustomer("thabo", "Pass#1234", "Thabo", "Molefe",
                "123456789", LocalDate.of(1998, 4, 12), true,
                "Plot 123, Palapye", "71234567", "thabo@mail.com");
        CompanyCustomer acme = new CompanyCustomer("acme", "Pass#5678", "Acme Traders",
                "BW0001234", "Neo Kgosi", "Gaborone", "3951234", "info@acme.co.bw");
        bank.registerCustomer(thabo);
        bank.registerCustomer(acme);

        Employer employer = new Employer("Debswana", "Orapa", "EMP-778");
        Account sav = bank.openAccount(thabo, "SAVINGS", "Palapye", Money.of("1000"), null);
        Account inv = bank.openAccount(thabo, "INVESTMENT", "Palapye", Money.of("500"), null);
        Account chq = bank.openAccount(thabo, "CHEQUE", "Palapye", Money.ZERO, employer);
        bank.openAccount(acme, "SAVINGS");
        chq.deposit(Money.of("8000"), "Salary");

        InterestPayment run = bank.runMonthlyInterest();
        System.out.println(run);

        for (Account a : thabo.getAccounts()) {
            System.out.println(a);
        }

        System.out.println("Search 'acme': " + bank.searchCustomers("acme"));
        System.out.println("Find ACC02: " + bank.findAccount("ACC02"));

        InterestPayment second = new InterestPayment();
        second.cancel();
        System.out.println("Second run: " + second.getStatus());
        try { second.execute(new java.util.ArrayList<>()); }
        catch (IllegalStateException e) { System.out.println("Refused: " + e.getMessage()); }

    }
}