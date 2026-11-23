package app;

import Controller.AccountController;
import Controller.LoginController;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.Schema;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;
import view.AccountView;
import view.LoginView;

import java.sql.SQLException;

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
            System.out.println(c + " : " + nums);       // handy account numbers for the demo
        }
    }

    private void showLogin() {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);
        loginController.setOnLoginSuccess(user -> {
            if (user.getRole() == Role.CUSTOMER) {
                loginView.showMessage("Customer screens are coming soon.");
            } else {
                showAccountScreen();
            }
        });
        stage.setScene(new Scene(loginView, 400, 300));
    }

    private void showAccountScreen() {
        AccountView accountView = new AccountView();
        new AccountController(accountView, bank);
        accountView.setOnLogout(e -> showLogin());
        stage.setScene(new Scene(accountView, 700, 550));
    }

    public static void main(String[] args) {
        launch(args);
    }
}