package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AccountView extends BorderPane {
    private final TextField accountNumberField = new TextField();
    private final Button findButton = new Button("Find");
    private final Label detailsLabel = new Label("No account selected");
    private final TextField amountField = new TextField();
    private final Button depositButton = new Button("Deposit");
    private final Button withdrawButton = new Button("Withdraw");
    private final Label messageLabel = new Label();
    private final TextArea historyArea = new TextArea();
    private final Button logoutButton = new Button("Logout");

    public AccountView() {
        setPadding(new Insets(20));

        Label title = new Label("Teller Accounts");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox findRow = new HBox(10, new Label("Account number:"), accountNumberField, findButton);
        HBox amountRow = new HBox(10, new Label("Amount (BWP):"), amountField, depositButton, withdrawButton);

        historyArea.setEditable(false);
        historyArea.setPrefRowCount(10);

        VBox center = new VBox(12, findRow, detailsLabel, amountRow, messageLabel,
                new Label("Transaction history"), historyArea);
        center.setPadding(new Insets(15, 0, 15, 0));

        setTop(title);
        setCenter(center);
        setBottom(logoutButton);
    }

    public String getAccountNumber() { return accountNumberField.getText(); }
    public String getAmountText() { return amountField.getText(); }

    public void setOnFind(EventHandler<ActionEvent> h) { findButton.setOnAction(h); }
    public void setOnDeposit(EventHandler<ActionEvent> h) { depositButton.setOnAction(h); }
    public void setOnWithdraw(EventHandler<ActionEvent> h) { withdrawButton.setOnAction(h); }
    public void setOnLogout(EventHandler<ActionEvent> h) { logoutButton.setOnAction(h); }

    public void showDetails(String text) { detailsLabel.setText(text); }
    public void showHistory(String text) { historyArea.setText(text); }
    public void showMessage(String text) { messageLabel.setText(text); }
    public void clearAmount() { amountField.clear(); }
}