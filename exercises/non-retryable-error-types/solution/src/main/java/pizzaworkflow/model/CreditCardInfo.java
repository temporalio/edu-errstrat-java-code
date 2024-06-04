package pizzaworkflow.model;

public class CreditCardInfo {
  
  private String holderName;
  private String number;

  public CreditCardInfo() {
  }

  public CreditCardInfo(String holderName, String number) {
      this.holderName = holderName;
      this.number = number;
  }

  public String getHolderName() {
      return holderName;
  }

  public void setHolderName(String holderName) {
      this.holderName = holderName;
  }

  public String getNumber() {
      return number;
  }

  public void setNumber(String number) {
      this.number = number;
  }

  @Override
  public String toString(){
    return "Holder Name: " + holderName + " number: " + number;
  }

}
