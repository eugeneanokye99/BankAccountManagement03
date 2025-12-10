package transaction;

import utils.CustomUtils;
import java.util.*;

public class TransactionManager {
    // Changed from array to ArrayList
    private List<Transaction> transactions;

    public TransactionManager() {
        this.transactions = new ArrayList<>(200); // Initial capacity
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
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber) &&
                    transaction.getType().equals("DEPOSIT")) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    // Calculate total withdrawals for an account
    public double calculateTotalWithdrawals(String accountNumber) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber) &&
                    transaction.getType().equals("WITHDRAWAL")) {
                total += transaction.getAmount();
            }
        }
        return total;
    }

    // Get transactions for a specific account
    public List<Transaction> getTransactionsForAccount(String accountNumber) {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getAccountNumber().equals(accountNumber)) {
                result.add(transaction);
            }
        }

        return result;
    }

    // Get transactions for a specific account as array (for backward compatibility)
    public Transaction[] getTransactionsForAccountAsArray(String accountNumber) {
        List<Transaction> list = getTransactionsForAccount(accountNumber);
        return list.toArray(new Transaction[0]);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions); // Return copy for safety
    }

    // Get transaction count
    public int getTransactionCount() {
        return transactions.size();
    }

    // Find transaction by ID
    public Transaction findTransactionById(String transactionId) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionId().equals(transactionId)) {
                return transaction;
            }
        }
        return null;
    }

    // Get transactions by type
    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> result = new ArrayList<>();

        for (Transaction transaction : transactions) {
            if (transaction.getType().equals(type)) {
                result.add(transaction);
            }
        }

        return result;
    }

    // Get transactions within a date range (simplified - would need proper date parsing)
    public List<Transaction> getTransactionsInDateRange(String startDate, String endDate) {
        List<Transaction> result = new ArrayList<>();

        // Note: This is a simplified version. In real implementation,
        // you would parse the dates and compare properly
        for (Transaction transaction : transactions) {
            String timestamp = transaction.getTimestamp();
            // Add date comparison logic here
            result.add(transaction); // Placeholder
        }

        return result;
    }

    // Clear all transactions (useful for testing)
    public void clearAllTransactions() {
        transactions.clear();
    }

    // Get total amount of all transactions
    public double getTotalTransactionAmount() {
        double total = 0;
        for (Transaction transaction : transactions) {
            total += transaction.getAmount();
        }
        return total;
    }

    // Get unique account numbers that have transactions
    public Set<String> getAccountsWithTransactions() {
        Set<String> accountNumbers = new HashSet<>();

        for (Transaction transaction : transactions) {
            accountNumbers.add(transaction.getAccountNumber());
        }

        return accountNumbers;
    }

    // Get latest transaction for an account
    public Transaction getLatestTransactionForAccount(String accountNumber) {
        List<Transaction> accountTransactions = getTransactionsForAccount(accountNumber);

        if (accountTransactions.isEmpty()) {
            return null;
        }

        // Sort by timestamp (newest first)
        accountTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));

        return accountTransactions.get(0);
    }
}