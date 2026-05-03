package com.banking.prototype.service;

/**
 * Thrown when a withdrawal or transfer would drop the balance below zero.
 */
public class InsufficientFundsException extends BankingException {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
