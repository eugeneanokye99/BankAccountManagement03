package ui;

import customer.Customer;
import customer.CustomerManager;
import account.Account;
import account.AccountManager;
import java.util.Scanner;
import java.util.List;

public class CustomerUI {
    private AccountManager accountManager;
    private CustomerManager customerManager;
    private Scanner scanner;

    public CustomerUI(AccountManager accountManager, Scanner scanner) {
        this.accountManager = accountManager;
        this.customerManager = new CustomerManager(accountManager);
        this.scanner = scanner;
    }

    // Main entry point for customer viewing
    public void viewCustomersMenu() {
        int customerChoice;

        do {
            displayCustomerMenu();
            System.out.print("Enter choice: ");

            try {
                customerChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (customerChoice) {
                    case 1: viewAllCustomers(); break;
                    case 2: viewCustomerDetails(); break;
                    case 3: searchCustomers(); break;
                    case 4: viewCustomerStatistics(); break;
                    case 5: return; // Go back to main menu
                    default: System.out.println("Invalid choice! Please enter 1-5.");
                }

                if (customerChoice != 5) {
                    System.out.print("\nPress Enter to continue...");
                    scanner.nextLine();
                }

            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
                customerChoice = 0;
            }

        } while (customerChoice != 5);
    }

    private void displayCustomerMenu() {
        int width = 50;
        System.out.println();
        System.out.print("┌"); for (int i = 0; i < width; i++) System.out.print("─"); System.out.println("┐");
        String title = "CUSTOMER MANAGEMENT";
        int padding = (width - title.length()) / 2;
        System.out.print("│"); System.out.print(" ".repeat(padding)); System.out.print(title);
        System.out.print(" ".repeat(width - padding - title.length())); System.out.println("│");
        System.out.print("└"); for (int i = 0; i < width; i++) System.out.print("─"); System.out.println("┘");
        System.out.println("\n1. View All Customers");
        System.out.println("2. View Customer Details");
        System.out.println("3. Search Customers");
        System.out.println("4. View Customer Statistics");
        System.out.println("5. Back to Main Menu");
        System.out.println();
    }

    public void viewAllCustomers() {
        List<Customer> customers = customerManager.getAllCustomers();

        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }

        System.out.println("\n" + "─".repeat(80));
        System.out.println("ALL CUSTOMERS");
        System.out.println("─".repeat(80));

        System.out.printf("%-10s %-20s %-10s %-15s %-12s %-15s%n",
                "ID", "Name", "Age", "Contact", "Type", "Accounts");
        System.out.println("─".repeat(80));

        for (Customer customer : customers) {
            int accountCount = customerManager.getAccountCountForCustomer(customer.getCustomerId());

            System.out.printf("%-10s %-20s %-10d %-15s %-12s %-15s%n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAge(),
                    customer.getContact(),
                    customer.getCustomerType(),
                    accountCount + " account(s)");
        }

        System.out.println("─".repeat(80));

