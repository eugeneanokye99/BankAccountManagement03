package transaction;

import utils.CustomUtils;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionManager {
    // Changed from array to ArrayList
    private final List<Transaction> transactions;

    public TransactionManager() {
        this.transactions = new ArrayList<>(200);
    }


    public TransactionManager(int initialCapacity) {
        this.transactions = new ArrayList<>(initialCapacity);
    }


    // Add transaction to ArrayList
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }



    // View transactions for a specific account (newest first)
    public void viewTransactionsByAccount(String accountNumber) {
        List<Transaction> accountTransactions = getTransactionsForAccount(accountNumber);

        if (accountTransactions.isEmpty()) {
            CustomUtils.print("No transactions found for account: " + accountNumber);
            return;
        }

        // Sort by timestamp in reverse chronological order
        accountTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        CustomUtils.print("\n" + "─".repeat(90));
        CustomUtils.print("TRANSACTION HISTORY - Account: " + accountNumber);
        CustomUtils.print("─".repeat(90));

        CustomUtils.printf("%-10s %-12s %-10s %-12s %-15s %-20s%n",
                "ID", "Account", "Type", "Amount", "Balance After", "Timestamp");
        CustomUtils.print("─".repeat(90));

        for (Transaction transaction : accountTransactions) {
            CustomUtils.printf("%-10s %-12s %-10s $%-11.2f $%-14.2f %-20s%n",
                    transaction.getTransactionId(),
                    transaction.getAccountNumber(),
                    transaction.getType(),
                    transaction.getAmount(),
                    transaction.getBalanceAfter(),
                    transaction.getTimestamp());
        }

        // Display summary
        CustomUtils.print("─".repeat(90));
        double totalDeposits = calculateTotalDeposits(accountNumber);
        double totalWithdrawals = calculateTotalWithdrawals(accountNumber);
        double netChange = totalDeposits - totalWithdrawals;

        CustomUtils.printf("Summary: Total Deposits: $%.2f | Total Withdrawals: $%.2f | Net Change: $%.2f%n",
                totalDeposits, totalWithdrawals, netChange);
        CustomUtils.print("Total Transactions: " + accountTransactions.size());
    }



    // Calculate total deposits for an account
    public double calculateTotalDeposits(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .filter(t -> t.getType().equals("DEPOSIT"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Calculate total withdrawals for an account
    public double calculateTotalWithdrawals(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .filter(t -> t.getType().equals("WITHDRAWAL"))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    // Get transactions for a specific account
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        return transactions.stream()
                .filter(t -> t.getAccountNumber().equals(accountNumber))
                .collect(Collectors.toList());
    }


    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }



}