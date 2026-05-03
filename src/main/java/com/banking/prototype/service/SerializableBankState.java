package com.banking.prototype.service;

import java.io.Serializable;

/**
 * Wrapper for serialization — holds the repository snapshot saved to disk.
 */
public class SerializableBankState implements Serializable {

    private static final long serialVersionUID = 1L;

    private BankDataRepository repository;

    public SerializableBankState() {
        this.repository = new BankDataRepository();
    }

    public SerializableBankState(BankDataRepository repository) {
        this.repository = repository;
    }

    public BankDataRepository getRepository() {
        return repository;
    }

    public void setRepository(BankDataRepository repository) {
        this.repository = repository;
    }
}
