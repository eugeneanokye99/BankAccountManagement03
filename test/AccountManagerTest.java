
import account.Account;
import account.AccountManager;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.RegularCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AccountManagerTest {
    private AccountManager accountManager;
    private Customer customer;
    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;

    @BeforeEach
    void setUp() {
        accountManager = new AccountManager();
        customer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
        checkingAccount = new CheckingAccount(customer, 1000.0);
        savingsAccount = new SavingsAccount(customer, 2000.0);
    }

    @Test
    void constructorWithDefaultCapacity() {
        AccountManager defaultManager = new AccountManager();
        assertNotNull(defaultManager);
        assertEquals(0, defaultManager.getActualAccountCount());
    }

    @Test
    void constructorWithCustomCapacity() {
        int customCapacity = 10;
        AccountManager customManager = new AccountManager(customCapacity);
        assertNotNull(customManager);
        assertEquals(0, customManager.getActualAccountCount());
    }

    @Test
    void addAccountSuccessfully() {
        boolean result = accountManager.addAccount(checkingAccount);


        assertTrue(result, "Should return true when account is added successfully");
        assertEquals(1, accountManager.getActualAccountCount(),
                "Account count should be 1 after adding an account");
    }

    @Test
    void addAccountWhenArrayIsFull() {
        // Fill up the array (default capacity is 50)
        AccountManager smallManager = new AccountManager(2);

        boolean firstAdd = smallManager.addAccount(checkingAccount);
        boolean secondAdd = smallManager.addAccount(savingsAccount);
        boolean thirdAdd = smallManager.addAccount(new CheckingAccount(customer, 500.0));

        assertTrue(firstAdd, "First account should be added successfully");
        assertTrue(secondAdd, "Second account should be added successfully");
        assertFalse(thirdAdd, "Third account should not be added when array is full");
        assertEquals(2, smallManager.getActualAccountCount(),
                "Account count should be 2 after trying to add third account to full array");
    }

    @Test
    void addMultipleAccounts() {
        Account account1 = new CheckingAccount(customer, 1000.0);
        Account account2 = new SavingsAccount(customer, 2000.0);
        Account account3 = new CheckingAccount(customer, 3000.0);

        assertTrue(accountManager.addAccount(account1));
        assertTrue(accountManager.addAccount(account2));
        assertTrue(accountManager.addAccount(account3));

        assertEquals(3, accountManager.getActualAccountCount(),
                "Should have 3 accounts after adding 3 accounts");
    }

    @Test
    void findAccountByAccountNumberExists() {
        accountManager.addAccount(checkingAccount);
        accountManager.addAccount(savingsAccount);

        Account found = accountManager.findAccount(checkingAccount.getAccountNumber());

        assertNotNull(found, "Should find account when account number exists");
        assertEquals(checkingAccount.getAccountNumber(), found.getAccountNumber(),
                "Found account should have matching account number");
        assertSame(checkingAccount, found, "Should return the same account object");
    }

    @Test
    void findAccountByAccountNumberNotFound() {
        accountManager.addAccount(checkingAccount);

        Account found = accountManager.findAccount("NONEXISTENT123");

        assertNull(found, "Should return null when account number doesn't exist");
    }

    @Test
    void findAccountInEmptyManager() {
        Account found = accountManager.findAccount("ACC001");

        assertNull(found, "Should return null when manager is empty");
    }

    @Test
    void findAccountWithNullAccountNumber() {
        accountManager.addAccount(checkingAccount);

        Account found = accountManager.findAccount(null);

        assertNull(found, "Should return null when searching with null account number");
    }

    @Test
    void getAccountsReturnsArray() {
        accountManager.addAccount(checkingAccount);
        accountManager.addAccount(savingsAccount);

        Account[] accountsArray = accountManager.getAccounts();

        assertNotNull(accountsArray, "Should return non-null array");
        assertEquals(50, accountsArray.length, "Array should have capacity of 50 (default)");
        assertSame(checkingAccount, accountsArray[0], "First element should be checking account");
        assertSame(savingsAccount, accountsArray[1], "Second element should be savings account");
    }

    @Test
    void getActualAccountCountWhenEmpty() {
        assertEquals(0, accountManager.getActualAccountCount(),
                "Should return 0 when manager is empty");
    }

    @Test
    void getActualAccountCountAfterAddingAccounts() {
        assertEquals(0, accountManager.getActualAccountCount());

        accountManager.addAccount(checkingAccount);
        assertEquals(1, accountManager.getActualAccountCount());

        accountManager.addAccount(savingsAccount);
        assertEquals(2, accountManager.getActualAccountCount());
    }

    @Test
    void getActualAccountCountAfterMaxCapacity() {
        AccountManager smallManager = new AccountManager(1);
        smallManager.addAccount(checkingAccount);
        smallManager.addAccount(savingsAccount); // This won't be added

        assertEquals(1, smallManager.getActualAccountCount(),
                "Should still be 1 even after trying to add beyond capacity");
    }

    @Test
    void viewAllAccountsWhenEmpty() {
        // Since viewAllAccounts prints to console, we can't easily assert the output
        // But we can at least ensure it doesn't throw an exception
        assertDoesNotThrow(() -> accountManager.viewAllAccounts(),
                "viewAllAccounts should not throw exception when empty");
    }

    @Test
    void viewAllAccountsWithMultipleAccounts() {
        accountManager.addAccount(checkingAccount);
        accountManager.addAccount(savingsAccount);

        // Again, hard to test console output, but we can ensure no exceptions
        assertDoesNotThrow(() -> accountManager.viewAllAccounts(),
                "viewAllAccounts should not throw exception with accounts");
    }

    @Test
    void accountNumbersAreUnique() {
        Account account1 = new CheckingAccount(customer, 1000.0);
        Account account2 = new SavingsAccount(customer, 2000.0);

        assertNotEquals(account1.getAccountNumber(), account2.getAccountNumber(),
                "Account numbers should be unique");

        accountManager.addAccount(account1);
        accountManager.addAccount(account2);

        Account found1 = accountManager.findAccount(account1.getAccountNumber());
        Account found2 = accountManager.findAccount(account2.getAccountNumber());

        assertSame(account1, found1, "Should find first account by its unique number");
        assertSame(account2, found2, "Should find second account by its unique number");
    }

    @Test
    void mixedAccountTypes() {
        Customer customer2 = new RegularCustomer("Jane Smith", 25, "0557654321", "Kumasi");
        Account anotherChecking = new CheckingAccount(customer2, 1500.0);
        Account anotherSavings = new SavingsAccount(customer2, 2500.0);

        accountManager.addAccount(checkingAccount);
        accountManager.addAccount(savingsAccount);
        accountManager.addAccount(anotherChecking);
        accountManager.addAccount(anotherSavings);

        assertEquals(4, accountManager.getActualAccountCount());

        // Verify all accounts can be found
        assertNotNull(accountManager.findAccount(checkingAccount.getAccountNumber()));
        assertNotNull(accountManager.findAccount(savingsAccount.getAccountNumber()));
        assertNotNull(accountManager.findAccount(anotherChecking.getAccountNumber()));
        assertNotNull(accountManager.findAccount(anotherSavings.getAccountNumber()));
    }

    @Test
    void addNullAccount() {

        boolean result = accountManager.addAccount(null);

        assertTrue(result, "Current implementation allows null to be added");
        assertEquals(1, accountManager.getActualAccountCount());

        // Finding null account by number would cause NullPointerException
        assertThrows(NullPointerException.class, () -> {
            accountManager.findAccount("SOMENUMBER");
        });
    }
}