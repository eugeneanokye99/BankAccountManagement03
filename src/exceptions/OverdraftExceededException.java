package exceptions;

public class OverdraftExceededException extends IllegalArgumentException {

      public OverdraftExceededException(String message) {
            super(message);
        }

}