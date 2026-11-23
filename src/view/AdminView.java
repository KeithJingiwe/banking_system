package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/** Boundary class for the bank administrator: monthly interest and system users. No business logic. */
public class AdminView extends BorderPane {
    private final Button runInterestButton = new Button("Run Monthly Interest Payment");
    private final TextArea outputArea = new TextArea();

    private final ListView<String> userList = new ListView<>();
    private final Button unlockButton = new Button("Unlock Selected");
    private final Button deactivateButton = new Button("Deactivate Selected");
    private final Button refreshButton = new Button("Refresh");

    private final TextField newUsername = new TextField();
    private final PasswordField newPassword = new PasswordField();
    private final ComboBox<String> newRole = new ComboBox<>();
    private final Button addUserButton = new Button("Add User");

    private final Label messageLabel = new Label();
    private final Button logoutButton = new Button("Logout");

    public AdminView() {
        setPadding(new Insets(20));

        Label title = new Label("Bank Administrator");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        setTop(new HBox(20, title, logoutButton));

        outputArea.setEditable(false);
        outputArea.setPrefRowCount(5);
        userList.setPrefHeight(180);
        newUsername.setPromptText("username");
        newPassword.setPromptText("password (6+ chars)");
        newRole.getItems().addAll("TELLER", "ADMINISTRATOR");
        newRole.setValue("TELLER");
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-weight: bold;");

        Label interestHeading = new Label("Monthly interest");
        interestHeading.setStyle("-fx-font-weight: bold;");
        Label usersHeading = new Label("System users");
        usersHeading.setStyle("-fx-font-weight: bold;");

        VBox center = new VBox(10,
                interestHeading, runInterestButton, outputArea,
                usersHeading, userList,
                new HBox(10, unlockButton, deactivateButton, refreshButton),
                new HBox(10, newUsername, newPassword, newRole, addUserButton),
                messageLabel);
        center.setPadding(new Insets(15, 0, 0, 0));
        setCenter(center);
    }

    public void showOutput(String text) { outputArea.setText(text); }
    public void showMessage(String text) { messageLabel.setText(text); }
    public void setUsers(List<String> lines) { userList.getItems().setAll(lines); }
    public String getSelectedUserLine() { return userList.getSelectionModel().getSelectedItem(); }

    public String getNewUsername() { return newUsername.getText(); }
    public String getNewPassword() { return newPassword.getText(); }
    public String getNewRole() { return newRole.getValue(); }
    public void clearNewUserForm() { newUsername.clear(); newPassword.clear(); }

    public void setOnRunInterest(EventHandler<ActionEvent> h) { runInterestButton.setOnAction(h); }
    public void setOnUnlock(EventHandler<ActionEvent> h) { unlockButton.setOnAction(h); }
    public void setOnDeactivate(EventHandler<ActionEvent> h) { deactivateButton.setOnAction(h); }
    public void setOnRefresh(EventHandler<ActionEvent> h) { refreshButton.setOnAction(h); }
    public void setOnAddUser(EventHandler<ActionEvent> h) { addUserButton.setOnAction(h); }
    public void setOnLogout(EventHandler<ActionEvent> h) { logoutButton.setOnAction(h); }
}
