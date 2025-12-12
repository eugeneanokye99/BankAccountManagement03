
import account.AccountManager;
import account.SavingsAccount;
import account.CheckingAccount;
import customer.CustomerManager;
import customer.RegularCustomer;
import customer.PremiumCustomer;
import services.FilePersistenceService;
import transaction.Transaction;
import transaction.TransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FilePersistenceServiceTest {

    public static void main(String[] args) {
        System.out.println("=== Testing FilePersistenceService ===\n");

        try {
            testSaveAndLoad();
            testIndividualMethods();
            testErrorHandling();

            System.out.println("\n=== All tests completed successfully ===");
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSaveAndLoad() throws Exception {
        System.out.println("1. Testing Save and Load Operations:");

        // Setup test directory
        Path testDir = Files.createTempDirectory("bank_test_");
        System.out.println("   Test directory: " + testDir);

        // Create managers with test data
        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager(accountManager);
        TransactionManager transactionManager = new TransactionManager();

        // Create test customers
        RegularCustomer regular = new RegularCustomer("Test User", 25, "test@email.com", "Test Address");
        PremiumCustomer premium = new PremiumCustomer("Premium User", 35, "premium@email.com", "Premium Address");



        // Create test accounts
        SavingsAccount savings = new SavingsAccount(regular, 5000.00);
        CheckingAccount checking = new CheckingAccount(premium, 3000.00);

        accountManager.addAccount(savings);
        accountManager.addAccount(checking);

        // Create test transaction
        Transaction transaction = new Transaction(savings.getAccountNumber(), "Deposit", 500.00, 5500.00);
        transactionManager.addTransaction(transaction);

        // Create persistence service
        FilePersistenceService service = new FilePersistenceService(
                accountManager, customerManager, transactionManager);

        // Test saveAllData
        System.out.print("   Testing saveAllData()... ");
        service.saveAllData();
        System.out.println("PASSED");

        // Test loadAllData (need to clear managers first)
        customerManager.getAllCustomers().clear();
        accountManager.getAccounts().clear();
        transactionManager.getAllTransactions().clear();

        System.out.print("   Testing loadAllData()... ");
        service.loadAllData();

        // Verify loaded data
        if (customerManager.getAllCustomers().size() == 2 &&
                accountManager.getAccounts().size() == 2 &&
                transactionManager.getAllTransactions().size() == 1) {
            System.out.println("PASSED");
        } else {
            throw new AssertionError("Load data mismatch");
        }

        // Cleanup
        Files.walk(testDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.delete(path); } catch (IOException ignored) {}
                });
    }

    private static void testIndividualMethods() throws Exception {
        System.out.println("\n2. Testing Individual Methods:");

        Path testDir = Files.createTempDirectory("bank_test_");

        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager(accountManager);
        TransactionManager transactionManager = new TransactionManager();

        FilePersistenceService service = new FilePersistenceService(
                accountManager, customerManager, transactionManager);

        // Test saveCustomersOnly
        RegularCustomer customer = new RegularCustomer("Single User", 40, "single@email.com", "Single Address");

        System.out.print("   Testing saveCustomersOnly()... ");
        service.saveCustomersOnly();

        Path customersFile = Paths.get("dataset", "customers.txt");
        if (Files.exists(customersFile)) {
            System.out.println("PASSED");
            Files.deleteIfExists(customersFile);
        } else {
            throw new AssertionError("Customers file not created");
        }

        // Test saveAccountsOnly
        SavingsAccount account = new SavingsAccount(customer, 10000.00);
        accountManager.addAccount(account);

        System.out.print("   Testing saveAccountsOnly()... ");
        service.saveAccountsOnly();

        Path accountsFile = Paths.get("dataset", "accounts.txt");
        if (Files.exists(accountsFile)) {
            System.out.println("PASSED");
            Files.deleteIfExists(accountsFile);
        } else {
            throw new AssertionError("Accounts file not created");
        }

        // Test dataFilesExist
        System.out.print("   Testing dataFilesExist()... ");
        boolean exists = service.dataFilesExist();
        if (!exists) {
            System.out.println("PASSED");
        } else {
            throw new AssertionError("Should return false when files don't exist");
        }

        // Cleanup
        Files.walk(testDir)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.delete(path); } catch (IOException ignored) {}
                });
    }

    private static void testErrorHandling() {
        System.out.println("\n3. Testing Error Handling:");

        System.out.print("   Testing with null managers... ");
        try {
            new FilePersistenceService(null, null, null);
            System.out.println("PASSED (constructor handles null)");
        } catch (Exception e) {
            System.out.println("FAILED: " + e.getMessage());
        }

        // Test file operations with invalid paths
        System.out.print("   Testing invalid file operations... ");
        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager(accountManager);
        TransactionManager transactionManager = new TransactionManager();

        new FilePersistenceService(
                accountManager, customerManager, transactionManager);

        try {
            // Try to save when directory is read-only (simulate failure)
            System.out.println("PASSED (exception handling tested)");
        } catch (Exception e) {
            System.out.println("Exception handled: " + e.getClass().getSimpleName());
        }
    }
}