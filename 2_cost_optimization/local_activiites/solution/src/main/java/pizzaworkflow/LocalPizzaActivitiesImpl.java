//2. Create a local pizza activities implementation

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
import io.temporal.failure.ApplicationFailure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalPizzaActivitiesImpl implements LocalPizzaActivities {

  private static final Logger logger = LoggerFactory.getLogger(LocalPizzaActivitiesImpl.class);

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
      throw ApplicationFailure.newNonRetryableFailure("Invalid credit card number",
        CreditCardProcessingException.class.getName());
    }
  }
}
