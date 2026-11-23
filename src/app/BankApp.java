package app;

import controller.AdminController;
import controller.CustomerController;
import Controller.LoginController;
import controller.TellerController;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.Schema;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Account;
import model.Bank;
import model.Customer;
import view.AdminView;
import view.CustomerView;
import view.LoginView;
import view.TellerView;

import java.sql.SQLException;

import static model.Role.ADMINISTRATOR;
import static model.Role.CUSTOMER;

public class BankApp extends Application {
    private Stage stage;
    private final Bank bank = new Bank("BAC Bank");

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        try {
            Schema.create();
            DataSeeder.seedIfEmpty();
            loadBank();
        } catch (SQLException e) {
            throw new RuntimeException("Database setup failed", e);
        }
        stage.setTitle("BAC Bank");
        showLogin();
        stage.show();
    }

    // Loads every stored customer and their accounts into the Bank
    private void loadBank() throws SQLException {
        CustomerDAO customerDAO = new CustomerDAO();
        AccountDAO accountDAO = new AccountDAO();
        for (Customer c : customerDAO.findAll()) {
            accountDAO.attachAccounts(c);
            bank.registerCustomer(c);
            StringBuilder nums = new StringBuilder();
            for (Account a : c.getAccounts()) nums.append(a.getAccountNumber()).append(' ');
            System.out.println(c + " [" + c.getUserName() + "] : " + nums);   // handy for the demo
        }
    }

    // Login: each role gets its own screen
    private void showLogin() {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
        loginController.setOnLoginSuccess(user -> {
            switch (user.getRole()) {
                case TELLER -> showTeller();
                case ADMINISTRATOR -> showAdmin();
                case CUSTOMER -> {
                    Customer customer = findCustomer(user.getUserName());
                    if (customer == null) {
                        loginView.showMessage("No customer profile found for this login.");
                    } else {
                        showCustomer(customer);
                    }
                }
            }
        });
        stage.setScene(new Scene(loginView, 400, 300));
    }

    private Customer findCustomer(String username) {
        for (Customer c : bank.getCustomers()) {
            if (c.getUserName().equalsIgnoreCase(username)) return c;
        }
        return null;
    }

    private void showTeller() {
        TellerView view = new TellerView();
        new TellerController(view, bank);
        view.setOnLogout(e -> showLogin());
        stage.setScene(new Scene(view, 960, 720));
    }

    private void showCustomer(Customer customer) {
        CustomerView view = new CustomerView();
        new CustomerController(view, customer);
        view.setOnLogout(e -> showLogin());
        stage.setScene(new Scene(view, 900, 700));
    }

    private void showAdmin() {
        AdminView view = new AdminView();
        new AdminController(view, bank);
        view.setOnLogout(e -> showLogin());
        stage.setScene(new Scene(view, 900, 700));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
