package pizzaworkflow.exceptions;

public class CreditCardProcessingException extends Exception {

  public CreditCardProcessingException() {
    super();
  }

  public CreditCardProcessingException(String message) {
    super(message);
  }

  public CreditCardProcessingException(String message, Throwable cause) {
    super(message, cause);
  }

  public CreditCardProcessingException(Throwable cause) {
    super(cause);
  }
}
