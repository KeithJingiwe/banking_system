package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/** Boundary class for a customer: own accounts, deposit, withdraw, balance. No business logic. */
public class CustomerView extends BorderPane {
    private final Label welcomeLabel = new Label();
    private final TextArea accountsArea = new TextArea();
    private final ComboBox<String> accountBox = new ComboBox<>();
    private final TextField amountField = new TextField();
    private final Button depositButton = new Button("Deposit");
    private final Button withdrawButton = new Button("Withdraw");
    private final Button balanceButton = new Button("Check Balance");
    private final Button logoutButton = new Button("Logout");
    private final Label messageLabel = new Label();
    private final TextArea historyArea = new TextArea();

    public CustomerView() {
        setPadding(new Insets(20));

        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        HBox top = new HBox(20, welcomeLabel, logoutButton);

        accountsArea.setEditable(false);
        accountsArea.setPrefRowCount(5);
        historyArea.setEditable(false);
        historyArea.setPrefRowCount(10);
        amountField.setPrefWidth(120);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-weight: bold;");

        HBox actionRow = new HBox(10, new Label("Account:"), accountBox,
                new Label("Amount (BWP):"), amountField, depositButton, withdrawButton, balanceButton);

        VBox center = new VBox(12, new Label("My accounts"), accountsArea, actionRow, messageLabel,
                new Label("Transaction history of selected account"), historyArea);
        center.setPadding(new Insets(15, 0, 0, 0));

        setTop(top);
        setCenter(center);
    }

    public void setWelcome(String text) { welcomeLabel.setText(text); }
    public void showAccounts(String text) { accountsArea.setText(text); }
    public void showHistory(String text) { historyArea.setText(text); }
    public void showMessage(String text) { messageLabel.setText(text); }
    public void clearAmount() { amountField.clear(); }

    public void setAccountNumbers(List<String> numbers) {
        String selected = accountBox.getValue();
        accountBox.getItems().setAll(numbers);
        if (selected != null && numbers.contains(selected)) {
            accountBox.setValue(selected);
        } else if (!numbers.isEmpty()) {
            accountBox.setValue(numbers.get(0));
        }
    }

    public String getSelectedAccount() { return accountBox.getValue(); }
    public String getAmountText() { return amountField.getText(); }

    public void setOnDeposit(EventHandler<ActionEvent> h) { depositButton.setOnAction(h); }
    public void setOnWithdraw(EventHandler<ActionEvent> h) { withdrawButton.setOnAction(h); }
    public void setOnBalance(EventHandler<ActionEvent> h) { balanceButton.setOnAction(h); }
    public void setOnLogout(EventHandler<ActionEvent> h) { logoutButton.setOnAction(h); }
}
