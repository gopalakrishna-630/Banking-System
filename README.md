<<<<<<< HEAD
# BankingSystem
=======
# Banking Information System (Java + JavaFX)

A **prototype desktop banking application** built with **Java 17**, **JavaFX 21**, and a **layered architecture** (`model`, `service`, `ui`, `util`). Data is stored on disk with **Java serialization** under the `data/` directory.

## Prerequisites

- **JDK 17** or newer (`java -version`)
- **Apache Maven 3.6+** (`mvn -version`)

If Maven is not installed, download [Apache Maven](https://maven.apache.org/download.cgi), unpack it, and use the full path to `bin/mvn` in the commands below.

## How to run

From this project directory (`BankingInformationSystem`):

```bash
mvn javafx:run
```

> **Note:** You need a graphical desktop session (JavaFX requires a display). On Linux headless servers, use X11 forwarding or run locally on your PC.

The application creates **`data/bank_state.ser`** on first save (registration or any transaction). Delete that file to reset all stored accounts.

## Project layout (GitHub-ready)

```
BankingInformationSystem/
├── pom.xml
├── README.md
├── .gitignore
├── data/
│   └── .gitkeep
├── docs/
│   ├── INTERNSHIP_REPORT.md
│   ├── SAMPLE_TEST_DATA.md
│   └── SCREENSHOTS_FOR_REPORT.md
└── src/main/java/com/banking/prototype/
    ├── model/          # Entities: UserAccount, Transaction, TransactionType
    ├── service/        # BankService, persistence, exceptions
    ├── ui/             # JavaFX: BankingFxApp
    └── util/           # ValidationUtil, Constants
```

## Main class

`com.banking.prototype.ui.BankingFxApp`

## Features (quick reference)

| Area | Behaviour |
|------|-----------|
| Registration | Username, password, optional initial deposit; unique `ACC########` account number |
| Login | Credential check before dashboard |
| Dashboard | Deposit, Withdraw, Transfer, View statement, Account details (profile), Logout |
| Transfer | Validates destination account; updates both accounts; blocks self-transfer |
| Errors | Validation and domain messages shown in alerts |

See **`docs/SAMPLE_TEST_DATA.md`** for suggested manual test flows and **`docs/SCREENSHOTS_FOR_REPORT.md`** for report figures.

## Security note

Passwords are stored **in plain text** for learning purposes only. A production system would use hashing (e.g. BCrypt) and never store raw passwords.
>>>>>>> 2fdf216 (Initial commit - Banking Information System)
