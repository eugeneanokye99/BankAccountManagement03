import java.time.LocalDate;
import java.util.Scanner;
import customer.Customer;
import customer.RegularCustomer;
import customer.PremiumCustomer;
import account.Account;
import account.SavingsAccount;
import account.CheckingAccount;
import account.AccountManager;
import exceptions.ValidationException;
import transaction.TransactionManager;
import transaction.Transaction;
import ui.AccountUI;
import ui.CustomerUI;
import utils.CustomUtils;
import utils.InputService;
import utils.InputValidator;


public class Main {
    private static AccountManager accountManager = new AccountManager();
    private static TransactionManager transactionManager = new TransactionManager();
    private static Scanner scanner = new Scanner(System.in);
    private static AccountUI accountUI;
    private static CustomerUI customerUI;
    static InputService inputService = new InputService(scanner);

    public static void main(String[] args) {
        // Initialize UI components
        accountUI = new AccountUI(accountManager, scanner);
        customerUI = new CustomerUI(accountManager, scanner);


        int choice;
        do {
            displayMainMenu();
            CustomUtils.printInline("Enter choice: ");

            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: createAccount(); break;
                    case 2: accountUI.viewAccountsMenu(); break;
                    case 3: customerUI.viewCustomersMenu(); break;
                    case 4: processTransaction(); break;
                    case 5: viewTransactionHistory(); break;
                    case 6: generateAccountStatement(); break;
                    case 7: runTest(); break;
                    case 8: exitApplication(); break;
                    default: CustomUtils.printError("Invalid choice! Please enter 1-6.");
                }
            } catch (Exception e) {
                CustomUtils.printError("Invalid input! Please enter a number.");
                scanner.nextLine();
                choice = 0;
            }

