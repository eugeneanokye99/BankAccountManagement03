package ui;

import account.Account;
import account.AccountManager;
import java.util.Scanner;

import  ui.AccountManagerUI;
import utils.CustomUtils;

public class AccountUI {
    private AccountManager accountManager;
    private Scanner scanner;
    private AccountManagerUI accountManagerUI ;

    public AccountUI(AccountManager accountManager, Scanner scanner) {
        this.accountManager = accountManager;
        this.scanner = scanner;
        this.accountManagerUI = new AccountManagerUI(accountManager, scanner);
    }

    // Main entry point for account viewing
    public void viewAccountsMenu() {
        int accountChoice;

        do {
            displayAccountMenu();
            CustomUtils.printInline("Enter choice: ");

            try {
                accountChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (accountChoice) {
                    case 1: viewAllAccounts(); break;
                    case 2: viewAccountDetails(); break;
                    case 3: searchAccount(); break;
                    case 4: accountManagerUI.manageAccounts(); break;
                    case 5: return; // Go back to main menu
                    default: CustomUtils.print("Invalid choice! Please enter 1-5.");
                }

                if (accountChoice != 5) {
                    CustomUtils.printInline("\nPress Enter to continue...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                CustomUtils.print("Invalid input! Please enter a number.");
                scanner.nextLine();
                accountChoice = 0;
            }

        } while (accountChoice != 5);
    }

    private void displayAccountMenu() {
        int width = 50;
        CustomUtils.print();
        CustomUtils.printInline("┌"); for (int i = 0; i < width; i++) CustomUtils.printInline("─"); CustomUtils.print("┐");
        String title = "ACCOUNT MANAGEMENT";
        int padding = (width - title.length()) / 2;
        CustomUtils.printInline("│"); CustomUtils.printInline(" ".repeat(padding)); CustomUtils.printInline(title);
        CustomUtils.printInline(" ".repeat(width - padding - title.length())); CustomUtils.print("│");
        CustomUtils.printInline("└"); for (int i = 0; i < width; i++) CustomUtils.printInline("─"); CustomUtils.print("┘");
        CustomUtils.print("\n1. View All Accounts");
        CustomUtils.print("2. View Account Details");
        CustomUtils.print("3. Search Account");
        CustomUtils.print("4. Manage Accounts");
        CustomUtils.print("5. Back to Main Menu");
        CustomUtils.print();
    }

    public void viewAllAccounts() {
        accountManager.viewAllAccounts();
    }

    public void viewAccountDetails() {
        CustomUtils.print("\n" + "─".repeat(50));
        CustomUtils.print("VIEW ACCOUNT DETAILS");
        CustomUtils.print("─".repeat(50));

        CustomUtils.printInline("Enter Account Number: ");
        String accountNumber = scanner.nextLine();

        Account account = accountManager.findAccount(accountNumber);
        if (account != null) {
            CustomUtils.print("\n=== ACCOUNT DETAILS ===");
            account.displayAccountDetails();
        } else {
            CustomUtils.print("Account not found!");
        }
    }

    private void searchAccount() {
        CustomUtils.print("\n" + "─".repeat(50));
        CustomUtils.print("SEARCH ACCOUNT");
        CustomUtils.print("─".repeat(50));

        CustomUtils.print("Search by:");
        CustomUtils.print("1. Account Number");
        CustomUtils.print("2. Customer Name");
        CustomUtils.print("3. Account Type");
        CustomUtils.printInline("Select option (1-3): ");

        try {
            int searchOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (searchOption) {
                case 1:
                    CustomUtils.printInline("Enter Account Number: ");
                    String accNumber = scanner.nextLine();
                    searchByAccountNumber(accNumber);
                    break;

                case 2:
                    CustomUtils.printInline("Enter Customer Name: ");
                    String customerName = scanner.nextLine();
                    searchByCustomerName(customerName);
                    break;

                case 3:
                    CustomUtils.print("Account Types:");
                    CustomUtils.print("1. Savings");
                    CustomUtils.print("2. Checking");
                    CustomUtils.printInline("Select type (1-2): ");
                    int typeChoice = scanner.nextInt();
                    scanner.nextLine();

                    String accountType = (typeChoice == 1) ? "Savings" : "Checking";
                    searchByAccountType(accountType);
                    break;

                default:
                    CustomUtils.print("Invalid option!");
            }

        } catch (Exception e) {
            CustomUtils.print("Invalid input!");
            scanner.nextLine();
        }
    }

    private void searchByAccountNumber(String accountNumber) {
        Account account = accountManager.findAccount(accountNumber);
        if (account != null) {
            account.displayAccountDetails();
        } else {
            CustomUtils.print("Account not found!");
        }
    }

    private void searchByCustomerName(String customerName) {
        Account[] accounts = accountManager.getAccounts();
        int accountCount = accountManager.getActualAccountCount();

        CustomUtils.print("\nSearch Results for: " + customerName);
        CustomUtils.print("─".repeat(80));

        boolean found = false;
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getCustomer().getName().toLowerCase()
                    .contains(customerName.toLowerCase())) {
                accounts[i].displayAccountDetails();
                CustomUtils.print("─".repeat(40));
                found = true;
            }
        }

        if (!found) {
            CustomUtils.print("No accounts found for customer: " + customerName);
        }
    }

    private void searchByAccountType(String accountType) {
        Account[] accounts = accountManager.getAccounts();
        int accountCount = accountManager.getActualAccountCount();

        CustomUtils.print("\n" + accountType + " Accounts:");
        CustomUtils.print("─".repeat(80));

        int count = 0;
        double totalBalance = 0;

        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountType().equals(accountType)) {
                accounts[i].displayAccountDetails();
                CustomUtils.print("─".repeat(40));
                count++;
                totalBalance += accounts[i].getBalance();
            }
        }

        if (count == 0) {
            CustomUtils.print("No " + accountType + " accounts found.");
        } else {
            CustomUtils.print("─".repeat(80));
            CustomUtils.print("Total " + accountType + " Accounts: " + count);
            CustomUtils.print("Total Balance: $" + String.format("%.2f", totalBalance));
        }
    }


}