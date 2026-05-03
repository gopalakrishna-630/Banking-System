package com.banking.prototype.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Application-wide constants (paths, validation limits).
 */
public final class Constants {

    private Constants() {
    }

    /** Directory under the working directory where persisted data is stored */
    public static final String DATA_DIR_NAME = "data";
    /** Serialized state file name */
    public static final String STATE_FILE_NAME = "bank_state.ser";

    public static Path dataDirectory() {
        return Paths.get(DATA_DIR_NAME);
    }

    public static Path stateFilePath() {
        return dataDirectory().resolve(STATE_FILE_NAME);
    }

    public static final int MIN_USERNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 4;
    public static final String ACCOUNT_NUMBER_PREFIX = "ACC";
}
