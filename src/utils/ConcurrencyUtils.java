package utils;

import account.Account;
import account.CheckingAccount;
import account.SavingsAccount;
import customer.Customer;
import customer.RegularCustomer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyUtils {


         //Transaction task for concurrent operations using existing Account classes

        private record TransactionTask(Account account, String type, double amount, int threadNumber) implements Runnable {

        @Override
            public void run() {
                String accountType = account instanceof SavingsAccount ? "Savings" : "Checking";

                System.out.printf("Thread-%d: %s $%.0f from %s Account %s%n",
                        threadNumber,
                        type.equals("DEPOSIT") ? "Depositing" : "Withdrawing",
                        amount,
                        accountType,
                        account.getAccountNumber());

                try {
                    if (type.equals("DEPOSIT")) {
                        account.deposit(amount);
                    } else {
                        boolean success = account.withdraw(amount);
                        if (!success) {
                            System.out.printf("Thread-%d: Withdrawal failed for Account %s (Insufficient funds)%n",
                                    threadNumber, account.getAccountNumber());
                        }
                    }
                } catch (Exception e) {
                    System.out.printf("Thread-%d: Error in transaction - %s%n",
                            threadNumber, e.getMessage());
                }
            }
        }



     //Static method to run the simulation programmatically
    public static void runConcurrentSimulation() {
        System.out.println("1. Concurrent Transactions Simulation\n");
        System.out.println("Running concurrent transaction simulation...\n");

        // Create customers
        Customer customer1 = new RegularCustomer("John Doe", 30, "0551234567", "Accra");
        Customer customer2 = new RegularCustomer("Jane Smith", 35, "0557654321", "Kumasi");

        // Create accounts
        Account checkingAccount = new CheckingAccount(customer1, 5000.00);
        Account savingsAccount = new SavingsAccount(customer2, 3000.00);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // Concurrent transactions
        executor.submit(new TransactionTask(checkingAccount, "DEPOSIT", 500, 1));
        executor.submit(new TransactionTask(checkingAccount, "WITHDRAW", 200, 2));
        executor.submit(new TransactionTask(checkingAccount, "DEPOSIT", 300, 3));
        executor.submit(new TransactionTask(checkingAccount, "WITHDRAW", 100, 4));

        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nThread-safe operations completed successfully.");
        System.out.printf("Final Balance for Checking Account %s: $%,.2f%n",
                checkingAccount.getAccountNumber(),
                checkingAccount.getBalance());

        System.out.printf("Final Balance for Savings Account %s: $%,.2f%n",
                savingsAccount.getAccountNumber(),
                savingsAccount.getBalance());
    }

     // Simulate concurrent transfers between accounts
    public static void runConcurrentTransferSimulation() {
        System.out.println("2. Concurrent Transfer Simulation\n");
        System.out.println("Running concurrent transfer simulation...\n");

        // Create customers
        Customer customer = new RegularCustomer("Transfer User", 40, "0551111111", "Tema");

        // Create accounts
        Account sourceAccount = new CheckingAccount(customer, 10000.00);
        Account targetAccount = new SavingsAccount(customer, 2000.00);

        ExecutorService executor = Executors.newFixedThreadPool(6);

        // Submit concurrent transfers
        for (int i = 1; i <= 6; i++) {
            final int threadNum = i;
            double amount = threadNum * 100; // Varying amounts: 100, 200, 300...

            executor.submit(() -> {
                System.out.printf("Thread-%d: Attempting to transfer $%.0f from %s to %s%n",
                        threadNum, amount,
                        sourceAccount.getAccountNumber(),
                        targetAccount.getAccountNumber());

                boolean success = sourceAccount.transfer(targetAccount, amount);

                if (success) {
                    System.out.printf("Thread-%d: Transfer successful!%n", threadNum);
                } else {
                    System.out.printf("Thread-%d: Transfer failed (insufficient funds)%n", threadNum);
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\nConcurrent transfers completed.");
        System.out.printf("Source Account (%s) Balance: $%,.2f%n",
                sourceAccount.getAccountNumber(),
                sourceAccount.getBalance());
        System.out.printf("Target Account (%s) Balance: $%,.2f%n",
                targetAccount.getAccountNumber(),
                targetAccount.getBalance());
    }


     //Stress test with high concurrency
    public static void runStressTest() {
        System.out.println("3. High Concurrency Stress Test\n");
        System.out.println("Running high concurrency stress test...\n");

        Customer customer = new RegularCustomer("Stress Test User", 25, "0559999999", "Takoradi");
        Account account = new CheckingAccount(customer, 50000.00);

        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        System.out.println("Starting " + numThreads + " concurrent threads...");

        for (int i = 1; i <= numThreads; i++) {
            final int threadNum = i;

            // Alternate between deposit and withdraw
            if (i % 2 == 0) {
                executor.submit(() -> {
                    System.out.printf("Thread-%d: Depositing $250 to %s%n",
                            threadNum, account.getAccountNumber());
                    account.deposit(250.00);
                });
            } else {
                executor.submit(() -> {
                    System.out.printf("Thread-%d: Withdrawing $100 from %s%n",
                            threadNum, account.getAccountNumber());
                    account.withdraw(100.00);
                });
            }
        }

        executor.shutdown();
        try {
            boolean finished = executor.awaitTermination(15, TimeUnit.SECONDS);
            if (!finished) {
                System.out.println("\nWarning: Not all threads completed within timeout!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Calculate expected balance
        // 10 deposits of $250 = $2500
        // 10 withdrawals of $100 = $1000
        // Starting: $50000 + $2500 - $1000 = $51500
        double expected = 51500.00;
        double actual = account.getBalance();

        System.out.println("\nStress test completed.");
        System.out.printf("Expected Balance: $%,.2f%n", expected);
        System.out.printf("Actual Balance: $%,.2f%n", actual);

        if (Math.abs(expected - actual) < 0.01) {
            System.out.println("✓ No race conditions detected!");
        } else {
            System.out.println("✗ Race condition detected! Balance mismatch.");
            System.out.println("  Difference: $" + (actual - expected));
        }
    }

    /**
     * Run all simulations
     */
    public static void runAllSimulations() {
        System.out.println("=".repeat(60));
        System.out.println("CONCURRENCY SIMULATIONS USING EXISTING CLASSES");
        System.out.println("=".repeat(60) + "\n");

        runConcurrentSimulation();
        System.out.println("\n" + "-".repeat(60) + "\n");

        runConcurrentTransferSimulation();
        System.out.println("\n" + "-".repeat(60) + "\n");

        runStressTest();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("ALL SIMULATIONS COMPLETED");
        System.out.println("=".repeat(60));
    }
}