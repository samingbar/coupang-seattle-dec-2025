package pizzaworkflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import pizzaworkflow.model.Distance;
import pizzaworkflow.model.Address;
import pizzaworkflow.model.OrderConfirmation;
import pizzaworkflow.model.Bill;
import pizzaworkflow.model.CreditCardConfirmation;
import pizzaworkflow.model.CreditCardInfo;

@ActivityInterface
public interface PizzaActivities {
  @ActivityMethod
  Distance getDistance(Address address);
  //Local interfaces moved to a new class 
}

