package test;

import account.Account;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.RegularCustomer;

public class AccountTest {

    public static void runAllTests() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("RUNNING BANK ACCOUNT TESTS");
        System.out.println("=".repeat(60) + "\n");

        int totalTests = 0;
        int passedTests = 0;

        // Test 1: Deposit Updates Balance
        totalTests++;
        if (testDepositUpdatesBalance()) {
            passedTests++;
            System.out.println("✓ Test 1: depositUpdatesBalance() ...... PASSED");
        } else {
            System.out.println("✗ Test 1: depositUpdatesBalance() ...... FAILED");
        }

        // Test 2: Withdraw Below Minimum Throws Exception
        totalTests++;
        if (testWithdrawBelowMinimumThrowsException()) {
            passedTests++;
            System.out.println("✓ Test 2: withdrawBelowMinimumThrowsException() ...... PASSED");
        } else {
            System.out.println("✗ Test 2: withdrawBelowMinimumThrowsException() ...... FAILED");
        }

        // Test 3: Overdraft Within Limit Allowed
        totalTests++;
        if (testOverdraftWithinLimitAllowed()) {
            passedTests++;
            System.out.println("✓ Test 3: overdraftWithinLimitAllowed() ...... PASSED");
        } else {
            System.out.println("✗ Test 3: overdraftWithinLimitAllowed() ...... FAILED");
        }

        // Test 4: Overdraft Exceed Throws Exception
        totalTests++;
        if (testOverdraftExceedThrowsException()) {
            passedTests++;
            System.out.println("✓ Test 4: overdraftExceedThrowsException() ...... PASSED");
        } else {
            System.out.println("✗ Test 4: overdraftExceedThrowsException() ...... FAILED");
        }

        // Test 5: Transfer Between Accounts Succeeds
        totalTests++;
        if (testTransferBetweenAccountsSucceeds()) {
            passedTests++;
            System.out.println("✓ Test 5: transferBetweenAccountsSucceeds() ...... PASSED");
        } else {
            System.out.println("✗ Test 5: transferBetweenAccountsSucceeds() ...... FAILED");
        }

        // Summary
        System.out.println("\n" + "=".repeat(60));
        System.out.println("TEST SUMMARY");
        System.out.println("=".repeat(60));
        System.out.println("Total Tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + (totalTests - passedTests));

        if (passedTests == totalTests) {
            System.out.println("\n✓ All " + totalTests + " tests passed successfully!");
        } else {
            System.out.println("\n✗ " + (totalTests - passedTests) + " test(s) failed!");
        }
    }

    private static boolean testDepositUpdatesBalance() {
        try {
            Customer regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
            CheckingAccount checkingAccount = new CheckingAccount(regularCustomer, 1000.0);

            double initialBalance = checkingAccount.getBalance();
            double depositAmount = 500.0;

            checkingAccount.deposit(depositAmount);

            double expectedBalance = initialBalance + depositAmount;
            double actualBalance = checkingAccount.getBalance();

            return Math.abs(expectedBalance - actualBalance) < 0.01;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testWithdrawBelowMinimumThrowsException() {
        try {
            Customer regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
            SavingsAccount savingsAccount = new SavingsAccount(regularCustomer, 1500.0);

            double initialBalance = savingsAccount.getBalance();
            double withdrawalAmount = 1200.0;

            try {
                savingsAccount.withdraw(withdrawalAmount);
                // If we get here, no exception was thrown - test fails
                return false;
            } catch (IllegalArgumentException e) {
                // Check if balance didn't change
                double currentBalance = savingsAccount.getBalance();
                boolean balanceUnchanged = Math.abs(initialBalance - currentBalance) < 0.01;

                // Check exception message
                String message = e.getMessage().toLowerCase();
                boolean validMessage = message.contains("withdrawal denied") ||
                        message.contains("insufficient funds");

                return balanceUnchanged && validMessage;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testOverdraftWithinLimitAllowed() {
        try {
            Customer regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
            CheckingAccount checkingAccount = new CheckingAccount(regularCustomer, 1000.0);

            double withdrawalAmount = 1500.0;
            double initialBalance = checkingAccount.getBalance();

            boolean success = checkingAccount.withdraw(withdrawalAmount);

            if (!success) return false;

            double newBalance = checkingAccount.getBalance();
            boolean balanceCorrect = Math.abs(initialBalance - withdrawalAmount - newBalance) < 0.01;
            boolean isNegative = newBalance < 0;
            boolean withinLimit = newBalance >= -1000.0;

            return balanceCorrect && isNegative && withinLimit;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testOverdraftExceedThrowsException() {
        try {
            Customer regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
            CheckingAccount checkingAccount = new CheckingAccount(regularCustomer, 1000.0);

            double initialBalance = checkingAccount.getBalance();
            double withdrawalAmount = 2500.0;

            try {
                checkingAccount.withdraw(withdrawalAmount);
                // If we get here, no exception was thrown - test fails
                return false;
            } catch (IllegalArgumentException e) {
                // Check if balance didn't change
                double currentBalance = checkingAccount.getBalance();
                boolean balanceUnchanged = Math.abs(initialBalance - currentBalance) < 0.01;

                // Check exception message
                String message = e.getMessage().toLowerCase();
                boolean validMessage = message.contains("insufficient funds");

                return balanceUnchanged && validMessage;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean testTransferBetweenAccountsSucceeds() {
        try {
            Customer regularCustomer = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
            Account source = new CheckingAccount(regularCustomer, 1000.0);
            Account target = new SavingsAccount(regularCustomer, 500.0);

            double transferAmount = 300.0;

            boolean success = source.transfer(target, transferAmount);

            if (!success) return false;

            double sourceBalance = source.getBalance();
            double targetBalance = target.getBalance();

            boolean sourceCorrect = Math.abs(700.0 - sourceBalance) < 0.01;
            boolean targetCorrect = Math.abs(800.0 - targetBalance) < 0.01;

            return sourceCorrect && targetCorrect;
        } catch (Exception e) {
            return false;
        }
    }
}