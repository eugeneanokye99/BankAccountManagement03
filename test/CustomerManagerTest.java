
import account.Account;
import account.AccountManager;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.CustomerManager;
import customer.PremiumCustomer;
import customer.RegularCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerManagerTest {
    private AccountManager accountManager;
    private CustomerManager customerManager;
    private Customer regularCustomer;
    private Customer premiumCustomer;

    @BeforeEach
    void setUp() {
        accountManager = new AccountManager();
        customerManager = new CustomerManager(accountManager);

        regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
        premiumCustomer = new PremiumCustomer("Jane Smith", 35, "0557654321", "Kumasi");

        Account checkingAccount1 = new CheckingAccount(regularCustomer, 1000.0);
        Account savingsAccount1 = new SavingsAccount(regularCustomer, 2000.0);
        Account checkingAccount2 = new CheckingAccount(premiumCustomer, 5000.0);

        accountManager.addAccount(checkingAccount1);
        accountManager.addAccount(savingsAccount1);
        accountManager.addAccount(checkingAccount2);
    }

    @Test
    @DisplayName("CustomerManager constructor should initialize with AccountManager")
    void constructorInitializesCorrectly() {
        assertNotNull(customerManager);
    }

    @Test
    @DisplayName("Get all customers should return unique customers")
    void getAllCustomersReturnsUniqueCustomers() {
        List<Customer> customers = customerManager.getAllCustomers();

        assertEquals(2, customers.size(), "Should return 2 unique customers");
        assertTrue(customers.contains(regularCustomer));
        assertTrue(customers.contains(premiumCustomer));
    }

    @Test
    @DisplayName("Get all customers with multiple accounts for same customer")
    void getAllCustomersWithMultipleAccounts() {
        // Add another account for regular customer
        Account anotherAccount = new SavingsAccount(regularCustomer, 3000.0);
        accountManager.addAccount(anotherAccount);

        List<Customer> customers = customerManager.getAllCustomers();

        assertEquals(2, customers.size(), "Should still return only 2 unique customers");

        // Verify regular customer appears only once
        long regularCustomerCount = customers.stream()
                .filter(c -> c.getCustomerId().equals(regularCustomer.getCustomerId()))
                .count();
        assertEquals(1, regularCustomerCount, "Regular customer should appear only once");
    }

    @Test
    @DisplayName("Get all customers when no accounts exist")
    void getAllCustomersWhenNoAccounts() {
        AccountManager emptyAccountManager = new AccountManager();
        CustomerManager emptyCustomerManager = new CustomerManager(emptyAccountManager);

        List<Customer> customers = emptyCustomerManager.getAllCustomers();

        assertTrue(customers.isEmpty(), "Should return empty list when no accounts");
    }

    @Test
    @DisplayName("Get customer by ID returns correct customer")
    void getCustomerByIdReturnsCustomer() {
        Customer found = customerManager.getCustomerById(regularCustomer.getCustomerId());

        assertNotNull(found, "Should find regular customer");
        assertEquals(regularCustomer.getCustomerId(), found.getCustomerId());
        assertEquals(regularCustomer.getName(), found.getName());
        assertEquals(regularCustomer.getAge(), found.getAge());
    }

    @Test
    @DisplayName("Get customer by ID returns null for non-existent customer")
    void getCustomerByIdReturnsNullForNonExistent() {
        Customer found = customerManager.getCustomerById("NONEXISTENT123");

        assertNull(found, "Should return null for non-existent customer ID");
    }

    @Test
    @DisplayName("Search customers by name returns matching customers")
    void searchCustomersByName() {
        // Add more customers for testing
        Customer johnSmith = new RegularCustomer("John Smith", 40, "0551111111", "Tema");
        Account johnSmithAccount = new CheckingAccount(johnSmith, 1500.0);
        accountManager.addAccount(johnSmithAccount);

        List<Customer> johnResults = customerManager.searchCustomersByName("John");
        List<Customer> janeResults = customerManager.searchCustomersByName("Jane");
        List<Customer> smithResults = customerManager.searchCustomersByName("Smith");
        List<Customer> noResults = customerManager.searchCustomersByName("Nonexistent");

        assertEquals(2, johnResults.size(), "Should find both John Doe and John Smith");
        assertEquals(1, janeResults.size(), "Should find Jane Smith");
        assertEquals(2, smithResults.size(), "Should find both Smiths");
        assertTrue(noResults.isEmpty(), "Should return empty for non-matching name");
    }

    @Test
    @DisplayName("Search customers by name is case-insensitive")
    void searchCustomersByNameCaseInsensitive() {
        List<Customer> lowercaseResults = customerManager.searchCustomersByName("john");
        List<Customer> uppercaseResults = customerManager.searchCustomersByName("JOHN");
        List<Customer> mixedCaseResults = customerManager.searchCustomersByName("JoHn");

        assertEquals(1, lowercaseResults.size());
        assertEquals(1, uppercaseResults.size());
        assertEquals(1, mixedCaseResults.size());
        assertEquals(regularCustomer.getCustomerId(), lowercaseResults.getFirst().getCustomerId());
    }

    @Test
    @DisplayName("Get customers by type returns correct customers")
    void getCustomersByType() {
        List<Customer> regularCustomers = customerManager.getCustomersByType("Regular");
        List<Customer> premiumCustomers = customerManager.getCustomersByType("Premium");

        assertEquals(1, regularCustomers.size());
        assertEquals(1, premiumCustomers.size());
        assertEquals(regularCustomer.getCustomerId(), regularCustomers.getFirst().getCustomerId());
        assertEquals(premiumCustomer.getCustomerId(), premiumCustomers.getFirst().getCustomerId());
    }

    @Test
    @DisplayName("Get customers by type returns empty for invalid type")
    void getCustomersByTypeInvalidType() {
        List<Customer> invalidTypeCustomers = customerManager.getCustomersByType("InvalidType");

        assertTrue(invalidTypeCustomers.isEmpty());
    }

    @Test
    @DisplayName("Get accounts for customer returns all accounts")
    void getAccountsForCustomer() {
        List<Account> regularCustomerAccounts = customerManager.getAccountsForCustomer(regularCustomer.getCustomerId());
        List<Account> premiumCustomerAccounts = customerManager.getAccountsForCustomer(premiumCustomer.getCustomerId());

        assertEquals(2, regularCustomerAccounts.size(), "Regular customer should have 2 accounts");
        assertEquals(1, premiumCustomerAccounts.size(), "Premium customer should have 1 account");

        // Verify accounts belong to correct customer
        for (Account account : regularCustomerAccounts) {
            assertEquals(regularCustomer.getCustomerId(), account.getCustomer().getCustomerId());
        }
    }

    @Test
    @DisplayName("Get accounts for non-existent customer returns empty list")
    void getAccountsForNonExistentCustomer() {
        List<Account> accounts = customerManager.getAccountsForCustomer("NONEXISTENT123");

        assertTrue(accounts.isEmpty());
    }

    @Test
    @DisplayName("Get account count for customer returns correct count")
    void getAccountCountForCustomer() {
        int regularCustomerCount = customerManager.getAccountCountForCustomer(regularCustomer.getCustomerId());
        int premiumCustomerCount = customerManager.getAccountCountForCustomer(premiumCustomer.getCustomerId());
        int nonExistentCount = customerManager.getAccountCountForCustomer("NONEXISTENT123");

        assertEquals(2, regularCustomerCount);
        assertEquals(1, premiumCustomerCount);
        assertEquals(0, nonExistentCount);
    }

    @Test
    @DisplayName("Get total balance for customer sums all account balances")
    void getTotalBalanceForCustomer() {
        double regularCustomerTotal = customerManager.getTotalBalanceForCustomer(regularCustomer.getCustomerId());
        double premiumCustomerTotal = customerManager.getTotalBalanceForCustomer(premiumCustomer.getCustomerId());
        double nonExistentTotal = customerManager.getTotalBalanceForCustomer("NONEXISTENT123");

        assertEquals(3000.0, regularCustomerTotal, 0.001); // 1000 + 2000
        assertEquals(5000.0, premiumCustomerTotal, 0.001);
        assertEquals(0.0, nonExistentTotal, 0.001);
    }

    @Test
    @DisplayName("Get total balance with negative balances")
    void getTotalBalanceWithNegativeBalances() {
        // Create an account with negative balance
        CheckingAccount negativeAccount = new CheckingAccount(regularCustomer, -500.0);
        negativeAccount.setBalance(-500.0); // Force negative balance
        accountManager.addAccount(negativeAccount);

        double totalBalance = customerManager.getTotalBalanceForCustomer(regularCustomer.getCustomerId());

        assertEquals(2500.0, totalBalance, 0.001); // 1000 + 2000 - 500
    }

    @Test
    @DisplayName("Get customer statistics returns correct values")
    void getCustomerStatistics() {
        CustomerManager.CustomerStatistics stats = customerManager.getCustomerStatistics();

        assertEquals(2, stats.getTotalCustomers());
        assertEquals(1, stats.getRegularCustomers());
        assertEquals(1, stats.getPremiumCustomers());
        assertEquals(3, stats.getTotalAccounts());
        assertEquals(1.5, stats.getAverageAccountsPerCustomer(), 0.001);
    }

    @Test
    @DisplayName("Get customer statistics with no customers")
    void getCustomerStatisticsNoCustomers() {
        AccountManager emptyAccountManager = new AccountManager();
        CustomerManager emptyCustomerManager = new CustomerManager(emptyAccountManager);

        CustomerManager.CustomerStatistics stats = emptyCustomerManager.getCustomerStatistics();

        assertEquals(0, stats.getTotalCustomers());
        assertEquals(0, stats.getRegularCustomers());
        assertEquals(0, stats.getPremiumCustomers());
        assertEquals(0, stats.getTotalAccounts());
        assertEquals(0, stats.getAverageAccountsPerCustomer(), 0.001);
    }

    @Test
    @DisplayName("Customer statistics display method works")
    void customerStatisticsDisplay() {
        CustomerManager.CustomerStatistics stats = customerManager.getCustomerStatistics();

        // Since displayStatistics() prints to console, we can verify it doesn't throw
        assertDoesNotThrow(stats::displayStatistics);
    }

    @Test
    @DisplayName("Check if customer exists returns correct boolean")
    void customerExists() {
        boolean regularExists = customerManager.customerExists(regularCustomer.getCustomerId());
        boolean premiumExists = customerManager.customerExists(premiumCustomer.getCustomerId());
        boolean nonExistentExists = customerManager.customerExists("NONEXISTENT123");

        assertTrue(regularExists);
        assertTrue(premiumExists);
        assertFalse(nonExistentExists);
    }

    @Test
    @DisplayName("Customer exists with null ID")
    void customerExistsWithNullId() {
        boolean exists = customerManager.customerExists(null);

        assertFalse(exists);
    }

    @Test
    @DisplayName("Customer exists with empty ID")
    void customerExistsWithEmptyId() {
        boolean exists = customerManager.customerExists("");

        assertFalse(exists);
    }

    @Test
    @DisplayName("Add multiple customers and verify all operations")
    void multipleCustomersComprehensiveTest() {
        // Clear previous setup
        accountManager = new AccountManager();
        customerManager = new CustomerManager(accountManager);

        // Create 5 customers
        Customer[] customers = new Customer[5];
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                customers[i] = new RegularCustomer("Customer" + i, 20 + i, "055000000" + i, "City" + i);
            } else {
                customers[i] = new PremiumCustomer("Customer" + i, 25 + i, "055111111" + i, "Town" + i);
            }
        }

        // Add accounts (some customers have multiple accounts)
        // Customer0: 2 accounts
        accountManager.addAccount(new CheckingAccount(customers[0], 1000.0));
        accountManager.addAccount(new SavingsAccount(customers[0], 2000.0));

        // Customer1: 1 account
        accountManager.addAccount(new CheckingAccount(customers[1], 3000.0));

        // Customer2: 3 accounts
        accountManager.addAccount(new CheckingAccount(customers[2], 1500.0));
        accountManager.addAccount(new SavingsAccount(customers[2], 2500.0));
        accountManager.addAccount(new CheckingAccount(customers[2], 3500.0));

        // Customer3: 0 accounts (no account added)
        // Customer4: 1 account
        accountManager.addAccount(new SavingsAccount(customers[4], 4000.0));

        // Test getAllCustomers
        List<Customer> allCustomers = customerManager.getAllCustomers();
        assertEquals(4, allCustomers.size(), "Should have 4 customers with accounts");

        // Test customer exists
        assertTrue(customerManager.customerExists(customers[0].getCustomerId()));
        assertFalse(customerManager.customerExists(customers[3].getCustomerId()), "Customer3 has no accounts");

        // Test account counts
        assertEquals(2, customerManager.getAccountCountForCustomer(customers[0].getCustomerId()));
        assertEquals(1, customerManager.getAccountCountForCustomer(customers[1].getCustomerId()));
        assertEquals(3, customerManager.getAccountCountForCustomer(customers[2].getCustomerId()));
        assertEquals(0, customerManager.getAccountCountForCustomer(customers[3].getCustomerId()));
        assertEquals(1, customerManager.getAccountCountForCustomer(customers[4].getCustomerId()));

        // Test total balances
        assertEquals(3000.0, customerManager.getTotalBalanceForCustomer(customers[0].getCustomerId()), 0.001);
        assertEquals(3000.0, customerManager.getTotalBalanceForCustomer(customers[1].getCustomerId()), 0.001);
        assertEquals(7500.0, customerManager.getTotalBalanceForCustomer(customers[2].getCustomerId()), 0.001);
        assertEquals(0.0, customerManager.getTotalBalanceForCustomer(customers[3].getCustomerId()), 0.001);
        assertEquals(4000.0, customerManager.getTotalBalanceForCustomer(customers[4].getCustomerId()), 0.001);

        // Test statistics
        CustomerManager.CustomerStatistics stats = customerManager.getCustomerStatistics();
        assertEquals(4, stats.getTotalCustomers());
        assertEquals(3, stats.getRegularCustomers()); // Customers 0, 2, 4
        assertEquals(1, stats.getPremiumCustomers()); // Customer 1
        assertEquals(7, stats.getTotalAccounts()); // 2 + 1 + 3 + 0 + 1
        assertEquals(1.75, stats.getAverageAccountsPerCustomer(), 0.001);
    }

    @Test
    @DisplayName("Search with partial name matches")
    void searchWithPartialName() {
        Customer customer1 = new RegularCustomer("Alexander Hamilton", 45, "0552222222", "New York");
        Customer customer2 = new RegularCustomer("Alex Johnson", 30, "0553333333", "Chicago");
        Customer customer3 = new RegularCustomer("William Alexander", 50, "0554444444", "Boston");

        accountManager.addAccount(new CheckingAccount(customer1, 1000.0));
        accountManager.addAccount(new CheckingAccount(customer2, 2000.0));
        accountManager.addAccount(new CheckingAccount(customer3, 3000.0));

        List<Customer> hamResults = customerManager.searchCustomersByName("Ham");
        List<Customer> willResults = customerManager.searchCustomersByName("William");

        assertEquals(1, hamResults.size(), "Should find Hamilton");
        assertEquals(1, willResults.size(), "Should find William");
    }

    @Test
    @DisplayName("Empty or null search parameters")
    void emptyOrNullSearchParameters() {
        // Search with empty string
        List<Customer> emptySearch = customerManager.searchCustomersByName("");
        List<Customer> whitespaceSearch = customerManager.searchCustomersByName("   ");
        assertNotNull(emptySearch);
        assertNotNull(whitespaceSearch);
    }
}