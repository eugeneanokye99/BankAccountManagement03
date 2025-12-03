package account;

import customer.Customer;
import utils.CustomUtils;

public class SavingsAccount extends Account {
    private final double interestRate;
    private final double minimumBalance;

    public SavingsAccount(Customer customer, double openingBalance) {
        super(customer, openingBalance);
        this.interestRate = 3.5; // 3.5% annually as per requirements
        this.minimumBalance = 500.0; // $500 minimum balance as per requirements

        // Validate initial deposit meets minimum balance requirement
        if (openingBalance < minimumBalance) {
            throw new IllegalArgumentException(
                    "Initial deposit for Savings Account must be at least $" + minimumBalance
            );
        }
    }

    // Getters for Savings-specific properties
    public double getInterestRate() {
        return interestRate;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

    // Calculate interest earned
    public double calculateInterest() {
        return getBalance() * (interestRate / 100);
    }

    // Override withdraw to enforce minimum balance
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            CustomUtils.print("Withdrawal amount must be positive");
            return false;
        }

        double newBalance = getBalance() - amount;

        // Check if withdrawal would violate minimum balance
        if (newBalance < minimumBalance) {
            System.out.printf("Withdrawal denied. Minimum balance of $%.2f must be maintained.%n", minimumBalance);
            System.out.printf("Current balance: $%.2f, After withdrawal: $%.2f%n", getBalance(), newBalance);
            return false;
        }

        // If valid, perform withdrawal
        setBalance(newBalance);
        return true;
    }

    // Implement abstract methods
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