package transaction;

import utils.CustomUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private static int transactionCounter = 0;

    private final String transactionId;
    private final String accountNumber;
    private final String type; // "DEPOSIT" or "WITHDRAWAL"
    private final double amount;
    private final double balanceAfter;
    private final String timestamp;
    private String relatedAccount; //for transfers

    public Transaction(String accountNumber, String type, double amount, double balanceAfter) {
        this.transactionId = generateTransactionId();
        this.accountNumber = accountNumber;
        this.type = type.toUpperCase();
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = generateTimestamp();
    }

    // New constructor for transfers
    public Transaction(String accountNumber, String type, double amount, double balanceAfter, String relatedAccount) {
        this.transactionId = generateTransactionId();
        this.accountNumber = accountNumber;
        this.type = type.toUpperCase();
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.timestamp = generateTimestamp();
        this.relatedAccount = relatedAccount;
    }

    private String generateTransactionId() {
        transactionCounter++;
        return String.format("TXN%03d", transactionCounter);
    }

    private String generateTimestamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a");
        return LocalDateTime.now().format(formatter);
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public static int getTransactionCounter() {
        return transactionCounter;
    }


}