            if (choice != 8) {
                CustomUtils.printInline("\nPress Enter to continue...");
                scanner.nextLine();
            }

        } while (choice != 8);

        scanner.close();
    }

    private static void runTest() {
        System.out.println("\nRunning tests...\n");
        test.AccountTest.runAllTests();
    }


    private static void generateAccountStatement() {
        CustomUtils.printSection("GENERATE ACCOUNT STATEMENT");

        CustomUtils.printInline("Enter Account Number: ");
        String accountNumber = scanner.nextLine();

        Account account = accountManager.findAccount(accountNumber);
        if (account == null) {
            CustomUtils.printError("Account not found!");
            return;
        }

        CustomUtils.print("\n" + "=".repeat(60));
        CustomUtils.print("ACCOUNT STATEMENT");
        CustomUtils.print("=".repeat(60));

        // Account Information
        CustomUtils.print("\nACCOUNT INFORMATION:");
        CustomUtils.print("Account Number: " + account.getAccountNumber());
        CustomUtils.print("Account Type: " + account.getAccountType());
        CustomUtils.print("Customer: " + account.getCustomer().getName());
        CustomUtils.print("Customer ID: " + account.getCustomer().getCustomerId());
        CustomUtils.print("Status: " + account.getStatus());
        CustomUtils.print("Current Balance: $" + String.format("%.2f", account.getBalance()));

        // Account-specific details
        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            CustomUtils.print("Interest Rate: " + savings.getInterestRate() + "%");
            CustomUtils.print("Minimum Balance: $" + String.format("%.2f", savings.getMinimumBalance()));
            CustomUtils.print("Interest Earned: $" + String.format("%.2f", savings.calculateInterest()));
        } else if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            CustomUtils.print("Overdraft Limit: $" + String.format("%.2f", checking.getOverdraftLimit()));
            CustomUtils.print("Monthly Fee: $" + String.format("%.2f", checking.getMonthlyFee()));
            if (account.getCustomer().getCustomerType().equals("Premium")) {
                CustomUtils.print("Monthly Fee Status: WAIVED");
            }
        }

        CustomUtils.print("\n" + "=".repeat(60));
        CustomUtils.print("Statement Date: " + LocalDate.now());
        CustomUtils.print("=".repeat(60));

        CustomUtils.printSuccess("Account statement generated successfully!");
    }

    private static void displayMainMenu() {
        CustomUtils.printHeader("BANK ACCOUNT MANAGEMENT - MAIN MENU");
        CustomUtils.print("1. Create Account");
        CustomUtils.print("2. View Accounts");
        CustomUtils.print("3. View Customers");
        CustomUtils.print("4. Process Transaction");
        CustomUtils.print("5. View Transaction History");
        CustomUtils.print("6. Generate Account Statements");
        CustomUtils.print("7. Run Tests");
        CustomUtils.print("8. Exit");
        CustomUtils.print();
    }

    private static void createAccount() {
        CustomUtils.printSection("ACCOUNT CREATION");



        try {
            // Get validated name using InputService
            String name = inputService.getName();

            // Get validated age using InputService
            int age = inputService.getAge();

            // Get validated contact
            String contact = inputService.getInputWithValidation(
                    "Enter customer contact (e.g., 0599012817): ",
                    input -> {
                        try {
                            InputValidator.validateContact(input);
                        } catch (ValidationException e) {
                            throw new RuntimeException(e);
                        }
                        return input.trim();
                    }
            );

            // Get validated address
            String address = inputService.getInputWithValidation(
                    "Enter customer address: ",
                    input -> {
                        try {
                            InputValidator.validateAddress(input);
                        } catch (ValidationException e) {
                            throw new RuntimeException(e);
                        }
                        return input.trim();
                    }
            );

            // Get customer type
            CustomUtils.print("\nCustomer type:");
            CustomUtils.print("1. Regular Customer (Standard banking services)");
            CustomUtils.print("2. Premium Customer (Enhanced benefits, min balance $10,000)");

            int customerType;
            while (true) {
                System.out.print("Select type (1-2): ");
                String input = scanner.nextLine().trim();

                try {
                    InputValidator.validateNumericString(input, "Customer type");
                    int type = Integer.parseInt(input);
                    if (type == 1 || type == 2) {
                        customerType = type;
                        break;
                    }
                    CustomUtils.printError("Please enter 1 or 2");
                } catch (ValidationException e) {
                    CustomUtils.printError("Please enter a valid number (1 or 2)");
                }
            }

            Customer customer;
            if (customerType == 1) {
                customer = new RegularCustomer(name, age, contact, address);
            } else {
                customer = new PremiumCustomer(name, age, contact, address);
            }

            // Get account type
            CustomUtils.print("\nAccount type:");
            CustomUtils.print("1. Savings Account (Interest: 3.5%, Min Balance: $500)");
            CustomUtils.print("2. Checking Account (Overdraft: $1,000, Monthly Fee: $10)");

            int accountType;
            while (true) {
                System.out.print("Select type (1-2): ");
                String input = scanner.nextLine().trim();

                try {
                    InputValidator.validateNumericString(input, "Account type");
                    int type = Integer.parseInt(input);
                    if (type == 1 || type == 2) {
                        accountType = type;
                        break;
                    }
                    CustomUtils.printError("Please enter 1 or 2");
                } catch (ValidationException e) {
                    CustomUtils.printError("Please enter a valid number (1 or 2)");
                }
            }

            // Get initial deposit with type-specific validation
            double minDeposit = getMinimumDeposit(customerType, accountType);
            String prompt = String.format("Enter initial deposit amount (minimum $%.2f): $", minDeposit);

            double openingBalance = inputService.getPositiveAmount();

            // Additional validation for minimum deposit
            while (openingBalance < minDeposit) {
                CustomUtils.printError(String.format("Minimum deposit for this account is $%.2f", minDeposit));
                openingBalance = inputService.getPositiveAmount();
            }

            // Create Account
            Account account;
            try {
                if (accountType == 1) {
                    account = new SavingsAccount(customer, openingBalance);
                } else {
                    account = new CheckingAccount(customer, openingBalance);
                }
            } catch (IllegalArgumentException e) {
                CustomUtils.printError("Error creating account: " + e.getMessage());
                return;
            }

            if (accountManager.addAccount(account)) {
                displayAccountCreationSuccess(account, customer);
            } else {
                CustomUtils.printError("Cannot create more accounts. Maximum limit reached.");
            }

        } catch (Exception e) {
            CustomUtils.printError("Unexpected error creating account: " + e.getMessage());
        } 
    }


    private static double getMinimumDeposit(int customerType, int accountType) {
        if (accountType == 1) {
            return 500.0; // Savings minimum
        } else if (customerType == 2) {
            return 10000.0; // Premium Checking minimum
        } else {
            return 0.01; // Regular Checking minimum (just above 0)
        }
    }

    private static void displayAccountCreationSuccess(Account account, Customer customer) {
        CustomUtils.print();
        CustomUtils.printDivider(50);
        CustomUtils.printSuccess("ACCOUNT CREATED SUCCESSFULLY!");
        CustomUtils.printDivider(50);

        CustomUtils.print("\nAccount Details:");
        CustomUtils.printDivider(30);
        CustomUtils.print("Account Number: " + account.getAccountNumber());
        CustomUtils.print("Customer: " + customer.getName() + " (" + customer.getCustomerType() + ")");
        CustomUtils.print("Account Type: " + account.getAccountType());
        CustomUtils.print("Initial Balance: $" + String.format("%.2f", account.getBalance()));

        if (account instanceof SavingsAccount) {
            SavingsAccount savings = (SavingsAccount) account;
            CustomUtils.print("Interest Rate: " + savings.getInterestRate() + "%");
            CustomUtils.print("Minimum Balance: $" + String.format("%.2f", savings.getMinimumBalance()));
        } else if (account instanceof CheckingAccount) {
            CheckingAccount checking = (CheckingAccount) account;
            CustomUtils.print("Overdraft Limit: $" + String.format("%.2f", checking.getOverdraftLimit()));
            CustomUtils.print("Monthly Fee: $" + String.format("%.2f", checking.getMonthlyFee()));
            if (customer.getCustomerType().equals("Premium")) {
                CustomUtils.print("Monthly Fee Status: WAIVED (Premium Customer)");
            }
        }
        CustomUtils.print("Status: " + account.getStatus());
        CustomUtils.printDivider(30);

        CustomUtils.print("\nNext Steps:");
        CustomUtils.print("1. You can now make transactions with account number: " + account.getAccountNumber());
        CustomUtils.print("2. View your account details in the 'View Accounts' section");
    }

    private static void processTransaction() {
        CustomUtils.printSection("PROCESS TRANSACTION");

        try {
            CustomUtils.printInline("Enter Account Number: ");
            String accountNumber = scanner.nextLine();

            Account sourceAccount = accountManager.findAccount(accountNumber);
            if (sourceAccount == null) {
                CustomUtils.printError("Account not found!");
                return;
            }

            // Display account details
            CustomUtils.print("\nAccount Details:");
            CustomUtils.print("Customer: " + sourceAccount.getCustomer().getName());
            CustomUtils.print("Account Type: " + sourceAccount.getAccountType());
            CustomUtils.print("Current Balance: $" + String.format("%.2f", sourceAccount.getBalance()));

            CustomUtils.print("\nTransaction type:");
            CustomUtils.print("1. Deposit");
            CustomUtils.print("2. Withdrawal");
            CustomUtils.print("3. Transfer");
            int transactionType = inputService.getIntInRange(
                    "Select type (1-3): ",
                    1, 3);

            if (transactionType == 3) {
                // Handle transfer
                handleTransfer(sourceAccount);
            } else {
                // Handle deposit/withdrawal
                handleDepositWithdrawal(sourceAccount, transactionType);
            }

        } catch (Exception e) {
            CustomUtils.printError("Error processing transaction: " + e.getMessage());
        }
    }

    private static void handleDepositWithdrawal(Account account, int transactionType) {
        double amount = inputService.getPositiveDouble("Enter amount: $");

        String type = (transactionType == 1) ? "DEPOSIT" : "WITHDRAWAL";
        double previousBalance = account.getBalance();

        // For withdrawals, validate FIRST before showing confirmation
        if (transactionType == 2) { // WITHDRAWAL
            // Check if withdrawal would be valid using a temporary check
            boolean isValidWithdrawal = true;
            String errorMessage = "";

            if (account instanceof SavingsAccount) {
                SavingsAccount savings = (SavingsAccount) account;
                double newBalance = previousBalance - amount;
                if (newBalance < savings.getMinimumBalance()) {
                    isValidWithdrawal = false;
                    errorMessage = String.format("Withdrawal would violate minimum balance of $%.2f",
                            savings.getMinimumBalance());
                }
            } else if (account instanceof CheckingAccount) {
                CheckingAccount checking = (CheckingAccount) account;
                double newBalance = previousBalance - amount;
                double maxNegative = -checking.getOverdraftLimit();
                if (newBalance < maxNegative) {
                    isValidWithdrawal = false;
                    errorMessage = String.format("Withdrawal would exceed overdraft limit of $%.2f",
                            checking.getOverdraftLimit());
                }
            }

            // If withdrawal is invalid, show error and return immediately
            if (!isValidWithdrawal) {
                CustomUtils.printError("Withdrawal not allowed!");
                CustomUtils.printError(errorMessage);
                CustomUtils.print(String.format("Current balance: $%.2f", previousBalance));
                CustomUtils.print(String.format("Requested: $%.2f", amount));
                return; // Exit without showing confirmation
            }
        }

        // Only show confirmation for valid transactions
        double newBalance = (transactionType == 1) ? previousBalance + amount : previousBalance - amount;
        displayTransactionConfirmation(account.getAccountNumber(), type, amount, previousBalance, newBalance);

        boolean confirmed = inputService.getConfirmation("\nConfirm transaction?");

        if (confirmed) {
            try {
                boolean success = account.processTransaction(amount, type);
                if (success) {
                    // Create and record transaction
                    Transaction transaction = new Transaction(account.getAccountNumber(), type, amount, account.getBalance());
                    transactionManager.addTransaction(transaction);

                    CustomUtils.printSuccess("Transaction completed successfully!");
                    CustomUtils.print("New Balance: $" + String.format("%.2f", account.getBalance()));
                } else {
                    CustomUtils.printError("Transaction failed unexpectedly!");
                }
            } catch (IllegalArgumentException e) {
                CustomUtils.printError("Transaction Error: " + e.getMessage());
            }
        } else {
            CustomUtils.print("Transaction cancelled.");
        }
    }

    private static void handleTransfer(Account sourceAccount) {
        CustomUtils.printInline("Enter Target Account Number: ");
        String targetAccountNumber = scanner.nextLine();

        Account targetAccount = accountManager.findAccount(targetAccountNumber);
        if (targetAccount == null) {
            CustomUtils.printError("Target account not found!");
            return;
        }

        if (sourceAccount.getAccountNumber().equals(targetAccountNumber)) {
            CustomUtils.printError("Cannot transfer to the same account!");
            return;
        }

        // Display target account details
        CustomUtils.print("\nTarget Account Details:");
        CustomUtils.print("Customer: " + targetAccount.getCustomer().getName());
        CustomUtils.print("Account Type: " + targetAccount.getAccountType());
        CustomUtils.print("Current Balance: $" + String.format("%.2f", targetAccount.getBalance()));

        double amount = inputService.getPositiveDouble("Enter amount: $");

        // Display transfer confirmation
        displayTransferConfirmation(sourceAccount, targetAccount, amount);

        boolean confirmed = inputService.getConfirmation("\nConfirm transaction?");

        if (confirmed) {
            try {
                boolean success = sourceAccount.transfer(targetAccount, amount);
                if (success) {
                    // Record both transactions (withdrawal from source, deposit to target)
                    Transaction withdrawalTransaction = new Transaction(
                            sourceAccount.getAccountNumber(),
                            "TRANSFER_OUT",
                            amount,
                            sourceAccount.getBalance()
                    );
                    transactionManager.addTransaction(withdrawalTransaction);

                    Transaction depositTransaction = new Transaction(
                            targetAccount.getAccountNumber(),
                            "TRANSFER_IN",
                            amount,
                            targetAccount.getBalance()
                    );
                    transactionManager.addTransaction(depositTransaction);

                    CustomUtils.printSuccess("Transfer completed successfully!");
                    CustomUtils.print("\nSource Account:");
                    CustomUtils.print("New Balance: $" + String.format("%.2f", sourceAccount.getBalance()));
                    CustomUtils.print("\nTarget Account:");
                    CustomUtils.print("New Balance: $" + String.format("%.2f", targetAccount.getBalance()));
                } else {
                    CustomUtils.printError("Transfer failed!");
                }
            } catch (IllegalArgumentException e) {
                CustomUtils.printError("Transfer Error: " + e.getMessage());
            }
        } else {
            CustomUtils.print("Transfer cancelled.");
        }
    }

    private static void displayTransferConfirmation(Account sourceAccount, Account targetAccount, double amount) {
        CustomUtils.print("\nTRANSFER CONFIRMATION");
        CustomUtils.printDivider(40);

        CustomUtils.print("FROM:");
        CustomUtils.print("  Account: " + sourceAccount.getAccountNumber());
        CustomUtils.print("  Customer: " + sourceAccount.getCustomer().getName());
        CustomUtils.print("  Current Balance: $" + String.format("%.2f", sourceAccount.getBalance()));

        CustomUtils.print("\nTO:");
        CustomUtils.print("  Account: " + targetAccount.getAccountNumber());
        CustomUtils.print("  Customer: " + targetAccount.getCustomer().getName());
        CustomUtils.print("  Current Balance: $" + String.format("%.2f", targetAccount.getBalance()));

        CustomUtils.print("\nTRANSFER DETAILS:");
        CustomUtils.print("  Amount: $" + String.format("%.2f", amount));
        CustomUtils.print("  From New Balance: $" + String.format("%.2f", sourceAccount.getBalance() - amount));
        CustomUtils.print("  To New Balance: $" + String.format("%.2f", targetAccount.getBalance() + amount));

        CustomUtils.print("  Date/Time: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));
        CustomUtils.printDivider(40);
    }

    private static void displayTransactionConfirmation(String accountNumber, String type,
                                                       double amount, double previousBalance, double newBalance) {
        CustomUtils.print("\nTRANSACTION CONFIRMATION");
        CustomUtils.printDivider(30);
        CustomUtils.print("Transaction ID: TXN" + String.format("%03d", Transaction.getTransactionCounter() + 1));
        CustomUtils.print("Account: " + accountNumber);
        CustomUtils.print("Type: " + type);
        CustomUtils.print("Amount: $" + String.format("%.2f", amount));
        CustomUtils.print("Previous Balance: $" + String.format("%.2f", previousBalance));
        CustomUtils.print("New Balance: $" + String.format("%.2f", newBalance));
        CustomUtils.print("Date/Time: " + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm a")));
        CustomUtils.printDivider(30);
    }

    private static void viewTransactionHistory() {
        CustomUtils.printSection("VIEW TRANSACTION HISTORY");

        try {
            CustomUtils.printInline("Enter Account Number: ");
            String accountNumber = scanner.nextLine();

            // Verify account exists
            Account account = accountManager.findAccount(accountNumber);
            if (account == null) {
                CustomUtils.printError("Account not found!");
                return;
            }

            transactionManager.viewTransactionsByAccount(accountNumber);

        } catch (Exception e) {
            CustomUtils.printError("Error viewing transaction history: " + e.getMessage());
        }
    }

    private static void exitApplication() {
        CustomUtils.print();
        CustomUtils.printSuccess("Thank you for using Bank Account Management System!");
        CustomUtils.print("Goodbye!");
    }
}