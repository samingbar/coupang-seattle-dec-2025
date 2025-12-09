// 1. Create a local pizza activities interface

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
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ActivityInterface
public interface LocalPizzaActivities {

    @ActivityMethod
    OrderConfirmation sendBill(Bill bill, CreditCardConfirmation creditCardConfirmation);

    @ActivityMethod
    CreditCardConfirmation processCreditCard(CreditCardInfo creditCard, Bill bill);
    }