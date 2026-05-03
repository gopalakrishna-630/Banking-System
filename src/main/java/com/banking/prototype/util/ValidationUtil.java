package com.banking.prototype.util;

import com.banking.prototype.service.BankingException;

/**
 * Centralised validation for user input (registration, amounts, account numbers).
 */
public final class ValidationUtil {

    private ValidationUtil() {
    }

    public static String requireNonBlank(String value, String fieldLabel) throws BankingException {
        if (value == null || value.isBlank()) {
            throw new BankingException(fieldLabel + " cannot be empty.");
        }
        return value.trim();
    }

    public static void validateUsername(String username) throws BankingException {
        String u = requireNonBlank(username, "Username");
        if (u.length() < Constants.MIN_USERNAME_LENGTH) {
            throw new BankingException("Username must be at least " + Constants.MIN_USERNAME_LENGTH + " characters.");
        }
        if (!u.matches("[a-zA-Z0-9_]+")) {
            throw new BankingException("Username may only contain letters, digits, and underscores.");
        }
    }

    public static void validatePassword(String password) throws BankingException {
        String p = requireNonBlank(password, "Password");
        if (p.length() < Constants.MIN_PASSWORD_LENGTH) {
            throw new BankingException("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters.");
        }
    }

    /**
     * Parses and validates a monetary amount ({@code > 0}).
     */
    public static double parsePositiveAmount(String text) throws BankingException {
        requireNonBlank(text, "Amount");
        double value;
        try {
            value = Double.parseDouble(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new BankingException("Please enter a valid number for the amount.");
        }
        if (value <= 0) {
            throw new BankingException("Amount must be greater than zero.");
        }
        if (!Double.isFinite(value)) {
            throw new BankingException("Invalid amount.");
        }
        // Round to 2 decimals for currency-like behaviour
        return Math.round(value * 100.0) / 100.0;
    }

    /**
     * Parses initial deposit — allows zero or positive.
     */
    public static double parseNonNegativeAmount(String text) throws BankingException {
        if (text == null || text.isBlank()) {
            return 0.0;
        }
        double value;
        try {
            value = Double.parseDouble(text.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            throw new BankingException("Please enter a valid number for the initial deposit.");
        }
        if (value < 0) {
            throw new BankingException("Initial deposit cannot be negative.");
        }
        if (!Double.isFinite(value)) {
            throw new BankingException("Invalid amount.");
        }
        return Math.round(value * 100.0) / 100.0;
    }

    public static void validateAccountNumberFormat(String accountNumber) throws BankingException {
        String a = requireNonBlank(accountNumber, "Account number");
        if (!a.startsWith(Constants.ACCOUNT_NUMBER_PREFIX)) {
            throw new BankingException("Account number must start with " + Constants.ACCOUNT_NUMBER_PREFIX + ".");
        }
        if (a.length() < 6) {
            throw new BankingException("Account number appears too short.");
        }
    }
}
