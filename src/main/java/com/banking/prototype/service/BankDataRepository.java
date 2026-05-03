package com.banking.prototype.service;

import com.banking.prototype.model.UserAccount;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In-memory repository backed by {@link HashMap} for fast lookup.
 * Keys: username (primary), and we resolve by account number via scan.
 * Persisted as part of {@link SerializableBankState}.
 */
public class BankDataRepository implements Serializable {

    private static final long serialVersionUID = 1L;

    /** username (lowercase) -> account */
    private final Map<String, UserAccount> accountsByUsername;
    /** Monotonic counter used to build unique account numbers */
    private long nextAccountSequence;

    public BankDataRepository() {
        this.accountsByUsername = new HashMap<>();
        this.nextAccountSequence = 1;
    }

    public boolean usernameExists(String username) {
        return accountsByUsername.containsKey(normalize(username));
    }

    public void put(UserAccount account) {
        accountsByUsername.put(normalize(account.getUsername()), account);
    }

    public Optional<UserAccount> findByUsername(String username) {
        return Optional.ofNullable(accountsByUsername.get(normalize(username)));
    }

    public Optional<UserAccount> findByAccountNumber(String accountNumber) {
        String target = accountNumber != null ? accountNumber.trim() : "";
        return accountsByUsername.values().stream()
                .filter(a -> a.getAccountNumber().equalsIgnoreCase(target))
                .findFirst();
    }

    public List<UserAccount> allAccounts() {
        return new ArrayList<>(accountsByUsername.values());
    }

    public long nextSequenceAndIncrement() {
        return nextAccountSequence++;
    }

    public void setNextAccountSequence(long nextAccountSequence) {
        this.nextAccountSequence = Math.max(1, nextAccountSequence);
    }

    private static String normalize(String username) {
        return username != null ? username.trim().toLowerCase() : "";
    }
}
