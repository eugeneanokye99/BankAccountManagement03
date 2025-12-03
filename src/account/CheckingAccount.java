package account;

import customer.Customer;
import utils.CustomUtils;

public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double monthlyFee;

    public CheckingAccount(Customer customer, double openingBalance) {
        super(customer, openingBalance);
        this.overdraftLimit = 1000.0; // $1000 overdraft limit as per requirements
        this.monthlyFee = 10.0; // $10 monthly fee as per requirements
    }

    // Getters for Checking-specific properties
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    // Override withdraw to allow overdraft up to limit
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            CustomUtils.print("Withdrawal amount must be positive");
            return false;
        }

        double maxWithdrawal = getBalance() + overdraftLimit;

        // Check if withdrawal exceeds overdraft limit
        if (amount > maxWithdrawal) {
            CustomUtils.printf("Withdrawal denied. Exceeds overdraft limit of $%.2f.%n", overdraftLimit);
            CustomUtils.printf("Current balance: $%.2f, Maximum withdrawal: $%.2f%n", getBalance(), maxWithdrawal);
            return false;
        }

        // If valid, perform withdrawal (can go negative up to overdraft limit)
        double newBalance = getBalance() - amount;
        setBalance(newBalance);

        if (newBalance < 0) {
            CustomUtils.printf("Overdraft used. Negative balance: $%.2f%n", newBalance);
        }

        return true;
    }

    // Implement abstract methods
    @Override
    public void displayAccountDetails() {
        CustomUtils.print("=== Checking Account Details ===");
        CustomUtils.print("Account Number: " + getAccountNumber());
        CustomUtils.print("Customer: " + getCustomer().getName());
        CustomUtils.print("Customer Type: " + getCustomer().getCustomerType());
        CustomUtils.print("Balance: $" + String.format("%.2f", getBalance()));
        CustomUtils.print("Overdraft Limit: $" + String.format("%.2f", overdraftLimit));
        CustomUtils.print("Monthly Fee: $" + String.format("%.2f", monthlyFee));
        CustomUtils.print("Status: " + getStatus());

        // Show fee waiver info for premium customers
        if (getCustomer().getCustomerType().equals("Premium")) {
            CustomUtils.print("Monthly Fee Status: WAIVED (Premium Customer)");
        }
    }

    @Override
    public String getAccountType() {
        return "Checking";
    }

    public void applyMonthlyFee() {
    }
}