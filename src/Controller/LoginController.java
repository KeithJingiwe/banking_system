package Controller;

import model. User;
import view.LoginView;

import java.util.HashMap;
import java.util.Map;

public class LoginController {
    private final LoginView view;
    private final Map<String, User> users = new HashMap<>();

    public LoginController(LoginView view) {
        this.view = view;
        this.view.setOnLoginAction(e -> handleLogin());
    }

    // Temporary: later the database (UserDAO) will supply users
    public void addUser(User user) {
        users.put(user.getUserName().toLowerCase(), user);
    }

    private void handleLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showMessage("Please enter your username and password.");
            return;
        }

        User user = users.get(username.toLowerCase());
        if (user == null) {
            view.showMessage("Invalid username or password.");
            view.clearPassword();
            return;
        }

        if (user.isLocked()) {
            view.showMessage("This account is locked. Contact an administrator.");
            view.clearPassword();
            return;
        }

        if (user.authenticate(password)) {
            view.showMessage("Welcome, " + user.getUserName() + " (" + user.getRole() + ")");
        } else if (user.isLocked()) {
            view.showMessage("Too many failed attempts. Account locked.");
        } else {
            view.showMessage("Invalid username or password.");
        }
        view.clearPassword();
    }
}
