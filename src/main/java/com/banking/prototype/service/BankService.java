package com.banking.prototype.service;

import com.banking.prototype.model.Transaction;
import com.banking.prototype.model.TransactionType;
import com.banking.prototype.model.UserAccount;
import com.banking.prototype.util.Constants;
import com.banking.prototype.util.ValidationUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Application service: registration, authentication, balance operations, transfers, and profile updates.
 * Uses {@link BankDataRepository} for storage and {@link DataPersistenceService} for durability.
 */
public class BankService {

    private final BankDataRepository repository;
    private final DataPersistenceService persistence;

    public BankService(BankDataRepository repository, DataPersistenceService persistence) {
        this.repository = Objects.requireNonNull(repository);
        this.persistence = Objects.requireNonNull(persistence);
    }

    /** Convenience factory: load from disk */
    public static BankService createDefault() {
        DataPersistenceService dp = new DataPersistenceService();
        return new BankService(dp.loadOrCreate(), dp);
    }

    private void persist() throws BankingException {
        try {
            persistence.save(repository);
        } catch (IOException e) {
            throw new BankingException("Could not save data to disk. Please try again.", e);
        }
    }

    /**
     * Registers a new user, generates a unique account number, records opening balance as a transaction.
     */
    public UserAccount register(String username, String password, double initialDeposit)
            throws BankingException {
        ValidationUtil.validateUsername(username);
        ValidationUtil.validatePassword(password);
        if (initialDeposit < 0) {
            throw new BankingException("Initial deposit cannot be negative.");
        }
        if (repository.usernameExists(username)) {
            throw new BankingException("That username is already registered.");
        }

        long seq = repository.nextSequenceAndIncrement();
        String accountNumber = Constants.ACCOUNT_NUMBER_PREFIX + String.format("%08d", seq);
        UserAccount account = new UserAccount(username.trim(), password, accountNumber, 0.0);
        account.setBalance(initialDeposit);

        if (initialDeposit > 0) {
            Transaction opening = new Transaction(
                    LocalDateTime.now(),
                    TransactionType.DEPOSIT,
                    initialDeposit,
                    initialDeposit,
                    "Opening deposit"
            );
            account.addTransaction(opening);
        }

        repository.put(account);
        persist();
        return account;
    }

    /** Validates credentials and returns the account if successful. */
    public UserAccount login(String username, String password) throws BankingException {
        ValidationUtil.requireNonBlank(username, "Username");
        ValidationUtil.requireNonBlank(password, "Password");
        Optional<UserAccount> opt = repository.findByUsername(username);
        if (opt.isEmpty() || !opt.get().getPassword().equals(password)) {
            throw new BankingException("Invalid username or password.");
        }
        return opt.get();
    }

    public UserAccount refreshAccount(UserAccount loggedIn) throws BankingException {
        return repository.findByUsername(loggedIn.getUsername())
                .orElseThrow(() -> new BankingException("Session expired. Please log in again."));
    }

    public void deposit(UserAccount user, double amount) throws BankingException {
        UserAccount acc = mustFind(user);
        acc.setBalance(acc.getBalance() + amount);
        acc.addTransaction(new Transaction(
                LocalDateTime.now(),
                TransactionType.DEPOSIT,
                amount,
                acc.getBalance(),
                "Deposit"
        ));
        persist();
    }

    public void withdraw(UserAccount user, double amount) throws BankingException, InsufficientFundsException {
        UserAccount acc = mustFind(user);
        if (acc.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Insufficient balance. Available: " + String.format("%.2f", acc.getBalance()));
        }
        acc.setBalance(acc.getBalance() - amount);
        acc.addTransaction(new Transaction(
                LocalDateTime.now(),
                TransactionType.WITHDRAWAL,
                amount,
                acc.getBalance(),
                "Withdrawal"
        ));
        persist();
    }

    /**
     * Transfers funds between two distinct accounts; updates both histories.
     */
    public void transfer(UserAccount fromUser, String toAccountNumber, double amount) throws BankingException,
            InsufficientFundsException {
        ValidationUtil.validateAccountNumberFormat(toAccountNumber);
        UserAccount from = mustFind(fromUser);
        UserAccount to = repository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new BankingException("Destination account number was not found."));

        if (from.getAccountNumber().equalsIgnoreCase(to.getAccountNumber())) {
            throw new BankingException("You cannot transfer to your own account.");
        }
        if (from.getBalance() < amount) {
            throw new InsufficientFundsException(
                    "Insufficient balance for this transfer. Available: "
                            + String.format("%.2f", from.getBalance()));
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        String refOut = "To " + to.getAccountNumber();
        String refIn = "From " + from.getAccountNumber();

        from.addTransaction(new Transaction(
                LocalDateTime.now(),
                TransactionType.TRANSFER_OUT,
                amount,
                from.getBalance(),
                refOut
        ));
        to.addTransaction(new Transaction(
                LocalDateTime.now(),
                TransactionType.TRANSFER_IN,
                amount,
                to.getBalance(),
                refIn
        ));

        persist();
    }

    public void updateProfile(UserAccount user, String fullName, String email, String phone)
            throws BankingException {
        UserAccount acc = mustFind(user);
        acc.setFullName(fullName != null ? fullName.trim() : "");
        acc.setEmail(email != null ? email.trim() : "");
        acc.setPhone(phone != null ? phone.trim() : "");
        persist();
    }

    public List<Transaction> transactionHistory(UserAccount user) throws BankingException {
        return List.copyOf(mustFind(user).getTransactions());
    }

    private UserAccount mustFind(UserAccount user) throws BankingException {
        return repository.findByUsername(user.getUsername())
                .orElseThrow(() -> new BankingException("Account not found."));
    }

    /** Package-visible for tests / admin tools */
    BankDataRepository getRepository() {
        return repository;
    }
}
