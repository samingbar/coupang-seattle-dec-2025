package pizzaworkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.activity.LocalActivityOptions;
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

  // 1. Define LocalActivityOptions with a short timeout
  private final LocalActivityOptions local_options =
      LocalActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .build();
  
  ActivityOptions options =
      ActivityOptions.newBuilder()
          .setStartToCloseTimeout(Duration.ofSeconds(5))
          .build();

  private final PizzaActivities activities =
    Workflow.newActivityStub(PizzaActivities.class, options);
  
  // 3. Add Workflow.newLocalActivityStub
  private final LocalPizzaActivities localActivities =
    Workflow.newLocalActivityStub(LocalPizzaActivities.class, local_options);

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

    Workflow.sleep(Duration.ofSeconds(3));

    Bill bill = new Bill(customer.getCustomerID(), orderNumber, "Pizza", totalPrice);

    CreditCardConfirmation creditCardConfirmation;
    OrderConfirmation confirmation;

    // Update to use local activity
    try {
      creditCardConfirmation = localActivities.processCreditCard(creditCardInfo, bill);
    } catch (ActivityFailure e) {
      logger.error("Unable to process credit card");
      throw ApplicationFailure.newFailureWithCause("Unable to process credit card",
          CreditCardProcessingException.class.getName(), e);
    }

    // Update to use local activity
    try {
      confirmation = localActivities.sendBill(bill, creditCardConfirmation);
    } catch (ActivityFailure e) {
      logger.error("Unable to bill customer");
      throw ApplicationFailure.newFailureWithCause("Unable to bill customer",
          InvalidChargeAmountException.class.getName(), e);
    }

    return confirmation;
  }
}
