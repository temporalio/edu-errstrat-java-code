package pizzaworkflow.model;

public class CreditCardConfirmation {

  private CreditCardInfo cardInfo;
  private String confirmationNumber;
  private int amount;
  private long billingTimestamp;

  public CreditCardConfirmation() {
  }

  public CreditCardConfirmation(CreditCardInfo cardInfo, String confirmationNumber, int amount, long billingTimestamp) {
    this.cardInfo = cardInfo;
    this.confirmationNumber = confirmationNumber;
    this.amount = amount;
    this.billingTimestamp = billingTimestamp;
  }

  public CreditCardInfo getCardInfo() {
    return cardInfo;
  }

  public void setCardInfo(CreditCardInfo cardInfo) {
    this.cardInfo = cardInfo;
  }

  public String getConfirmationNumber() {
    return confirmationNumber;
  }

  public void setConfirmationNumber(String confirmationNumber) {
    this.confirmationNumber = confirmationNumber;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public long getBillingTimestamp() {
    return billingTimestamp;
  }

  public void setBillingTimestamp(long billingTimestamp) {
    this.billingTimestamp = billingTimestamp;
  }

  @Override
  public String toString() {
    return "CreditCardConfirmation{" + "cardInfo='" + cardInfo
        + '\'' + ", confirmationNumber='" + confirmationNumber + '\'' + ", billingTimestamp="
        + billingTimestamp + ", amount=" + amount + '}';
  }

}
