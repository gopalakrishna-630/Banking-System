package com.banking.prototype.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Record of a single banking operation for statement history.
 * Fields are non-final so Java serialization can restore instances reliably.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime timestamp;
    private TransactionType type;
    private double amount;
    private double balanceAfter;
    private String description;

    /** Used only by serialization framework */
    @SuppressWarnings("unused")
    private Transaction() {
    }

    public Transaction(LocalDateTime timestamp, TransactionType type, double amount,
                       double balanceAfter, String description) {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Objects.requireNonNull(timestamp);
        this.type = Objects.requireNonNull(type);
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.description = description != null ? description : "";
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getDescription() {
        return description;
    }
}
