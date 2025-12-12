import account.AccountManager;
import account.SavingsAccount;
import account.CheckingAccount;
import customer.CustomerManager;
import customer.RegularCustomer;
import customer.PremiumCustomer;
import services.FilePersistenceService;
import transaction.Transaction;
import transaction.TransactionManager;

import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilePersistenceServiceTest {

    private AccountManager accountManager;
    private CustomerManager customerManager;
    private TransactionManager transactionManager;
    private FilePersistenceService service;
    private Path testDir;

    @BeforeEach
    public void setUp() throws IOException {
        accountManager = new AccountManager();
        customerManager = new CustomerManager(accountManager);
        transactionManager = new TransactionManager();
        service = new FilePersistenceService(accountManager, customerManager, transactionManager);
        testDir = Files.createTempDirectory("bank_test_");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (Files.exists(testDir)) {
            Files.walk(testDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                    });
        }
    }

    @Test
    @Order(1)
    public void testSaveAndLoad() throws Exception {
        RegularCustomer regular = new RegularCustomer("Test User", 25, "test@email.com", "Test Address");
        PremiumCustomer premium = new PremiumCustomer("Premium User", 35, "premium@email.com", "Premium Address");

        SavingsAccount savings = new SavingsAccount(regular, 5000.00);
        CheckingAccount checking = new CheckingAccount(premium, 3000.00);

        accountManager.addAccount(savings);
        accountManager.addAccount(checking);

        Transaction transaction = new Transaction(savings.getAccountNumber(), "Deposit", 500.00, 5500.00);
        transactionManager.addTransaction(transaction);

        // Save
        service.saveAllData();

        // Clear managers
        customerManager.getAllCustomers().clear();
        accountManager.getAccounts().clear();
        transactionManager.getAllTransactions().clear();

        // Load
        service.loadAllData();

        assertEquals(2, customerManager.getAllCustomers().size(), "Customers loaded incorrectly");
        assertEquals(2, accountManager.getAccounts().size(), "Accounts loaded incorrectly");
        assertEquals(1, transactionManager.getAllTransactions().size(), "Transactions loaded incorrectly");
    }

    @Test
    @Order(2)
    public void testIndividualMethods() throws Exception {
        RegularCustomer customer = new RegularCustomer("Single User", 40, "single@email.com", "Single Address");
        SavingsAccount account = new SavingsAccount(customer, 10000.00);
        accountManager.addAccount(account);

        // Save individual files
        service.saveCustomersOnly();
        Path customersFile = Paths.get("dataset", "customers.txt");
        assertTrue(Files.exists(customersFile), "Customers file not created");
        Files.deleteIfExists(customersFile);

        service.saveAccountsOnly();
        Path accountsFile = Paths.get("dataset", "accounts.txt");
        assertTrue(Files.exists(accountsFile), "Accounts file not created");
        Files.deleteIfExists(accountsFile);

        // dataFilesExist
        assertFalse(service.dataFilesExist(), "dataFilesExist should return false when files don't exist");
    }

    @Test
    @Order(3)
    public void testErrorHandling() {
        assertDoesNotThrow(() -> new FilePersistenceService(null, null, null),
                "Constructor should handle null managers");

        // You can add more specific exception tests if needed
    }
}
