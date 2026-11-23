package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

/** Boundary class for the bank teller. Shows forms and collects input; contains no business logic. */
public class TellerView extends BorderPane {
    // navigation buttons
    private final Button registerNav = new Button("Register Customer");
    private final Button openNav = new Button("Open Account");
    private final Button depositNav = new Button("Deposit");
    private final Button withdrawNav = new Button("Withdraw");
    private final Button balanceNav = new Button("Balance Enquiry");
    private final Button logoutButton = new Button("Logout");

    private final StackPane content = new StackPane();
    private final Label messageLabel = new Label();
    private final TextArea outputArea = new TextArea();

    // register customer panel
    private final ComboBox<String> customerType = new ComboBox<>();
    private final TextField regUsername = new TextField();
    private final PasswordField regPassword = new PasswordField();
    private final Label label1 = new Label("First name:");
    private final Label label2 = new Label("Surname:");
    private final Label label3 = new Label("Omang number:");
    private final TextField field1 = new TextField();
    private final TextField field2 = new TextField();
    private final TextField field3 = new TextField();
    private final Label dobLabel = new Label("Date of birth:");
    private final DatePicker dobPicker = new DatePicker();
    private final CheckBox employedBox = new CheckBox("Employed");
    private final TextField regAddress = new TextField();
    private final TextField regPhone = new TextField();
    private final TextField regEmail = new TextField();
    private final Button registerButton = new Button("Register");

    // open account panel
    private final TextField openCustomer = new TextField();
    private final ComboBox<String> accountType = new ComboBox<>();
    private final TextField openBranch = new TextField("Gaborone");
    private final TextField openDeposit = new TextField();
    private final TextField employerName = new TextField();
    private final TextField employerAddress = new TextField();
    private final TextField employeeNumber = new TextField();
    private final Button openButton = new Button("Open Account");

    // deposit panel
    private final TextField depositAccount = new TextField();
    private final TextField depositAmount = new TextField();
    private final Button depositButton = new Button("Deposit");

    // withdraw panel
    private final TextField withdrawAccount = new TextField();
    private final TextField withdrawAmount = new TextField();
    private final Button withdrawButton = new Button("Withdraw");

    // balance panel
    private final TextField balanceAccount = new TextField();
    private final Button balanceButton = new Button("Check Balance");

    private final Node registerPanel;
    private final Node openPanel;
    private final Node depositPanel;
    private final Node withdrawPanel;
    private final Node balancePanel;

