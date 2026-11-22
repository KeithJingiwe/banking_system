package app;

import Controller.LoginController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Role;
import model.User;
import view.LoginView;

public class BankApp extends Application {
    @Override
    public void start(Stage stage) {
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(loginView);

        // Temporary test users
        loginController.addUser(new User("teller1", "Teller#123", Role.TELLER));
        loginController.addUser(new User("admin", "Admin#123", Role.ADMINISTRATOR));

        stage.setTitle("BAC Bank");
        stage.setScene(new Scene(loginView, 400, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}