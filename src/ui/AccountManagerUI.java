package ui;

import account.Account;
import account.AccountManager;
import account.CheckingAccount;
import account.SavingsAccount;
import utils.CustomUtils;
import utils.InputService;
import utils.InputValidator;
import exceptions.ValidationException;

import java.time.LocalDate;
import java.util.Scanner;

public class AccountManagerUI {
    private AccountManager accountManager;
    private Scanner scanner;

    // Constructor to inject dependencies
    public AccountManagerUI(AccountManager accountManager, Scanner scanner) {
        this.accountManager = accountManager;
        this.scanner = scanner;
    }

    // Main entry point
    public void manageAccounts() {
        InputService inputService = new InputService(scanner);
        int manageChoice;

        do {
            displayManageAccountsMenu();
            CustomUtils.printInline("Enter choice: ");

            try {
                manageChoice = scanner.nextInt();
                scanner.nextLine();

                switch (manageChoice) {
                    case 1: updateAccountStatus(); break;
                    case 2: updateAccountInformation(); break;
                    case 3: return; // Go back
                    default: CustomUtils.printError("Invalid choice! Please enter 1-3.");
                }

                if (manageChoice != 3) {
                    CustomUtils.printInline("\nPress Enter to continue...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                CustomUtils.printError("Invalid input! Please enter a number.");
                scanner.nextLine();
                manageChoice = 0;
            }

        } while (manageChoice != 3);
    }

    private void displayManageAccountsMenu() {
        CustomUtils.printHeader("MANAGE ACCOUNTS");
        CustomUtils.print("1. Update Account Status");
        CustomUtils.print("2. Update Account Information");
        CustomUtils.print("3. Back to Account Menu");
        CustomUtils.print();
    }

    // 1. Update Account Status
    private void updateAccountStatus() {
        CustomUtils.printSection("UPDATE ACCOUNT STATUS");
        InputService inputService = new InputService(scanner);

        String accountNumber = inputService.getAccountNumber("Enter Account Number: ");

        Account account = accountManager.findAccount(accountNumber);
        if (account == null) {
            CustomUtils.printError("Account not found!");
            return;
        }

        CustomUtils.print("\nCurrent Status: " + account.getStatus());
        CustomUtils.print("\nSelect new status:");
        CustomUtils.print("1. Active");
        CustomUtils.print("2. Inactive");
        CustomUtils.print("3. Closed");

        int statusChoice = inputService.getIntInRange(
                "Select status (1-3): ",
                1, 3);

        String newStatus = switch (statusChoice) {
            case 1 -> "Active";
            case 2 -> "Inactive";
            case 3 -> "Closed";
            default -> "Active";
        };

        // Check if account can be closed (balance should be zero)
        if (newStatus.equals("Closed") && account.getBalance() > 0) {
            CustomUtils.printError("Cannot close account with balance. Withdraw all funds first.");
            return;
        }

        boolean confirm = inputService.getConfirmation(
                "Change status from '" + account.getStatus() + "' to '" + newStatus + "'?");

        if (confirm) {
            account.setStatus(newStatus);
            CustomUtils.printSuccess("Account status updated successfully!");
            CustomUtils.print("New Status: " + account.getStatus());
        } else {
            CustomUtils.print("Status update cancelled.");
        }
    }

    // 2. Update Account Information
    private void updateAccountInformation() {
        CustomUtils.printSection("UPDATE ACCOUNT INFORMATION");
        InputService inputService = new InputService(scanner);

        String accountNumber = inputService.getAccountNumber("Enter Account Number: ");

        Account account = accountManager.findAccount(accountNumber);
        if (account == null) {
            CustomUtils.printError("Account not found!");
            return;
        }

        CustomUtils.print("\nWhat would you like to update?");
        CustomUtils.print("1. Customer Contact Information");
        if (account instanceof CheckingAccount) {
            CustomUtils.print("2. Overdraft Limit");
        }
        CustomUtils.print("3. Cancel");

        int maxOption = (account instanceof CheckingAccount) ? 3 : 2;
        int updateChoice = inputService.getIntInRange(
                "Select option (1-" + maxOption + "): ",
                1, maxOption);

        switch (updateChoice) {
            case 1:
                updateContactInformation(account);
                break;
            case 2:
                if (account instanceof CheckingAccount) {
                    updateOverdraftLimit((CheckingAccount) account);
                } else {
                    CustomUtils.printError("Invalid option for this account type.");
                }
                break;
            case 3:
                CustomUtils.print("Update cancelled.");
                break;
        }
    }

    private void updateContactInformation(Account account) {
        InputService inputService = new InputService(scanner);

        CustomUtils.print("\nCurrent Contact: " + account.getCustomer().getContact());

        String newContact = inputService.getContact();

        CustomUtils.printSuccess("Contact information update functionality requires Customer class modification.");
        CustomUtils.print("New contact would be: " + newContact);
    }

    private void updateOverdraftLimit(CheckingAccount checkingAccount) {
        InputService inputService = new InputService(scanner);

        CustomUtils.print("\nCurrent Overdraft Limit: $" +
                String.format("%.2f", checkingAccount.getOverdraftLimit()));

        double newLimit = inputService.getPositiveDouble("Enter new overdraft limit ($): ");

        CustomUtils.printSuccess("Overdraft limit update functionality requires CheckingAccount class modification.");
        CustomUtils.print("New limit would be: $" + String.format("%.2f", newLimit));
    }
}