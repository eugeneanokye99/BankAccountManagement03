
import account.Account;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.RegularCustomer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AccountTest {
    private Customer regularCustomer;
    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;

    @BeforeEach
    void setUp() {
        regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
        checkingAccount = new CheckingAccount(regularCustomer, 1000.0);
        savingsAccount = new SavingsAccount(regularCustomer, 1500.0);
    }

    @Test
    void depositUpdatesBalance() {
        double initialBalance = checkingAccount.getBalance();
        double depositAmount = 500.0;

        checkingAccount.deposit(depositAmount);

        double expectedBalance = initialBalance + depositAmount;
        assertEquals(expectedBalance, checkingAccount.getBalance(), 0.01,
                "Deposit should increase balance by the deposited amount");
    }

    @Test
    void withdrawBelowMinimumThrowsException() {
        double withdrawalAmount = 1200.0;
        double initialBalance = savingsAccount.getBalance();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> savingsAccount.withdraw(withdrawalAmount),
                "Withdrawing below minimum balance should throw IllegalArgumentException"
        );

        // Verify balance didn't change
        assertEquals(initialBalance, savingsAccount.getBalance(), 0.01,
                "Balance should remain unchanged after failed withdrawal");

        assertTrue(exception.getMessage().contains("Withdrawal denied") ||
                        exception.getMessage().contains("Insufficient funds"),
                "Exception message should mention Withdrawal denied or insufficient funds");
    }

    @Test
    void overdraftWithinLimitAllowed() {
        double withdrawalAmount = 1500.0;
        double initialBalance = checkingAccount.getBalance();


        boolean success = checkingAccount.withdraw(withdrawalAmount);


        assertTrue(success, "Withdrawal within overdraft limit should succeed");
        assertEquals(initialBalance - withdrawalAmount, checkingAccount.getBalance(), 0.01,
                "Balance should be negative but within overdraft limit");
        assertTrue(checkingAccount.getBalance() < 0,
                "Balance should be negative when using overdraft");
        assertTrue(checkingAccount.getBalance() >= -1000.0,
                "Negative balance should not exceed overdraft limit of $1000");
    }

    @Test
    void overdraftExceedThrowsException() {

        double withdrawalAmount = 2500.0;
        double initialBalance = checkingAccount.getBalance();


        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkingAccount.withdraw(withdrawalAmount),
                "Withdrawing beyond overdraft limit should throw IllegalArgumentException"
        );


        assertEquals(initialBalance, checkingAccount.getBalance(), 0.01,
                "Balance should remain unchanged after failed overdraft withdrawal");

        assertTrue(exception.getMessage().contains("Insufficient funds"),
                "Exception message should mention insufficient funds");
    }


    @Test
    void transferBetweenAccountsSucceeds() {

        Account source = new CheckingAccount(regularCustomer, 1000.0);
        Account target = new SavingsAccount(regularCustomer, 500.0);
        double transferAmount = 300.0;


        boolean success = source.transfer(target, transferAmount);

        assertTrue(success, "Transfer should succeed with sufficient funds");
        assertEquals(700.0, source.getBalance(), 0.01,
                "Source balance should decrease by transfer amount");
        assertEquals(800.0, target.getBalance(), 0.01,
                "Target balance should increase by transfer amount");
    }
}