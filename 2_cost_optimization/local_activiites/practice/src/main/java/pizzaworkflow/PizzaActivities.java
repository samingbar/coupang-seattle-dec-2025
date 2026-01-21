package pizzaworkflow;

import io.temporal.activity.ActivityInterface;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.Address;
import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;

@ActivityInterface
public interface PizzaActivities {

  Distance getDistance(Address address);

  //TODO: Move to LocalPizzaActivities.java
  OrderConfirmation sendBill(Bill bill, CreditCardConfirmation creditCardConfirmation);

  //TODO: Move to LocalPizzaActivities.java
  CreditCardConfirmation processCreditCard(CreditCardInfo creditCard, Bill bill);

}