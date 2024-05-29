package pizzaworkflow.model;

import java.util.List;

public class PizzaOrder {

  private String orderNumber;
  private Customer customer;
  private List<Pizza> items;
  private boolean isDelivery;
  private Address address;
  private CreditCardInfo cardInfo;

  public PizzaOrder() {}

  public PizzaOrder(String orderNumber, Customer customer, List<Pizza> items, boolean isDelivery,
      Address address, CreditCardInfo cardInfo) {

    this.orderNumber = orderNumber;
    this.customer = customer;
    this.items = items;
    this.isDelivery = isDelivery;
    this.address = address;
    this.cardInfo = cardInfo;
  }

  public String getOrderNumber() {
    return orderNumber;
  }

  public void setOrderNumber(String orderNumber) {
    this.orderNumber = orderNumber;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public List<Pizza> getItems() {
    return items;
  }

  public void setItems(List<Pizza> items) {
    this.items = items;
  }

  public boolean isDelivery() {
    return isDelivery;
  }

  public void setDelivery(boolean delivery) {
    isDelivery = delivery;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public CreditCardInfo getCardInfo() {
    return cardInfo;
  }

  public void setCreditCardInfo(CreditCardInfo cardInfo) {
    this.cardInfo = cardInfo;
  }
}
