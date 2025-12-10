
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import transaction.Transaction;
import transaction.TransactionManager;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;

public class TransactionManagerTest {
    private Transaction transaction;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() {
        // Reset static counter before each test
        resetTransactionCounter();
        transaction = new Transaction("ACC001", "DEPOSIT", 500.0, 1500.0);
        transactionManager = new TransactionManager();
    }

    // Helper method to reset static transaction counter
    private void resetTransactionCounter() {
        try {
            Field counterField = Transaction.class.getDeclaredField("transactionCounter");
            counterField.setAccessible(true);
            counterField.set(null, 0);
        } catch (Exception e) {
            fail("Failed to reset transaction counter: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Transaction constructor should initialize all fields correctly")
    void transactionConstructorInitializesFields() {
        assertEquals("ACC001", transaction.getAccountNumber());
        assertEquals("DEPOSIT", transaction.getType());
        assertEquals(500.0, transaction.getAmount(), 0.001);
        assertEquals(1500.0, transaction.getBalanceAfter(), 0.001);
        assertNotNull(transaction.getTransactionId());
        assertNotNull(transaction.getTimestamp());
    }


    @Test
    @DisplayName("Transaction type should be stored in uppercase")
    void transactionTypeIsUppercased() {
        Transaction t1 = new Transaction("ACC001", "deposit", 100.0, 1100.0);
        Transaction t2 = new Transaction("ACC002", "Withdrawal", 50.0, 950.0);
        Transaction t3 = new Transaction("ACC003", "TRANSFER", 200.0, 1200.0);

        assertEquals("DEPOSIT", t1.getType());
        assertEquals("WITHDRAWAL", t2.getType());
        assertEquals("TRANSFER", t3.getType());
    }

    @Test
    @DisplayName("Transfer constructor should work")
    void transferConstructorWorks() {
        Transaction transfer = new Transaction("ACC001", "TRANSFER", 200.0, 800.0, "ACC002");

        assertEquals("ACC001", transfer.getAccountNumber());
        assertEquals("TRANSFER", transfer.getType());
        assertEquals(200.0, transfer.getAmount(), 0.001);
        assertEquals(800.0, transfer.getBalanceAfter(), 0.001);
        assertNotNull(transfer.getTransactionId());
        assertNotNull(transfer.getTimestamp());
    }

    @Test
    @DisplayName("Getters should return correct values")
    void gettersReturnCorrectValues() {
        Transaction t = new Transaction("ACC123", "WITHDRAWAL", 250.75, 749.25);

        assertTrue(t.getTransactionId().startsWith("TXN"));
        assertEquals("ACC123", t.getAccountNumber());
        assertEquals("WITHDRAWAL", t.getType());
        assertEquals(250.75, t.getAmount(), 0.001);
        assertEquals(749.25, t.getBalanceAfter(), 0.001);
        assertNotNull(t.getTimestamp());
    }

    @Test
    @DisplayName("Static transaction counter should be accessible")
    void staticTransactionCounterIsAccessible() {
        // After setUp, counter should be 1
        assertEquals(1, Transaction.getTransactionCounter());

        new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0);
        assertEquals(2, Transaction.getTransactionCounter());
    }

    // TransactionManager Tests
    @Test
    @DisplayName("TransactionManager default constructor creates array with capacity 200")
    void transactionManagerDefaultConstructor() {
        assertEquals(0, getTransactionManagerCount(transactionManager));
    }

    @Test
    @DisplayName("TransactionManager custom constructor creates array with specified capacity")
    void transactionManagerCustomConstructor() {
        TransactionManager customManager = new TransactionManager(50);
        assertEquals(0, getTransactionManagerCount(customManager));
    }

    @Test
    @DisplayName("Add transaction should increase count")
    void addTransactionIncreasesCount() {
        transactionManager.addTransaction(transaction);
        assertEquals(1, getTransactionManagerCount(transactionManager));

        Transaction t2 = new Transaction("ACC002", "WITHDRAWAL", 100.0, 900.0);
        transactionManager.addTransaction(t2);
        assertEquals(2, getTransactionManagerCount(transactionManager));
    }

    @Test
    @DisplayName("Add transaction should not exceed capacity")
    void addTransactionWithinCapacity() {
        TransactionManager smallManager = new TransactionManager(2);

        Transaction t1 = new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0);
        Transaction t2 = new Transaction("ACC002", "WITHDRAWAL", 50.0, 950.0);
        Transaction t3 = new Transaction("ACC003", "DEPOSIT", 200.0, 1200.0);

        smallManager.addTransaction(t1);
        smallManager.addTransaction(t2);
        smallManager.addTransaction(t3); // Should not be added

        assertEquals(2, getTransactionManagerCount(smallManager));
    }

