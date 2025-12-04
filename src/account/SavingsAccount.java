package account;

import customer.Customer;
import utils.CustomUtils;

public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;

    public SavingsAccount(Customer customer, double openingBalance) {
        super(customer, openingBalance);
        this.interestRate = 3.5;
        this.minimumBalance = 500.0;

        // Validate initial deposit meets minimum balance requirement
        if (openingBalance < minimumBalance) {
            throw new IllegalArgumentException(
                    "Initial deposit for Savings Account must be at least $" + minimumBalance
            );
        }
    }

    // Getters
    public double getInterestRate() {
        return interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    public double calculateInterest() {
        return getBalance() * (interestRate / 100);
    }

    // Override withdraw to enforce minimum balance
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        double newBalance = getBalance() - amount;

        // Check if withdrawal would violate minimum balance
        if (newBalance < minimumBalance) {
            throw new IllegalArgumentException(
                    String.format("Withdrawal denied. Minimum balance of $%.2f must be maintained.", minimumBalance)
            );
        }

        // If valid, perform withdrawal
        setBalance(newBalance);
        return true;
    }


    @Override
    public void displayAccountDetails() {
        CustomUtils.print("=== Savings Account Details ===");
        CustomUtils.print("Account Number: " + getAccountNumber());
        CustomUtils.print("Customer: " + getCustomer().getName());
        CustomUtils.print("Balance: $" + String.format("%.2f", getBalance()));
        CustomUtils.print("Interest Rate: " + interestRate + "%");
        CustomUtils.print("Minimum Balance: $" + String.format("%.2f", minimumBalance));
        CustomUtils.print("Status: " + getStatus());
        CustomUtils.print("Interest Earned: $" + String.format("%.2f", calculateInterest()));
    }

    @Override
    public String getAccountType() {
        return "Savings";
    }

}