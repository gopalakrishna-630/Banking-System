# Internship report — Banking Information System prototype

## Introduction

This project documents the design and implementation of a **desktop Banking Information System prototype** developed using **Core Java** and **JavaFX**. The goal was to practise **object-oriented design**, **layered architecture**, and **file-based persistence** in a context similar to a small bank’s internal tooling. The application runs locally, stores customer accounts and transaction histories on disk, and provides a guided graphical workflow from registration through everyday banking tasks.

## Objective

The main objective was to build a **modular, maintainable** system that separates **domain models**, **business rules**, **data access**, and **presentation**. Functional targets included **secure authentication** (prototype level), **account and profile management**, **deposits and withdrawals with balance validation**, **inter-account transfers**, and a **transaction statement**. A secondary objective was to demonstrate **robust input validation** and **user-friendly error messages** so that incorrect amounts or unknown accounts fail safely without corrupting stored data.

## Features

The solution is organised into packages: **`model`** (`UserAccount`, `Transaction`), **`service`** (`BankService`, serialization-backed repository), **`util`** (`ValidationUtil`, `Constants`), and **`ui`** (JavaFX screens). **Registration** collects username, password, and optional initial deposit, then assigns a unique **`ACC` + numeric** account number. **Login** verifies credentials before opening the **dashboard**, where users can **deposit**, **withdraw**, **transfer** (with destination validation and insufficient-fund checks), **view a tabular statement** (date, type, amount, balance after), and **edit profile details**. State is saved using **Java serialization** to `data/bank_state.ser`, satisfying the persistence requirement with simple **HashMap**-backed storage in memory.

## Conclusion

The prototype meets the functional scope with a learner-friendly structure: clear layers, explicit exceptions, and one JavaFX class for login, registration, and dashboard navigation. Future work would add **password hashing**, **audit logging**, and possibly a **database** instead of serialized files. The work ties together **OO design**, **JavaFX**, and **file persistence** in a single coherent banking scenario for demos and extensions.