        // Show statistics
        CustomerManager.CustomerStatistics stats = customerManager.getCustomerStatistics();
        System.out.printf("Total Customers: %d | Regular: %d | Premium: %d%n",
                stats.getTotalCustomers(),
                stats.getRegularCustomers(),
                stats.getPremiumCustomers());
    }

    private void viewCustomerDetails() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("VIEW CUSTOMER DETAILS");
        System.out.println("─".repeat(50));

        System.out.print("Enter Customer ID: ");
        String customerId = scanner.nextLine().trim();

        viewCustomerDetailsById(customerId);
    }

    public void viewCustomerDetailsById(String customerId) {
        Customer customer = customerManager.getCustomerById(customerId);

        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        List<Account> customerAccounts = customerManager.getAccountsForCustomer(customerId);
        double totalBalance = customerManager.getTotalBalanceForCustomer(customerId);

        // Display customer details
        System.out.println("\n" + "─".repeat(60));
        System.out.println("CUSTOMER DETAILS");
        System.out.println("─".repeat(60));

        customer.displayCustomerDetails();

        // Display customer's accounts
        System.out.println("\nCustomer's Accounts:");
        System.out.println("─".repeat(60));

        if (customerAccounts.isEmpty()) {
            System.out.println("No accounts found for this customer.");
        } else {
            for (Account account : customerAccounts) {
                System.out.printf("%s | %s | Balance: $%.2f | Status: %s%n",
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance(),
                        account.getStatus());
            }
            System.out.println("─".repeat(60));
            System.out.printf("Total Accounts: %d | Total Balance: $%.2f%n",
                    customerAccounts.size(), totalBalance);
        }
    }

    private void searchCustomers() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("SEARCH CUSTOMERS");
        System.out.println("─".repeat(50));

        System.out.println("Search by:");
        System.out.println("1. Customer Name");
        System.out.println("2. Customer ID");
        System.out.println("3. Customer Type");
        System.out.print("Select option (1-3): ");

        try {
            int searchOption = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (searchOption) {
                case 1:
                    System.out.print("Enter Customer Name: ");
                    String customerName = scanner.nextLine();
                    searchCustomersByName(customerName);
                    break;

                case 2:
                    System.out.print("Enter Customer ID: ");
                    String customerId = scanner.nextLine();
                    viewCustomerDetailsById(customerId);
                    break;

                case 3:
                    System.out.println("Customer Types:");
                    System.out.println("1. Regular");
                    System.out.println("2. Premium");
                    System.out.print("Select type (1-2): ");
                    int typeChoice = scanner.nextInt();
                    scanner.nextLine();

                    String customerType = (typeChoice == 1) ? "Regular" : "Premium";
                    searchCustomersByType(customerType);
                    break;

                default:
                    System.out.println("Invalid option!");
            }

        } catch (Exception e) {
            System.out.println("Invalid input!");
            scanner.nextLine();
        }
    }

    private void searchCustomersByName(String customerName) {
        List<Customer> results = customerManager.searchCustomersByName(customerName);

        System.out.println("\nSearch Results for: " + customerName);
        System.out.println("─".repeat(80));

        if (results.isEmpty()) {
            System.out.println("No customers found with name: " + customerName);
            return;
        }

        System.out.printf("%-10s %-20s %-10s %-15s %-12s%n",
                "ID", "Name", "Age", "Contact", "Type");
        System.out.println("─".repeat(80));

        for (Customer customer : results) {
            System.out.printf("%-10s %-20s %-10d %-15s %-12s%n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAge(),
                    customer.getContact(),
                    customer.getCustomerType());
        }

        System.out.println("─".repeat(80));
        System.out.println("Found " + results.size() + " customer(s)");
    }

    private void searchCustomersByType(String customerType) {
        List<Customer> customers = customerManager.getCustomersByType(customerType);

        System.out.println("\n" + customerType + " Customers:");
        System.out.println("─".repeat(80));

        if (customers.isEmpty()) {
            System.out.println("No " + customerType.toLowerCase() + " customers found.");
            return;
        }

        System.out.printf("%-10s %-20s %-10s %-15s %-12s%n",
                "ID", "Name", "Age", "Contact", "Accounts");
        System.out.println("─".repeat(80));

        for (Customer customer : customers) {
            int accountCount = customerManager.getAccountCountForCustomer(customer.getCustomerId());

            System.out.printf("%-10s %-20s %-10d %-15s %-12s%n",
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getAge(),
                    customer.getContact(),
                    accountCount + " account(s)");
        }

        System.out.println("─".repeat(80));
        System.out.println("Total " + customerType + " Customers: " + customers.size());
    }

    private void viewCustomerStatistics() {
        CustomerManager.CustomerStatistics stats = customerManager.getCustomerStatistics();
        stats.displayStatistics();
    }
}