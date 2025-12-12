package account;

import transaction.Transaction;
import transaction.TransactionManager;
import utils.CustomUtils;
import java.util.*;
import java.util.stream.Collectors;

public class AccountManager {
    private final Map<String, Account> accounts; // Key: accountNumber, Value: Account
    private final List<Account> accountList; // For maintaining order and easy iteration

    public AccountManager() {
        this.accounts = new HashMap<>();
        this.accountList = new ArrayList<>();
    }

    public AccountManager(int initialCapacity) {
        this.accounts = new HashMap<>(initialCapacity);
        this.accountList = new ArrayList<>(initialCapacity);
    }

    public boolean addAccount(Account account) {
        String accountNumber = account.getAccountNumber();

        // Check if account already exists
        if (accounts.containsKey(accountNumber)) {
            return false;
        }

        accounts.put(accountNumber, account);
        accountList.add(account);
        return true;
    }

    public Account findAccount(String accountNumber) {
        return accounts.get(accountNumber); // Returns null if not found
    }

    // Display all accounts
    public void viewAllAccounts() {
        if (accountList.isEmpty()) {
            CustomUtils.print("No accounts found.");
            return;
        }

        double totalBalance = accountList.stream()
                .mapToDouble(Account::getBalance)
                .sum();

        CustomUtils.print("\n" + "─".repeat(80));
        CustomUtils.print("ACCOUNT LISTING");
        CustomUtils.print("─".repeat(80));

        // Use forEach for printing
        accountList.forEach(account -> {
            account.displayAccountDetails();
            CustomUtils.print("─".repeat(80));
        });

        CustomUtils.print("Total Accounts: " + accountList.size());
        CustomUtils.print("Total Bank Balance: $" + String.format("%.2f", totalBalance));
    }

    // Search accounts by customer name
    public List<Account> searchByCustomerName(String customerName) {
        String searchName = customerName.toLowerCase();

        return accountList.stream()
                .filter(account ->
                        account.getCustomer().getName().toLowerCase().contains(searchName))
                .collect(Collectors.toList());
    }

    // Search accounts by account type
    public List<Account> searchByAccountType(String accountType) {
        return accountList.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .collect(Collectors.toList());
    }


    // Get total balance by account type
    public double getTotalBalanceByAccountType(String accountType) {
        return accountList.stream()
                .filter(account -> account.getAccountType().equals(accountType))
                .mapToDouble(Account::getBalance)
                .sum();
    }

    // Generate account statement
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

        // Get transactions for this account
        List<Transaction> accountTransactions = transactionManager.getTransactionsForAccount(accountNumber);
        accountTransactions.sort((t1, t2) -> t2.getTimestamp().compareTo(t1.getTimestamp()));


        // Lines 9-...: Transaction lines
        if (accountTransactions.isEmpty()) {
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

    // Get all accounts as a List (better than array for collections)
    public List<Account> getAccounts() {
        return new ArrayList<>(accountList);
    }

    // Get account count
    public int getActualAccountCount() {
        return accountList.size();
    }


}