package services;

import account.Account;
import account.SavingsAccount;

public record TransactionTask(Account account, String type, double amount, int threadNumber) implements Runnable {

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