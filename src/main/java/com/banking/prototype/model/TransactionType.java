package com.banking.prototype.model;

/**
 * Types of banking transactions recorded in the statement.
 */
public enum TransactionType {
    /** Cash or initial deposit credit */
    DEPOSIT,
    /** Cash withdrawal */
    WITHDRAWAL,
    /** Incoming transfer from another account */
    TRANSFER_IN,
    /** Outgoing transfer to another account */
    TRANSFER_OUT
}
