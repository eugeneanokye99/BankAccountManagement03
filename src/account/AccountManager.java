package account;

import utils.CustomUtils;

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


    // Get all accounts (for TransactionManager)
    public Account[] getAccounts() {
        return accounts;
    }

    // Get account count (for TransactionManager)
    public int getActualAccountCount() {
        return accountCount;
    }
}