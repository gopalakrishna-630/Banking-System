package com.banking.prototype.ui;

import com.banking.prototype.model.Transaction;
import com.banking.prototype.model.TransactionType;
import com.banking.prototype.model.UserAccount;
import com.banking.prototype.service.BankService;
import com.banking.prototype.service.BankingException;
import com.banking.prototype.service.InsufficientFundsException;
import com.banking.prototype.util.ValidationUtil;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * JavaFX entry point: login, registration, dashboard with deposit / withdraw / transfer / statement,
 * and profile management.
 */
public class BankingFxApp extends Application {

    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BankService bankService = BankService.createDefault();
    private Stage primaryStage;
    private UserAccount sessionUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Banking Information System — Prototype");
        stage.setMinWidth(720);
        stage.setMinHeight(520);
        showLogin();
        stage.show();
    }

    private void showLogin() {
        sessionUser = null;
        VBox root = new VBox(16);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.CENTER);

        Label heading = new Label("Welcome — Sign in");
        heading.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(280);
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(280);

        Button loginBtn = new Button("Login");
        loginBtn.setDefaultButton(true);
        Button registerBtn = new Button("Create account");

        loginBtn.setOnAction(e -> {
            try {
                sessionUser = bankService.login(usernameField.getText(), passwordField.getText());
                showDashboard();
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        });

        registerBtn.setOnAction(e -> showRegistration());

        HBox row = new HBox(12, loginBtn, registerBtn);
        row.setAlignment(Pos.CENTER);

        root.getChildren().addAll(heading, usernameField, passwordField, row);
        primaryStage.setScene(new Scene(root, 480, 420));
    }

    private void showRegistration() {
        VBox root = new VBox(14);
        root.setPadding(new Insets(28));
        root.setAlignment(Pos.CENTER_LEFT);

        Label heading = new Label("New account registration");
        heading.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField userField = new TextField();
        userField.setPromptText("Username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        TextField depositField = new TextField();
        depositField.setPromptText("Initial deposit (optional, default 0)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Username"), userField);
        grid.addRow(1, new Label("Password"), passField);
        grid.addRow(2, new Label("Initial deposit"), depositField);

        Button submit = new Button("Register");
        Button back = new Button("Back to login");

        submit.setOnAction(e -> {
            try {
                double initial = ValidationUtil.parseNonNegativeAmount(depositField.getText());
                ValidationUtil.validateUsername(userField.getText());
                ValidationUtil.validatePassword(passField.getText());
                UserAccount created = bankService.register(userField.getText(), passField.getText(), initial);
                Alert ok = new Alert(Alert.AlertType.INFORMATION);
                ok.setTitle("Success");
                ok.setHeaderText("Account created");
                ok.setContentText("Your account number is " + created.getAccountNumber()
                        + "\nYou can sign in now.");
                ok.showAndWait();
                showLogin();
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        });

        back.setOnAction(e -> showLogin());

        HBox buttons = new HBox(12, submit, back);
        root.getChildren().addAll(heading, grid, buttons);
        primaryStage.setScene(new Scene(root, 520, 380));
    }

    private void showDashboard() {
        try {
            sessionUser = bankService.refreshAccount(sessionUser);
        } catch (BankingException e) {
            userError(e.getMessage());
            showLogin();
            return;
        }

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        Label welcome = new Label();
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        welcome.setText("Hello, " + sessionUser.getFullName());

        Label meta = new Label();
        meta.setWrapText(true);
        refreshMetaLabels(sessionUser, welcome, meta);

        Button depositBtn = new Button("Deposit");
        Button withdrawBtn = new Button("Withdraw");
        Button transferBtn = new Button("Transfer");
        Button statementBtn = new Button("View statement");
        Button profileBtn = new Button("Account details");
        Button logoutBtn = new Button("Logout");

        depositBtn.setOnAction(e -> runMoneyDialog("Deposit", "Amount to deposit", true, () -> {
            try {
                refreshMetaLabels(bankService.refreshAccount(sessionUser), welcome, meta);
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        }));

        withdrawBtn.setOnAction(e -> runMoneyDialog("Withdraw", "Amount to withdraw", false, () -> {
            try {
                refreshMetaLabels(bankService.refreshAccount(sessionUser), welcome, meta);
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        }));

        transferBtn.setOnAction(e -> runTransferDialog(() -> {
            try {
                refreshMetaLabels(bankService.refreshAccount(sessionUser), welcome, meta);
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        }));

        statementBtn.setOnAction(e -> showStatementWindow());

        profileBtn.setOnAction(e -> showProfileDialog(() -> {
            try {
                UserAccount u = bankService.refreshAccount(sessionUser);
                sessionUser = u;
                refreshMetaLabels(u, welcome, meta);
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        }));

        logoutBtn.setOnAction(e -> showLogin());

        HBox row1 = new HBox(10, depositBtn, withdrawBtn, transferBtn, statementBtn);
        HBox row2 = new HBox(10, profileBtn, logoutBtn);

        root.getChildren().addAll(welcome, meta, row1, row2);
        primaryStage.setScene(new Scene(root, 720, 420));
    }

    private static void refreshMetaLabels(UserAccount u, Label welcome, Label meta) {
        welcome.setText("Hello, " + u.getFullName());
        meta.setText("Account: " + u.getAccountNumber()
                + "  |  Balance: " + String.format("%.2f", u.getBalance())
                + "  |  Email: " + (u.getEmail().isEmpty() ? "—" : u.getEmail()));
    }

    private void runMoneyDialog(String title, String prompt, boolean isDeposit, Runnable onSuccess) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(prompt);

        ButtonType okType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okType, ButtonType.CANCEL);

        TextField amountField = new TextField();
        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.addRow(0, new Label("Amount:"), amountField);
        dialog.getDialogPane().setContent(g);

        Window owner = primaryStage;
        dialog.initOwner(owner);

        dialog.setResultConverter(btn -> {
            if (btn == okType) {
                return amountField.getText();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(amountText -> {
            try {
                double amt = ValidationUtil.parsePositiveAmount(amountText);
                if (isDeposit) {
                    bankService.deposit(sessionUser, amt);
                } else {
                    bankService.withdraw(sessionUser, amt);
                }
                userInfo(isDeposit ? "Deposit successful." : "Withdrawal successful."
                        + "\nNew balance: " + String.format("%.2f",
                        bankService.refreshAccount(sessionUser).getBalance()));
                onSuccess.run();
            } catch (InsufficientFundsException ex) {
                userError(ex.getMessage());
            } catch (BankingException ex) {
                userError(ex.getMessage());
            }
        });
    }

    private void runTransferDialog(Runnable onSuccess) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Transfer funds");
        dialog.setHeaderText("Enter destination account and amount");

        ButtonType sendType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendType, ButtonType.CANCEL);

        TextField toAccount = new TextField();
        toAccount.setPromptText("e.g. ACC00000001");
        TextField amountField = new TextField();

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.addRow(0, new Label("To account:"), toAccount);
        g.addRow(1, new Label("Amount:"), amountField);
        dialog.getDialogPane().setContent(g);
        dialog.initOwner(primaryStage);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == sendType) {
                try {
                    double amt = ValidationUtil.parsePositiveAmount(amountField.getText());
                    ValidationUtil.validateAccountNumberFormat(toAccount.getText());
                    bankService.transfer(sessionUser, toAccount.getText().trim(), amt);
                    userInfo("Transfer completed successfully.");
                    onSuccess.run();
                } catch (BankingException ex) {
                    userError(ex.getMessage());
                }
            }
        });
    }

    private void showProfileDialog(Runnable onSaved) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Account details");
        dialog.setHeaderText("View and update your profile");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CLOSE);

        TextField nameField = new TextField(sessionUser.getFullName());
        TextField emailField = new TextField(sessionUser.getEmail());
        TextField phoneField = new TextField(sessionUser.getPhone());
        Label accLabel = new Label(sessionUser.getAccountNumber());
        accLabel.setStyle("-fx-font-weight: bold;");

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);
        g.addRow(0, new Label("Account number:"), accLabel);
        g.addRow(1, new Label("Full name:"), nameField);
        g.addRow(2, new Label("Email:"), emailField);
        g.addRow(3, new Label("Phone:"), phoneField);
        dialog.getDialogPane().setContent(g);
        dialog.initOwner(primaryStage);

        dialog.showAndWait().ifPresent(btn -> {
            if (btn == saveType) {
                try {
                    bankService.updateProfile(sessionUser, nameField.getText(), emailField.getText(),
                            phoneField.getText());
                    userInfo("Profile updated.");
                    onSaved.run();
                } catch (BankingException ex) {
                    userError(ex.getMessage());
                }
            }
        });
    }

    private void showStatementWindow() {
        Stage st = new Stage();
        st.initOwner(primaryStage);
        st.setTitle("Transaction statement");

        TableView<Transaction> table = new TableView<>();
        TableColumn<Transaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getTimestamp().format(TS_FORMAT)));
        TableColumn<Transaction, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(c -> new ReadOnlyStringWrapper(formatType(c.getValue().getType())));
        TableColumn<Transaction, String> colAmount = new TableColumn<>("Amount");
        colAmount.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                String.format("%.2f", c.getValue().getAmount())));
        TableColumn<Transaction, String> colBal = new TableColumn<>("Balance after");
        colBal.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                String.format("%.2f", c.getValue().getBalanceAfter())));
        TableColumn<Transaction, String> colDesc = new TableColumn<>("Details");
        colDesc.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDescription()));

        table.getColumns().addAll(colDate, colType, colAmount, colBal, colDesc);

        try {
            List<Transaction> list = bankService.transactionHistory(sessionUser);
            table.getItems().addAll(list);
        } catch (BankingException e) {
            userError(e.getMessage());
            return;
        }

        Button close = new Button("Close");
        close.setOnAction(e -> st.close());
        ScrollPane scroll = new ScrollPane(table);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        VBox vb = new VBox(10, scroll, close);
        vb.setPadding(new Insets(10));
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

        st.setScene(new Scene(vb, 900, 420));
        st.show();
    }

    private static String formatType(TransactionType ty) {
        return switch (ty) {
            case DEPOSIT -> "Deposit";
            case WITHDRAWAL -> "Withdrawal";
            case TRANSFER_IN -> "Transfer in";
            case TRANSFER_OUT -> "Transfer out";
        };
    }

    private void userError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void userInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Information");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
