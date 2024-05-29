package pizzaworkflow;

import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.Address;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;

import pizzaworkflow.exceptions.InvalidChargeAmountException;
import pizzaworkflow.exceptions.OutOfServiceAreaException;
import pizzaworkflow.exceptions.CreditCardProcessingException;

import java.time.Instant;

import io.temporal.activity.Activity;
// TODO Part A: Add the necessary statement to import ApplicationFailure

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PizzaActivitiesImpl implements PizzaActivities {

  private static final Logger logger = LoggerFactory.getLogger(PizzaActivitiesImpl.class);

  @Override
  public Distance getDistance(Address address) {

    logger.info("getDistance invoked; determining distance to customer address");

    // this is a simulation, which calculates a fake (but consistent)
    // distance for a customer address based on its length. The value
    // will therefore be different when called with different addresses,
    // but will be the same across all invocations with the same address.

    int kilometers = address.getLine1().length() + address.getLine2().length() - 10;
    if (kilometers < 1) {
      kilometers = 5;
    }

    Distance distance = new Distance(kilometers);

    logger.info("getDistance complete: {}", distance.getKilometers());
    return distance;
  }

  @Override
  public OrderConfirmation sendBill(Bill bill, CreditCardConfirmation creditCardConfirmation) {
    int amount = bill.getAmount();

    logger.info("sendBill invoked: customer: {} amount: {}", bill.getCustomerID(), amount);

    int chargeAmount = amount;

    // This month's special offer: Get $5 off all orders over $30
    if (amount > 3000) {
      logger.info("Applying discount");

      chargeAmount -= 500; // reduce amount charged by 500 cents
    }

    // reject invalid amounts before calling the payment processor
    if (chargeAmount < 0) {
      logger.error("invalid charge amount: {%d} (must be above zero)", chargeAmount);
      String errorMessage = "invalid charge amount: " + chargeAmount;
      throw ApplicationFailure.newNonRetryableFailure(errorMessage, InvalidChargeAmountException.class.getName());
    }

    // pretend we called a payment processing service here
    OrderConfirmation confirmation = new OrderConfirmation(bill.getOrderNumber(), "SUCCESS",
        "P24601", Instant.now().getEpochSecond(), chargeAmount, creditCardConfirmation);

    logger.debug("Sendbill complete: Confirmation Number: {}",
        confirmation.getConfirmationNumber());

    return confirmation;
  }

  @Override
  public CreditCardConfirmation processCreditCard(CreditCardInfo creditCard, Bill bill) {

    if(creditCard.getNumber().length() == 16) {
      String cardProcessingConfirmationNumber = "PAYME-78759";
      return new CreditCardConfirmation(creditCard, cardProcessingConfirmationNumber, bill.getAmount(), Instant.now().getEpochSecond());
    } else {
      // TODO Part A: Throw a failure here to fail the Activity
      // if the credit card "processing" fails. This failure should fail the
      // Activity and NOT retry. Pass in a valid error message and the error
      // type, using the custom exception defined in `exceptions/CreditCardProcessingException.java`
    }
  }
}
