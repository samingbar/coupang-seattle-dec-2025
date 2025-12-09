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
}
