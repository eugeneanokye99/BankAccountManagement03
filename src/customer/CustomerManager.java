package customer;

import account.Account;
import account.AccountManager;
import java.util.*;

public class CustomerManager {
    private AccountManager accountManager;

    public CustomerManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    // Get all unique customers
    public List<Customer> getAllCustomers() {
        List<Account> accounts = accountManager.getAccounts();
        Set<String> processedCustomerIds = new HashSet<>();
        List<Customer> customers = new ArrayList<>();

        for (Account account : accounts) {
            Customer customer = account.getCustomer();
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
        List<Account> accounts = accountManager.getAccounts();

        for (Account account : accounts) {
            Customer customer = account.getCustomer();
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
        String searchName = name.toLowerCase();

        for (Customer customer : allCustomers) {
            if (customer.getName().toLowerCase().contains(searchName)) {
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
        List<Account> allAccounts = accountManager.getAccounts();
        List<Account> customerAccounts = new ArrayList<>();

        for (Account account : allAccounts) {
            if (account.getCustomer().getCustomerId().equals(customerId)) {
                customerAccounts.add(account);
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

    // Get customers with multiple accounts
    public List<Customer> getCustomersWithMultipleAccounts(int minAccounts) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();

        for (Customer customer : allCustomers) {
            if (getAccountCountForCustomer(customer.getCustomerId()) >= minAccounts) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get customers with high total balance
    public List<Customer> getCustomersWithHighBalance(double minBalance) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();

        for (Customer customer : allCustomers) {
            if (getTotalBalanceForCustomer(customer.getCustomerId()) >= minBalance) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get customer by contact number
    public Customer getCustomerByContact(String contact) {
        List<Account> accounts = accountManager.getAccounts();

        for (Account account : accounts) {
            Customer customer = account.getCustomer();
            if (customer.getContact().equals(contact)) {
                return customer;
            }
        }

        return null;
    }

    // Get customers in age range
    public List<Customer> getCustomersInAgeRange(int minAge, int maxAge) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();

        for (Customer customer : allCustomers) {
            int age = customer.getAge();
            if (age >= minAge && age <= maxAge) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get customers by location (partial address match)
    public List<Customer> getCustomersByLocation(String location) {
        List<Customer> allCustomers = getAllCustomers();
        List<Customer> results = new ArrayList<>();
        String searchLocation = location.toLowerCase();

        for (Customer customer : allCustomers) {
            if (customer.getAddress().toLowerCase().contains(searchLocation)) {
                results.add(customer);
            }
        }

        return results;
    }

    // Get average age of customers
    public double getAverageCustomerAge() {
        List<Customer> allCustomers = getAllCustomers();

        if (allCustomers.isEmpty()) {
            return 0.0;
        }

        int totalAge = 0;
        for (Customer customer : allCustomers) {
            totalAge += customer.getAge();
        }

        return (double) totalAge / allCustomers.size();
    }

    // Get customer with most accounts
    public Customer getCustomerWithMostAccounts() {
        List<Customer> allCustomers = getAllCustomers();

        if (allCustomers.isEmpty()) {
            return null;
        }

        Customer customerWithMostAccounts = allCustomers.get(0);
        int maxAccounts = getAccountCountForCustomer(customerWithMostAccounts.getCustomerId());

        for (Customer customer : allCustomers) {
            int accountCount = getAccountCountForCustomer(customer.getCustomerId());
            if (accountCount > maxAccounts) {
                maxAccounts = accountCount;
                customerWithMostAccounts = customer;
            }
        }

        return customerWithMostAccounts;
    }

    // Get customer with the highest total balance
    public Customer getCustomerWithHighestBalance() {
        List<Customer> allCustomers = getAllCustomers();

        if (allCustomers.isEmpty()) {
            return null;
        }

        Customer richestCustomer = allCustomers.get(0);
        double maxBalance = getTotalBalanceForCustomer(richestCustomer.getCustomerId());

        for (Customer customer : allCustomers) {
            double balance = getTotalBalanceForCustomer(customer.getCustomerId());
            if (balance > maxBalance) {
                maxBalance = balance;
                richestCustomer = customer;
            }
        }

        return richestCustomer;
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