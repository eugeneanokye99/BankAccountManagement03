package account;

import customer.Customer;
import transaction.Transactable;
import utils.CustomUtils;
import exceptions.InsufficientFundsException;

public abstract class Account implements Transactable {
    private final String accountNumber;
    private Customer customer;
    private double balance;
    private String status;

    private static int accountCounter = 0;

    public Account(Customer customer, double openingBalance) {
        this.accountNumber = generateAccountNumber();
        this.customer = customer;
        this.balance = openingBalance;
        this.status = "Active";
    }

    private String generateAccountNumber() {
        accountCounter++;
        return String.format("ACC%03d", accountCounter);
    }

    // Getters and Setters
    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public static int getAccountCounter() { return accountCounter; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setStatus(String status) { this.status = status; }

    // Abstract methods
    public abstract void displayAccountDetails();
    public abstract String getAccountType();

    // Transaction methods
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        balance += amount;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (amount > balance) {
            throw new InsufficientFundsException(accountNumber, balance, amount);
        }
        balance -= amount;
        return true;
    }

    public boolean transfer(Account targetAccount, double amount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account cannot be null");
        }

        if (this == targetAccount) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        // Check if source account has sufficient funds
        if (amount > this.balance) {
            throw new InsufficientFundsException(accountNumber, balance, amount);
        }

        try {
            // Withdraw from source account
            boolean withdrawalSuccess = this.withdraw(amount);
            if (!withdrawalSuccess) {
                return false;
            }

            // Deposit to target account
            targetAccount.deposit(amount);

            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Transfer failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean processTransaction(double amount, String type) {
        try {
            if (type.equalsIgnoreCase("DEPOSIT")) {
                deposit(amount);
                return true;
            } else if (type.equalsIgnoreCase("WITHDRAWAL")) {
                return withdraw(amount);
            }
            throw new IllegalArgumentException("Invalid transaction type: " + type);
        } catch (IllegalArgumentException e) {
            throw e;
        }
    }

}