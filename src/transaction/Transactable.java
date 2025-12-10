package transaction;

import account.Account;
import exceptions.OverdraftExceededException;

public interface Transactable {
    boolean processTransaction(double amount, String type) throws OverdraftExceededException;
    boolean transfer(Account targetAccount, double amount);
}