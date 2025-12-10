package account;

import transaction.Transaction;
import transaction.TransactionManager;
import utils.CustomUtils;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private Account[] accounts;
    private int accountCount;

    public AccountManager() {
        this.accounts = new Account[50];
        this.accountCount = 0;
    }

    public AccountManager(int capacity) {
        this.accounts = new Account[capacity];
        this.accountCount = 0;
    }

    // Add account to array
    public boolean addAccount(Account account) {
        if (accountCount < accounts.length) {
            accounts[accountCount] = account;
            accountCount++;
            return true;
        }
        return false; // Array is full
    }

    // Find account by account number
    public Account findAccount(String accountNumber) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equals(accountNumber)) {
                return accounts[i];
            }
        }
        return null; // Account not found
    }

    // Display all accounts
    public void viewAllAccounts() {
        if (accountCount == 0) {
            CustomUtils.print("No accounts found.");
            return;
        }

        double totalBalance = 0;
        CustomUtils.print("\n" + "─".repeat(80));
        CustomUtils.print("ACCOUNT LISTING");
        CustomUtils.print("─".repeat(80));

        for (int i = 0; i < accountCount; i++) {
            Account account = accounts[i];

            account.displayAccountDetails();

            CustomUtils.print("─".repeat(80));
            totalBalance += account.getBalance();
        }

        CustomUtils.print("Total Accounts: " + accountCount);
        CustomUtils.print("Total Bank Balance: $" + String.format("%.2f", totalBalance));

    }


    // Search accounts by customer name
    public List<Account> searchByCustomerName(String customerName) {
        List<Account> results = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getCustomer().getName().toLowerCase()
                    .contains(customerName.toLowerCase())) {
                results.add(accounts[i]);
            }
        }

        return results;
    }

    // Search accounts by account type
    public List<Account> searchByAccountType(String accountType) {
        List<Account> results = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountType().equals(accountType)) {
                results.add(accounts[i]);
            }
        }

        return results;
    }

    // Get all savings accounts
    public List<Account> getSavingsAccounts() {
        return searchByAccountType("Savings");
    }

    // Get all checking accounts
    public List<Account> getCheckingAccounts() {
        return searchByAccountType("Checking");
    }

    // Get accounts with balance above threshold
    public List<Account> getAccountsWithBalanceAbove(double minBalance) {
        List<Account> results = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getBalance() >= minBalance) {
                results.add(accounts[i]);
            }
        }

        return results;
    }

    // Get accounts with balance below threshold
    public List<Account> getAccountsWithBalanceBelow(double maxBalance) {
        List<Account> results = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getBalance() <= maxBalance) {
                results.add(accounts[i]);
            }
        }

        return results;
    }

    // Get total balance by account type
    public double getTotalBalanceByAccountType(String accountType) {
        double total = 0;

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountType().equals(accountType)) {
                total += accounts[i].getBalance();
            }
        }

        return total;
    }
// Add to AccountManager class


    public String generateAccountStatement(String accountNumber, TransactionManager transactionManager) {
        Account account = findAccount(accountNumber);
        if (account == null) {
            return "Error: Account not found!";
        }

        StringBuilder statement = new StringBuilder();

        // Line 1: Section header
        statement.append("GENERATE ACCOUNT STATEMENT\n");

        // Line 2: Separator (7 underscores as shown in screenshot)
        statement.append("_______".repeat(1)).append("\n");

        // Line 3: Prompt for account number (UI handles this)
        // Blank line after prompt
        statement.append("\n");

        // Line 4: Account info line
        statement.append("Account: ")
                .append(account.getCustomer().getName())
                .append(" (")
                .append(account.getAccountType())
                .append(")\n");

        // Line 5: Current balance
        statement.append("Current Balance: $")
                .append(String.format("%.2f", account.getBalance()))
                .append("\n");

        // Line 6: Blank line
        statement.append("\n");

        // Line 7: Transactions header
        statement.append("Transactions:\n");

        // Line 8: Separator
        statement.append("_______".repeat(1)).append("\n");

        // Get transactions for this account using your existing method
        Transaction[] accountTransactions = transactionManager.getTransactionsForAccount(accountNumber);

        // Sort by timestamp (newest first) - same logic as in viewTransactionsByAccount
        java.util.Arrays.sort(accountTransactions, new java.util.Comparator<Transaction>() {
            @Override
            public int compare(Transaction t1, Transaction t2) {
                return t2.getTimestamp().compareTo(t1.getTimestamp());
            }
        });

        // Lines 9-...: Transaction lines
        if (accountTransactions.length == 0) {
            statement.append("No transactions found.\n");
        } else {
            for (Transaction transaction : accountTransactions) {
                // Determine sign based on transaction type
                String sign;
                if (transaction.getType().equals("DEPOSIT") ||
                        transaction.getType().equals("TRANSFER_IN")) {
                    sign = "+";
                } else {
                    sign = "-";
                }

                // Format: TXN001 | DEPOSIT  | +$1,500.00 | $6,750.00
                statement.append(transaction.getTransactionId())
                        .append(" | ")
                        .append(String.format("%-10s", transaction.getType()))
                        .append(" | ")
                        .append(sign)
                        .append("$")
                        .append(String.format("%.2f", transaction.getAmount()))
                        .append(" | $")
                        .append(String.format("%.2f", transaction.getBalanceAfter()))
                        .append("\n");
            }
        }

        // Last separator line
        statement.append("_______".repeat(1)).append("\n");

        // Calculate net change
        double totalDeposits = transactionManager.calculateTotalDeposits(accountNumber);
        double totalWithdrawals = transactionManager.calculateTotalWithdrawals(accountNumber);
        double netChange = totalDeposits - totalWithdrawals;

        // Net Change line
        String netChangeSign = netChange >= 0 ? "+" : "";
        statement.append("Net Change: ")
                .append(netChangeSign)
                .append("$")
                .append(String.format("%.2f", Math.abs(netChange)))
                .append("\n");

        return statement.toString();
    }



    // Get all accounts (for TransactionManager)
    public Account[] getAccounts() {
        return accounts;
    }

    // Get account count (for TransactionManager)
    public int getActualAccountCount() {
        return accountCount;
    }
}