
import account.Account;
import account.AccountManager;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.RegularCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    void getAccountsReturnsList() {
        accountManager.addAccount(checkingAccount);
        accountManager.addAccount(savingsAccount);

        List<Account> accountsList = accountManager.getAccounts();

        assertNotNull(accountsList, "Should return non-null list");
        assertEquals(2, accountsList.size(), "List should have 2 accounts");
        assertSame(checkingAccount, accountsList.get(0), "First element should be checking account");
        assertSame(savingsAccount, accountsList.get(1), "Second element should be savings account");
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
    void viewAllAccountsWhenEmpty() {
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

}