    public TellerView() {
        setPadding(new Insets(15));

        customerType.getItems().addAll("Individual", "Company");
        customerType.setValue("Individual");
        customerType.setOnAction(e -> updateTypeLabels());
        accountType.getItems().addAll("SAVINGS", "INVESTMENT", "CHEQUE");
        accountType.setValue("SAVINGS");

        registerPanel = buildRegisterPanel();
        openPanel = buildOpenPanel();
        depositPanel = buildSimplePanel("Account number:", depositAccount, "Amount (BWP):", depositAmount, depositButton);
        withdrawPanel = buildSimplePanel("Account number:", withdrawAccount, "Amount (BWP):", withdrawAmount, withdrawButton);
        balancePanel = buildSimplePanel("Account number:", balanceAccount, null, null, balanceButton);

        Label title = new Label("Bank Teller");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label spacer = new Label("   ");
        HBox nav = new HBox(8, registerNav, openNav, depositNav, withdrawNav, balanceNav, spacer, logoutButton);
        nav.setPadding(new Insets(8, 0, 8, 0));
        setTop(new VBox(6, title, nav));

        registerNav.setOnAction(e -> show(registerPanel));
        openNav.setOnAction(e -> show(openPanel));
        depositNav.setOnAction(e -> show(depositPanel));
        withdrawNav.setOnAction(e -> show(withdrawPanel));
        balanceNav.setOnAction(e -> show(balancePanel));

        outputArea.setEditable(false);
        outputArea.setPrefRowCount(9);
        messageLabel.setWrapText(true);
        messageLabel.setStyle("-fx-font-weight: bold;");
        setCenter(content);
        setBottom(new VBox(8, messageLabel, outputArea));

        updateTypeLabels();
        show(registerPanel);
    }

    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.setPadding(new Insets(15));
        return g;
    }

    private Node buildRegisterPanel() {
        GridPane g = grid();
        int r = 0;
        g.add(new Label("Customer type:"), 0, r);   g.add(customerType, 1, r++);
        g.add(new Label("Username:"), 0, r);        g.add(regUsername, 1, r++);
        g.add(new Label("Password:"), 0, r);        g.add(regPassword, 1, r++);
        g.add(label1, 0, r);                        g.add(field1, 1, r++);
        g.add(label2, 0, r);                        g.add(field2, 1, r++);
        g.add(label3, 0, r);                        g.add(field3, 1, r++);
        g.add(dobLabel, 0, r);                      g.add(dobPicker, 1, r++);
        g.add(employedBox, 1, r++);
        g.add(new Label("Address:"), 0, r);         g.add(regAddress, 1, r++);
        g.add(new Label("Phone:"), 0, r);           g.add(regPhone, 1, r++);
        g.add(new Label("Email:"), 0, r);           g.add(regEmail, 1, r++);
        g.add(registerButton, 1, r);
        return g;
    }

    private Node buildOpenPanel() {
        GridPane g = grid();
        int r = 0;
        g.add(new Label("Customer (ID or name):"), 0, r); g.add(openCustomer, 1, r++);
        g.add(new Label("Account type:"), 0, r);          g.add(accountType, 1, r++);
        g.add(new Label("Branch:"), 0, r);                g.add(openBranch, 1, r++);
        g.add(new Label("Opening deposit (BWP):"), 0, r); g.add(openDeposit, 1, r++);
        g.add(new Label("Cheque account only - employer details:"), 0, r, 2, 1); r++;
        g.add(new Label("Employer name:"), 0, r);         g.add(employerName, 1, r++);
        g.add(new Label("Employer address:"), 0, r);      g.add(employerAddress, 1, r++);
        g.add(new Label("Employee number:"), 0, r);       g.add(employeeNumber, 1, r++);
        g.add(openButton, 1, r);
        return g;
    }

    private Node buildSimplePanel(String l1, TextField f1, String l2, TextField f2, Button button) {
        GridPane g = grid();
        int r = 0;
        g.add(new Label(l1), 0, r);  g.add(f1, 1, r++);
        if (l2 != null) { g.add(new Label(l2), 0, r); g.add(f2, 1, r++); }
        g.add(button, 1, r);
        return g;
    }

    private void show(Node panel) {
        content.getChildren().setAll(panel);
        messageLabel.setText("");
        outputArea.clear();
    }

    private void updateTypeLabels() {
        boolean individual = isIndividual();
        label1.setText(individual ? "First name:" : "Company name:");
        label2.setText(individual ? "Surname:" : "Registration no.:");
        label3.setText(individual ? "Omang number:" : "Contact person:");
        dobLabel.setVisible(individual);
        dobPicker.setVisible(individual);
        employedBox.setVisible(individual);
    }

    // ---- values typed by the teller ----
    public boolean isIndividual() { return "Individual".equals(customerType.getValue()); }
    public String getRegUsername() { return regUsername.getText(); }
    public String getRegPassword() { return regPassword.getText(); }
    public String getField1() { return field1.getText(); }
    public String getField2() { return field2.getText(); }
    public String getField3() { return field3.getText(); }
    public LocalDate getDateOfBirth() { return dobPicker.getValue(); }
    public boolean isEmployed() { return employedBox.isSelected(); }
    public String getRegAddress() { return regAddress.getText(); }
    public String getRegPhone() { return regPhone.getText(); }
    public String getRegEmail() { return regEmail.getText(); }

    public String getOpenCustomer() { return openCustomer.getText(); }
    public String getAccountType() { return accountType.getValue(); }
    public String getOpenBranch() { return openBranch.getText(); }
    public String getOpenDeposit() { return openDeposit.getText(); }
    public String getEmployerName() { return employerName.getText(); }
    public String getEmployerAddress() { return employerAddress.getText(); }
    public String getEmployeeNumber() { return employeeNumber.getText(); }

    public String getDepositAccount() { return depositAccount.getText(); }
    public String getDepositAmount() { return depositAmount.getText(); }
    public String getWithdrawAccount() { return withdrawAccount.getText(); }
    public String getWithdrawAmount() { return withdrawAmount.getText(); }
    public String getBalanceAccount() { return balanceAccount.getText(); }

    // ---- handlers set by the controller ----
    public void setOnRegister(EventHandler<ActionEvent> h) { registerButton.setOnAction(h); }
    public void setOnOpenAccount(EventHandler<ActionEvent> h) { openButton.setOnAction(h); }
    public void setOnDeposit(EventHandler<ActionEvent> h) { depositButton.setOnAction(h); }
    public void setOnWithdraw(EventHandler<ActionEvent> h) { withdrawButton.setOnAction(h); }
    public void setOnBalance(EventHandler<ActionEvent> h) { balanceButton.setOnAction(h); }
    public void setOnLogout(EventHandler<ActionEvent> h) { logoutButton.setOnAction(h); }

    // ---- display ----
    public void showMessage(String text) { messageLabel.setText(text); }
    public void showOutput(String text) { outputArea.setText(text); }

    public void clearRegisterForm() {
        regUsername.clear(); regPassword.clear(); field1.clear(); field2.clear(); field3.clear();
        dobPicker.setValue(null); employedBox.setSelected(false);
        regAddress.clear(); regPhone.clear(); regEmail.clear();
    }

    public void clearOpenForm() {
        openCustomer.clear(); openDeposit.clear();
        employerName.clear(); employerAddress.clear(); employeeNumber.clear();
    }

    public void clearAmounts() { depositAmount.clear(); withdrawAmount.clear(); }
}
