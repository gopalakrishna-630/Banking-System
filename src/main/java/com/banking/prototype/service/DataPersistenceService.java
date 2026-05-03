package com.banking.prototype.service;

import com.banking.prototype.util.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads and saves bank state using Java serialization ({@link ObjectOutputStream}).
 */
public class DataPersistenceService {

    private final Path stateFile;

    public DataPersistenceService(Path stateFile) {
        this.stateFile = stateFile;
    }

    public DataPersistenceService() {
        this(Constants.stateFilePath());
    }

    /**
     * Reads persisted state or returns a fresh repository if the file is missing or corrupt.
     */
    public BankDataRepository loadOrCreate() {
        ensureDataDirectory();
        if (!Files.isRegularFile(stateFile)) {
            return new BankDataRepository();
        }
        try (InputStream in = Files.newInputStream(stateFile);
             ObjectInputStream ois = new ObjectInputStream(in)) {
            Object obj = ois.readObject();
            if (obj instanceof SerializableBankState state && state.getRepository() != null) {
                return state.getRepository();
            }
            if (obj instanceof BankDataRepository repo) {
                return repo;
            }
        } catch (Exception e) {
            // Corrupt file — start fresh but could log in a real app
            System.err.println("Warning: could not load bank data, starting fresh: " + e.getMessage());
        }
        return new BankDataRepository();
    }

    public void save(BankDataRepository repository) throws IOException {
        ensureDataDirectory();
        SerializableBankState state = new SerializableBankState(repository);
        try (OutputStream out = Files.newOutputStream(stateFile);
             ObjectOutputStream oos = new ObjectOutputStream(out)) {
            oos.writeObject(state);
        }
    }

    private void ensureDataDirectory() {
        try {
            Files.createDirectories(stateFile.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Cannot create data directory: " + stateFile.getParent(), e);
        }
    }
}
