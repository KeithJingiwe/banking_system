package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class LoginView extends GridPane {
  private final TextField usernameField = new TextField();
  private final PasswordField passwordField = new PasswordField();
  private final Button loginButton = new Button("Login");
  private final Label messageLabel = new Label();

  public LoginView() {
    setAlignment(Pos.CENTER);
    setHgap(10);
    setVgap(12);
    setPadding(new Insets(30));

    Label title = new Label("BAC Bank - Login");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    add(title, 0, 0, 2, 1);
    add(new Label("Username:"), 0, 1);
    add(usernameField, 1, 1);
    add(new Label("Password:"), 0, 2);
    add(passwordField, 1, 2);
    add(loginButton, 1, 3);
    add(messageLabel, 0, 4, 2, 1);
  }

  public String getUsername() { return usernameField.getText(); }
  public String getPassword() { return passwordField.getText(); }

  public void setOnLoginAction(EventHandler<ActionEvent> handler) {
    loginButton.setOnAction(handler);
  }

  public void showMessage(String message) { messageLabel.setText(message); }

  public void clearPassword() { passwordField.clear(); }
}
