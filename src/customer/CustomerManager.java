package customer;

import account.Account;
import account.AccountManager;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
    private AccountManager accountManager;

    public CustomerManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // Get all unique customers
    public List<Customer> getAllCustomers() {
        Account[] accounts = accountManager.getAccounts();
        int accountCount = accountManager.getActualAccountCount();

        HashSet<String> processedCustomerIds = new HashSet<>();
        List<Customer> customers = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            Customer customer = accounts[i].getCustomer();
            String customerId = customer.getCustomerId();

            if (!processedCustomerIds.contains(customerId)) {
                customers.add(customer);
                processedCustomerIds.add(customerId);
            }
        }

        return customers;
    }

    // Get customer by ID
    public Customer getCustomerById(String customerId) {
        Account[] accounts = accountManager.getAccounts();
        int accountCount = accountManager.getActualAccountCount();

        for (int i = 0; i < accountCount; i++) {
            Customer customer = accounts[i].getCustomer();
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }

        return null;
    }

    // Search customers by name
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();

        for (Customer customer : allCustomers) {
            if (customer.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get customers by type
    public List<Customer> getCustomersByType(String customerType) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();

        for (Customer customer : allCustomers) {
            if (customer.getCustomerType().equals(customerType)) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get all accounts for a customer
    public List<Account> getAccountsForCustomer(String customerId) {
        Account[] accounts = accountManager.getAccounts();
        int accountCount = accountManager.getActualAccountCount();
        List<Account> customerAccounts = new ArrayList<>();

        for (int i = 0; i < accountCount; i++) {
            Customer customer = accounts[i].getCustomer();
            if (customer.getCustomerId().equals(customerId)) {
                customerAccounts.add(accounts[i]);
            }
        }

        return customerAccounts;
    }

    // Get account count for a customer
    public int getAccountCountForCustomer(String customerId) {
        return getAccountsForCustomer(customerId).size();
    }

    // Get total balance for a customer
    public double getTotalBalanceForCustomer(String customerId) {
        List<Account> accounts = getAccountsForCustomer(customerId);
        double totalBalance = 0;

        for (Account account : accounts) {
            totalBalance += account.getBalance();
        }

        return totalBalance;
    }

    // Get customer statistics
    public CustomerStatistics getCustomerStatistics() {
        List<Customer> allCustomers = getAllCustomers();
        int regularCount = 0;
        int premiumCount = 0;
        int totalAccounts = 0;

        for (Customer customer : allCustomers) {
            if (customer.getCustomerType().equals("Regular")) {
                regularCount++;
            } else {
                premiumCount++;
            }
            totalAccounts += getAccountCountForCustomer(customer.getCustomerId());
        }

        return new CustomerStatistics(
                allCustomers.size(),
                regularCount,
                premiumCount,
                totalAccounts
        );
    }

    // Check if customer exists
    public boolean customerExists(String customerId) {
        return getCustomerById(customerId) != null;
    }

    // Inner class for statistics
    public static class CustomerStatistics {
        private int totalCustomers;
        private int regularCustomers;
        private int premiumCustomers;
        private int totalAccounts;

        public CustomerStatistics(int totalCustomers, int regularCustomers,
                                  int premiumCustomers, int totalAccounts) {
            this.totalCustomers = totalCustomers;
            this.regularCustomers = regularCustomers;
            this.premiumCustomers = premiumCustomers;
            this.totalAccounts = totalAccounts;
        }

        public int getTotalCustomers() { return totalCustomers; }
        public int getRegularCustomers() { return regularCustomers; }
        public int getPremiumCustomers() { return premiumCustomers; }
        public int getTotalAccounts() { return totalAccounts; }

        public double getAverageAccountsPerCustomer() {
            return totalCustomers > 0 ? (double) totalAccounts / totalCustomers : 0;
        }

        public void displayStatistics() {
            System.out.println("\n" + "─".repeat(50));
            System.out.println("CUSTOMER STATISTICS");
            System.out.println("─".repeat(50));
            System.out.printf("Total Customers: %d%n", totalCustomers);
            System.out.printf("Regular Customers: %d%n", regularCustomers);
            System.out.printf("Premium Customers: %d%n", premiumCustomers);
            System.out.printf("Total Accounts: %d%n", totalAccounts);
            System.out.printf("Average Accounts per Customer: %.1f%n", getAverageAccountsPerCustomer());
            System.out.println("─".repeat(50));
        }
    }
}