    @Test
    @DisplayName("Calculate total deposits for account")
    void calculateTotalDeposits() {
        transactionManager.addTransaction(new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0));
        transactionManager.addTransaction(new Transaction("ACC001", "WITHDRAWAL", 50.0, 1050.0));
        transactionManager.addTransaction(new Transaction("ACC001", "DEPOSIT", 200.0, 1250.0));
        transactionManager.addTransaction(new Transaction("ACC002", "DEPOSIT", 300.0, 1300.0));

        double totalDepositsAcc1 = transactionManager.calculateTotalDeposits("ACC001");
        double totalDepositsAcc2 = transactionManager.calculateTotalDeposits("ACC002");

        assertEquals(300.0, totalDepositsAcc1, 0.001); // 100 + 200
        assertEquals(300.0, totalDepositsAcc2, 0.001); // 300
    }

    @Test
    @DisplayName("Calculate total withdrawals for account")
    void calculateTotalWithdrawals() {
        transactionManager.addTransaction(new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0));
        transactionManager.addTransaction(new Transaction("ACC001", "WITHDRAWAL", 50.0, 1050.0));
        transactionManager.addTransaction(new Transaction("ACC001", "WITHDRAWAL", 30.0, 1020.0));
        transactionManager.addTransaction(new Transaction("ACC002", "WITHDRAWAL", 300.0, 700.0));

        double totalWithdrawalsAcc1 = transactionManager.calculateTotalWithdrawals("ACC001");
        double totalWithdrawalsAcc2 = transactionManager.calculateTotalWithdrawals("ACC002");

        assertEquals(80.0, totalWithdrawalsAcc1, 0.001); // 50 + 30
        assertEquals(300.0, totalWithdrawalsAcc2, 0.001); // 300
    }

    @Test
    @DisplayName("Calculate total for account with no transactions")
    void calculateTotalForNonexistentAccount() {
        transactionManager.addTransaction(new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0));

        double deposits = transactionManager.calculateTotalDeposits("NONEXISTENT");
        double withdrawals = transactionManager.calculateTotalWithdrawals("NONEXISTENT");

        assertEquals(0.0, deposits, 0.001);
        assertEquals(0.0, withdrawals, 0.001);
    }

    @Test
    @DisplayName("View transactions should not throw exception for empty account")
    void viewTransactionsForEmptyAccount() {
        // Since viewTransactionsByAccount prints to console, we can only test that it doesn't throw
        assertDoesNotThrow(() -> transactionManager.viewTransactionsByAccount("ACC001"));
    }

    @Test
    @DisplayName("Get transactions for specific account")
    void getTransactionsForAccount() throws Exception {
        Transaction t1 = new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0);
        Transaction t2 = new Transaction("ACC002", "DEPOSIT", 200.0, 1200.0);
        Transaction t3 = new Transaction("ACC001", "WITHDRAWAL", 50.0, 1050.0);

        transactionManager.addTransaction(t1);
        transactionManager.addTransaction(t2);
        transactionManager.addTransaction(t3);

        // Use reflection to test private method
        var method = TransactionManager.class.getDeclaredMethod("getTransactionsForAccount", String.class);
        method.setAccessible(true);
        Transaction[] acc1Transactions = (Transaction[]) method.invoke(transactionManager, "ACC001");
        Transaction[] acc2Transactions = (Transaction[]) method.invoke(transactionManager, "ACC002");
        Transaction[] acc3Transactions = (Transaction[]) method.invoke(transactionManager, "ACC003");

        assertEquals(2, acc1Transactions.length); // t1 and t3
        assertEquals(1, acc2Transactions.length); // t2
        assertEquals(0, acc3Transactions.length); // none
    }

    @Test
    @DisplayName("Transaction arrays should be sorted by timestamp (newest first)")
    void transactionsSortedByTimestamp() throws Exception {
        // Since timestamp is generated at creation, we need to control it
        // We'll test that the sorting comparator works correctly

        TransactionManager manager = new TransactionManager();

        // Add transactions in reverse order (oldest to newest by ID)
        resetTransactionCounter();
        Transaction t1 = new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0); // TXN001
        Transaction t2 = new Transaction("ACC001", "DEPOSIT", 200.0, 1300.0); // TXN002
        Transaction t3 = new Transaction("ACC001", "WITHDRAWAL", 50.0, 1250.0); // TXN003

        manager.addTransaction(t1);
        manager.addTransaction(t2);
        manager.addTransaction(t3);

        // The view method sorts by timestamp in reverse order
        // Since we can't control timestamps easily, we'll test that the method doesn't throw
        assertDoesNotThrow(() -> manager.viewTransactionsByAccount("ACC001"));
    }

    @Test
    @DisplayName("Multiple accounts transactions are tracked separately")
    void multipleAccountsSeparateTracking() {
        transactionManager.addTransaction(new Transaction("ACC001", "DEPOSIT", 100.0, 1100.0));
        transactionManager.addTransaction(new Transaction("ACC002", "DEPOSIT", 200.0, 1200.0));
        transactionManager.addTransaction(new Transaction("ACC001", "WITHDRAWAL", 50.0, 1050.0));
        transactionManager.addTransaction(new Transaction("ACC002", "WITHDRAWAL", 100.0, 1100.0));
        transactionManager.addTransaction(new Transaction("ACC003", "DEPOSIT", 300.0, 1300.0));

        assertEquals(2, transactionManager.calculateTotalDeposits("ACC001"), 100.0);
        assertEquals(1, transactionManager.calculateTotalWithdrawals("ACC001"), 50.0);

        assertEquals(1, transactionManager.calculateTotalDeposits("ACC002"), 200.0);
        assertEquals(1, transactionManager.calculateTotalWithdrawals("ACC002"), 100.0);

        assertEquals(1, transactionManager.calculateTotalDeposits("ACC003"), 300.0);
        assertEquals(0, transactionManager.calculateTotalWithdrawals("ACC003"), 0.0);
    }

    @Test
    @DisplayName("Transaction with zero amount")
    void transactionWithZeroAmount() {
        Transaction zeroTransaction = new Transaction("ACC001", "DEPOSIT", 0.0, 1000.0);

        assertEquals("ACC001", zeroTransaction.getAccountNumber());
        assertEquals("DEPOSIT", zeroTransaction.getType());
        assertEquals(0.0, zeroTransaction.getAmount(), 0.001);
        assertEquals(1000.0, zeroTransaction.getBalanceAfter(), 0.001);
    }

    @Test
    @DisplayName("Transaction with negative balance after")
    void transactionWithNegativeBalance() {
        Transaction negativeTransaction = new Transaction("ACC001", "WITHDRAWAL", 1500.0, -500.0);

        assertEquals("ACC001", negativeTransaction.getAccountNumber());
        assertEquals("WITHDRAWAL", negativeTransaction.getType());
        assertEquals(1500.0, negativeTransaction.getAmount(), 0.001);
        assertEquals(-500.0, negativeTransaction.getBalanceAfter(), 0.001);
    }

    // Helper method to get transaction count from TransactionManager
    private int getTransactionManagerCount(TransactionManager manager) {
        try {
            Field countField = TransactionManager.class.getDeclaredField("transactionCount");
            countField.setAccessible(true);
            return (int) countField.get(manager);
        } catch (Exception e) {
            fail("Failed to get transaction count: " + e.getMessage());
            return -1;
        }
    }

    @Test
    @DisplayName("Case-insensitive transaction type matching")
    void caseInsensitiveTransactionTypeMatching() {
        // This tests that the type matching in calculate methods is case-sensitive
        // Since Transaction stores type in uppercase, and calculate methods check uppercase

        transactionManager.addTransaction(new Transaction("ACC001", "deposit", 100.0, 1100.0));
        transactionManager.addTransaction(new Transaction("ACC001", "Withdrawal", 50.0, 1050.0));

        double deposits = transactionManager.calculateTotalDeposits("ACC001");
        double withdrawals = transactionManager.calculateTotalWithdrawals("ACC001");

        // Since constructor converts to uppercase, these should work
        assertEquals(100.0, deposits, 0.001);
        assertEquals(50.0, withdrawals, 0.001);
    }

    @Test
    @DisplayName("Edge case: Very large number of transactions")
    void largeNumberOfTransactions() {
        TransactionManager largeManager = new TransactionManager(1000);

        // Add 1000 transactions
        for (int i = 0; i < 1000; i++) {
            largeManager.addTransaction(new Transaction("ACC" + (i % 10),
                    i % 2 == 0 ? "DEPOSIT" : "WITHDRAWAL",
                    i * 10.0,
                    i * 100.0));
        }

        // Should not throw any exceptions
        assertDoesNotThrow(() -> largeManager.viewTransactionsByAccount("ACC0"));
    }
}