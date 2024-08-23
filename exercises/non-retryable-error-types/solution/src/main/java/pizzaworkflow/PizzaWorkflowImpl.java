package pizzaworkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;

import pizzaworkflow.model.Address;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.Customer;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;
import pizzaworkflow.model.Pizza;
import pizzaworkflow.model.PizzaOrder;
import pizzaworkflow.exceptions.CreditCardProcessingException;
import pizzaworkflow.exceptions.InvalidChargeAmountException;
import pizzaworkflow.exceptions.OutOfServiceAreaException;

import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;

public class PizzaWorkflowImpl implements PizzaWorkflow {

  public static final Logger logger = Workflow.getLogger(PizzaWorkflowImpl.class);

  RetryOptions retryOptions = RetryOptions.newBuilder()
      .setInitialInterval(Duration.ofSeconds(15))
      .setBackoffCoefficient(2.0)
      .setMaximumInterval(Duration.ofSeconds(60))
      .setMaximumAttempts(25)
      .setDoNotRetry(CreditCardProcessingException.class.getName())
      .build();

  ActivityOptions options = ActivityOptions.newBuilder()
      .setStartToCloseTimeout(Duration.ofSeconds(300))
      .setRetryOptions(retryOptions)
      .setHeartbeatTimeout(Duration.ofSeconds(10))
      .build();

  private final PizzaActivities activities =
      Workflow.newActivityStub(PizzaActivities.class, options);

  @Override
  public OrderConfirmation orderPizza(PizzaOrder order) {

    String orderNumber = order.getOrderNumber();
    Customer customer = order.getCustomer();
    List<Pizza> items = order.getItems();
    boolean isDelivery = order.isDelivery();
    Address address = order.getAddress();
    CreditCardInfo creditCardInfo = order.getCardInfo();

    logger.info("orderPizza Workflow Invoked");

    int totalPrice = 0;
    for (Pizza pizza : items) {
      totalPrice += pizza.getPrice();
    }

    Distance distance;
    try {
      distance = activities.getDistance(address);
    } catch (NullPointerException e) {
      logger.error("Unable to get distance");
      throw new NullPointerException("Unable to get distance");
    }

    if (isDelivery && (distance.getKilometers() > 25)) {
      logger.error("Customer lives outside the service area");
      throw ApplicationFailure.newFailure("Customer lives outside the service area",
          OutOfServiceAreaException.class.getName());
    }

    logger.info("distance is {}", distance.getKilometers());

    // Use a short Timer duration here to simulate the passage of time
    // while avoiding delaying the exercise.
    Workflow.sleep(Duration.ofSeconds(3));

    Bill bill = new Bill(customer.getCustomerID(), orderNumber, "Pizza", totalPrice);

    CreditCardConfirmation creditCardConfirmation;

    try {
      creditCardConfirmation = activities.processCreditCard(creditCardInfo, bill);
    } catch (ActivityFailure e) {
      logger.error("Unable to process credit card");
      throw ApplicationFailure.newFailure("Unable to process credit card",
          CreditCardProcessingException.class.getName());
    }

    OrderConfirmation confirmation;
    try {
      confirmation = activities.sendBill(bill, creditCardConfirmation);
    } catch (ActivityFailure e) {
      logger.error("Unable to bill customer");
      throw ApplicationFailure.newFailure("Unable to bill customer",
          InvalidChargeAmountException.class.getName());
    }

    boolean deliveryDriverAvailable = activities.notifyDeliveryDriver(confirmation);
    if(deliveryDriverAvailable){
      return confirmation;
    } else{
      // Notify customer delivery is not available and they will have to come
      // get their pizza, or cancel the order and compensate.

      // For this exercise, change the value of the status variable to "DELIVERY"
      confirmation.setStatus("DELIVERY FAILURE");
      return confirmation;
    }

    
  }
}
