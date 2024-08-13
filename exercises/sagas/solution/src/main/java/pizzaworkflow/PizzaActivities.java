package pizzaworkflow;

import io.temporal.activity.ActivityInterface;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.Address;
import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.PizzaOrder;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;

@ActivityInterface
public interface PizzaActivities {

  Distance getDistance(Address address);

  OrderConfirmation sendBill(Bill bill, CreditCardConfirmation creditCardConfirmation);

  CreditCardConfirmation processCreditCard(CreditCardInfo creditCard, Bill bill);

  boolean notifyDeliveryDriver(OrderConfirmation order);

  String updateInventory(PizzaOrder order);

  String revertInventory(PizzaOrder order);

  String refundCustomer(CreditCardInfo creditCardInfo);


}
