package account;

import customer.Customer;
import exceptions.OverdraftExceededException;
import utils.CustomUtils;

public class CheckingAccount extends Account {
    private final double overdraftLimit;
    private final double monthlyFee;

    public CheckingAccount(Customer customer, double openingBalance) {
        super(customer, openingBalance);
        this.overdraftLimit = 1000.0;
        this.monthlyFee = 10.0;
    }

    // Getters
    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    // Override withdraw to allow overdraft up to limit
    @Override
    public boolean withdraw(double amount) throws OverdraftExceededException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        double maxWithdrawal = getBalance() + overdraftLimit;
        double newBalance = getBalance() - amount;

        // Check if withdrawal exceeds available balance + overdraft limit
        if (amount > maxWithdrawal && newBalance < 0) {
            throw new OverdraftExceededException(
                    String.format("Insufficient funds. Exceeds overdraft limit of $%.2f.", overdraftLimit)
            );
        }


        setBalance(newBalance);



        return true;
    }


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


}