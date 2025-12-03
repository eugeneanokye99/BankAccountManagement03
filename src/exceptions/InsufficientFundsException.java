package exceptions;

public class InsufficientFundsException extends IllegalArgumentException {
    private String accountNumber;
    private double currentBalance;
    private double requestedAmount;

    public InsufficientFundsException(String accountNumber, double currentBalance, double requestedAmount) {
        super(String.format("Insufficient funds in account %s. Current: $%.2f, Requested: $%.2f",
                accountNumber, currentBalance, requestedAmount));
        this.accountNumber = accountNumber;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public String getAccountNumber() { return accountNumber; }
    public double getCurrentBalance() { return currentBalance; }
    public double getRequestedAmount() { return requestedAmount; }
}