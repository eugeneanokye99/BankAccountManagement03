package exceptions;

public class InsufficientFundsException extends IllegalArgumentException {

    public InsufficientFundsException(String accountNumber, double currentBalance, double requestedAmount) {
        super(String.format("Insufficient funds in account %s. Current: $%.2f, Requested: $%.2f",
                accountNumber, currentBalance, requestedAmount));
    }

}