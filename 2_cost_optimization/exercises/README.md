## Exercise: Cost Optimization with Local Activities

During this exercise, you will:

- Convert standard Activities to Local Activities
- Configure appropriate timeouts for Local Activities
- Observe how Local Activities can reduce Workflow latency and cost

This module reuses the familiar pizza-ordering Workflow from the error-handling
exercises, but focuses on moving certain calls to Local Activities for better
performance characteristics.

## Setup

You'll need two terminal windows for this exercise.

1. From the repository root, change to the `2_cost_optimization/local_activiites/practice`
   directory using the following command:
   ```bash
   cd 2_cost_optimization/local_activiites/practice
   ```
2. In one terminal, run `mvn clean compile` to install packages.

Make your changes to the code in the `practice` subdirectory (look for `TODO`
comments that will guide you to where you should make changes to the code). If
you need a hint or want to verify your changes, look at the complete version in
the `solution` subdirectory.

## Part A: Create Local Activities for Billing and Payment

In this part of the exercise, you will refactor the billing and payment logic
into a separate Local Activity interface and implementation. The distance
calculation will remain a normal Activity.

1. Open the `PizzaActivities.java` file in your text editor.
2. Update this interface so that it only declares the `getDistance(Address address)`
   Activity method.
3. Create a new `LocalPizzaActivities.java` interface in the same package.
   1. Annotate the interface with `@ActivityInterface`.
   2. Add two Activity methods to this interface:
      - `OrderConfirmation sendBill(Bill bill, CreditCardConfirmation creditCardConfirmation);`
      - `CreditCardConfirmation processCreditCard(CreditCardInfo creditCard, Bill bill);`
4. Create a new `LocalPizzaActivitiesImpl.java` class that implements
   `LocalPizzaActivities`.
   1. Move the `sendBill` and `processCreditCard` logic from
      `PizzaActivitiesImpl.java` into this new class.
   2. Add a logger and any necessary imports, mirroring the existing
      implementation in `PizzaActivitiesImpl.java`.
5. Update `PizzaActivitiesImpl.java` so that it only implements the `getDistance`
   Activity.

## Part B: Use Local Activities in the Workflow

Now you will configure Local Activity options in your Workflow and update the
Workflow to call your new Local Activities.

1. Open `PizzaWorkflowImpl.java` in your text editor.
2. At the top of the file, ensure `LocalActivityOptions` is imported:
   ```java
   import io.temporal.activity.LocalActivityOptions;
   ```
3. Inside `PizzaWorkflowImpl`, define a `LocalActivityOptions` field with a short
   `StartToCloseTimeout`, for example 5 seconds.
4. Create a Local Activity stub using `Workflow.newLocalActivityStub`, passing
   in `LocalPizzaActivities.class` and the `LocalActivityOptions` you defined.
5. In the `orderPizza` method, locate the call to
   `activities.processCreditCard(creditCardInfo, bill);` and update it to call
   `localActivities.processCreditCard(creditCardInfo, bill);` instead, keeping
   the existing try/catch and error-handling logic.
6. Similarly, update the call to `activities.sendBill(bill, creditCardConfirmation);`
   so that it invokes `localActivities.sendBill(bill, creditCardConfirmation);`.

## Part C: Register and Run Your Implementation

Finally, you will register your new Local Activities implementation with the
Worker and run your Workflow.

1. Open `PizzaWorker.java`.
2. In the `main` method, ensure that both Activity implementations are registered:
   ```java
   worker.registerActivitiesImplementations(
       new PizzaActivitiesImpl(),
       new LocalPizzaActivitiesImpl()
   );
   ```
3. Save all modified files.
4. From the `2_cost_optimization/local_activiites/practice` directory, compile
   your project:
   ```bash
   mvn clean compile
   ```
5. In one terminal, start the Worker:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
   ```
6. In another terminal, from the same directory, start the Workflow by executing:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
   ```
7. Use the Web UI to inspect the Workflow Execution and observe how the
   Local Activities behave compared to the standard Activities.

### This is the end of the exercise.
