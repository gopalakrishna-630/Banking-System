package com.banking.prototype.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a registered bank customer account with profile fields and transaction history.
 * <p>
 * Password is stored in plain text for this learning prototype only — never do this in production.
 */
public class UserAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String username;
    private String password;
    private final String accountNumber;
    private double balance;
    private String fullName;
    private String email;
    private String phone;
    private final List<Transaction> transactions;

    public UserAccount(String username, String password, String accountNumber, double initialBalance) {
        this.username = Objects.requireNonNull(username, "username");
        this.password = Objects.requireNonNull(password, "password");
        this.accountNumber = Objects.requireNonNull(accountNumber, "accountNumber");
        this.balance = initialBalance;
        this.fullName = username;
        this.email = "";
        this.phone = "";
        this.transactions = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName : "";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null ? email : "";
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone : "";
    }

    /** Returns an unmodifiable view of transactions for safe display in the UI. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Adds a transaction record (internal use by service layer). */
    public void addTransaction(Transaction transaction) {
        transactions.add(Objects.requireNonNull(transaction));
    }
}
