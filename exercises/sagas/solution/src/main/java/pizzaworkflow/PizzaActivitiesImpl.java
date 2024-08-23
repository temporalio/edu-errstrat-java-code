package pizzaworkflow;

import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.PizzaOrder;
import pizzaworkflow.model.Address;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;

import pizzaworkflow.exceptions.InvalidChargeAmountException;
import pizzaworkflow.exceptions.CreditCardProcessingException;

import java.time.Instant;
import java.time.Duration;
import java.util.Random;

import io.temporal.activity.Activity;
import io.temporal.failure.ApplicationFailure;

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
      throw Activity.wrap(new InvalidChargeAmountException(errorMessage));
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

    if (creditCard.getNumber().length() == 16) {
      String cardProcessingConfirmationNumber = "PAYME-78759";
      return new CreditCardConfirmation(creditCard, cardProcessingConfirmationNumber, bill.getAmount(),
          Instant.now().getEpochSecond());
    } else {
      throw ApplicationFailure.newFailure("Invalid credit card number",
          CreditCardProcessingException.class.getName());
      // This also works
      // throw Activity.wrap(new CreditCardProcessingException("Invalid credit card
      // number"));
    }
  }

  @Override
  public boolean notifyDeliveryDriver(OrderConfirmation order) {

    Random rand = new Random();

    /*
     * This is a simulation of attempting to notify a delivery driver that
     * the order is ready for delivery. It starts by generating a number from 0 -
     * 14.
     * From there a loop is iterated over from 0 < 10, each time checking to
     * see if the random number matches the loop counter and then sleeping for 5
     * seconds. Each iteration of the loop sends a heartbeat back letting the
     * Workflow know that progress is still being made. If the number matches a
     * loop counter, it is a success. If it doesn't, then a delivery driver was
     * unable to be contacted and failure is returned.
     */
    int successSimulation = rand.nextInt(15);

    for (int x = 0; x < 10; x++) {

      if (successSimulation == x) {
        // Pretend to use the `order` variable to notify the driver
        logger.info("Delivery driver responded");
        return true;
      }

      Activity.getExecutionContext().heartbeat("Heartbeat: " + x);
      logger.info("Heartbeat: " + x);
      try {
        Thread.sleep(5000); // 5 seconds;
      } catch (InterruptedException e) {
        continue;
      }

    }
    logger.info("Delivery driver didn't respond");
    return false;
  }

  public String updateInventory(PizzaOrder order) {
    logger.info("Updated inventory for order: {}", order.getOrderNumber());
    return "Updated inventory for order: " + order.getOrderNumber();
  }

  public String revertInventory(PizzaOrder order) {
    logger.info("Reverted changes to inventory caused by order: {}", order.getOrderNumber());
    return "Reverted changes to inventory caused by order" + order.getOrderNumber();
  }

  public String refundCustomer(CreditCardInfo creditCardInfo) {
    logger.info("Customer refunded: ", creditCardInfo.getHolderName());
    return "Customer refunded: " + creditCardInfo.getHolderName();
  }

}
