package Controller;

import dao.UserDAO;
import model.User;
import view.LoginView;

import java.sql.SQLException;
import java.util.function.Consumer;

public class LoginController {
    private final LoginView view;
    private final UserDAO userDAO = new UserDAO();
    private Consumer<User> onLoginSuccess;

    public LoginController(LoginView view) {
        this.view = view;
        this.view.setOnLoginAction(e -> handleLogin());
    }

    public void setOnLoginSuccess(Consumer<User> handler) { this.onLoginSuccess = handler; }

    private void handleLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.showMessage("Please enter your username and password.");
            return;
        }

        try {
            User user = userDAO.findByUsername(username);
            if (user == null) {
                view.showMessage("Invalid username or password.");
            } else if (user.isLocked()) {
                view.showMessage("This account is locked. Contact an administrator.");
            } else {
                boolean ok = user.authenticate(password);
                userDAO.update(user);                       // saves failed attempts / lock
                if (ok) {
                    if (onLoginSuccess != null) onLoginSuccess.accept(user);
                } else if (user.isLocked()) {
                    view.showMessage("Too many failed attempts. Account locked.");
                } else {
                    view.showMessage("Invalid username or password.");
                }
            }
        } catch (SQLException ex) {
            view.showMessage("Database error: " + ex.getMessage());
        }
        view.clearPassword();
    }
}