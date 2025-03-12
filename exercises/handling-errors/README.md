## Exercise #1: Handling Errors

During this exercise, you will:

- Throw and handle exceptions in Temporal Workflows and Activities
- Use non-retryable errors to fail an Activity
- Locate the details of a failure in Temporal Workflows and Activities in the Event History

Make your changes to the code in the `practice` subdirectory (look for `TODO`
comments that will guide you to where you should make changes to the code). If
you need a hint or want to verify your changes, look at the complete version in
the `solution` subdirectory.

## Setup

You'll need two terminal windows for this exercise.

1. In all terminals, change to the `exercises/handling-errors/practice`
   directory using the following command:
   ```bash
   cd exercises/handling-errors/practice
   ```
2. In one terminal, run `mvn clean compile` to install packages.

## Part A: Throw a non-retryable `ApplicationFailure` to fail an Activity

In this part of the exercise, you will throw a non-retryable Application Failure 
that will fail your Activities.

Application Failures are used to communicate application-specific failures in
Workflows and Activities. In Activities, throwing an `ApplicationFailure` will
not cause the Activity to fail. To have an Activity fail when an `ApplicationFailure` 
is thrown, set it as non-retryable. Any other 
exception that is raised in Java is automatically wrapped to an `ApplicationFailure `
upon being thrown.

1. Start by reviewing the `PizzaActivities.java` file, familiarizing yourself with
   the Activity method declarations.
2. Open the `PizzaActivitiesImpl.java` in your text editor.
3. Add the following import statement `import io.temporal.failure.ApplicationFailure;`
   at the top of this file.
4. In the `sendBill` Activity, notice how an error is thrown if the `chargeAmount`
   is less than 0. If the calculated amount to charge the customer is negative,
   a non-retryable `ApplicationFailure` is thrown. It is important to use a
   non-retryable failure here, as you want to fail the Activity if the amount
   was calculated to be negative. In this error, we pass a reason for the failure
   and the type of error that is being converted to an `ApplicationFailure`.
5. Go to `processCreditCard` Activity, where you will throw an `ApplicationFailure`
   if the credit card fails its validation setp. In this Activity, you will throw
   an error if the entered credit card number does not have 16 digits. Use the
   `ApplicationFailure` code from the previous step as a reference. You should
   pass a `CreditCardProcessingException` as the type to this function. This
   custom exception can be found in `pizzaworkflow/exceptions/CrediCardProcessingException.java`.
6. Save your file.

## Part B: Catch the Activity Failure

In this part of the exercise, you will catch the `ApplicationFailure` that was
thrown from the `processCreditCard` Activity and handle it.

1. Open the `PizzaWorkflowImpl.java` in your text editor.
2. Locate the line with the following code: `creditCardConfirmation = activities.processCreditCard(creditCardInfo, bill);`.
   1. This code calls the `processCreditCard` Activity, and if a non-retryable
      `ApplicationFailure` is thrown, the Workflow will fail. However, it is possible
      to catch this failure and either handle it, or continue to propagate it up.
   2. Wrap this line in a `try/catch` block. However, you will not catch `ApplicationFailure`.
      Since the `ApplicationFailure` in the Activity is designated as non-retryable,
      by the time it reaches the Workflow it is converted to an `ActivityFailure`.
   3. Within the `catch` block, add a logging statement stating that the Activity
      has failed.
   4. After the logging statement, throw another `ApplicationFailure` using
      `ApplicationFailure.newFailure`, passing in a message and the
      `CreditCardProcessingException` as the exception type. This will cause the
      Workflow to fail, as you were unable to bill the customer.
3. Save your file.

## Part C: Run the Workflow

In this part of the exercise, you will run your Workflow and see both your
Workflow and Activity succeed and fail.

In the `Starter.java` file, a valid credit card number as part of an order has
been provided to run this Workflow.

**First, run the Workflow successfully:**

1. In one terminal, compile your code by running `mvn clean compile`.
2. In the same terminal, start the Worker by running:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
   ```
3. In another terminal, start the Workflow by executing `Starter.java`:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
   ```
4. In the Web UI, verify that the Workflow ran successfully to completion.

**Next, you'll modify the starter data to cause the Workflow to fail:**

1. Open `Starter.java` and modify the line
   ```java
   CreditCardInfo cardInfo = new CreditCardInfo("Lisa Anderson", "4242424242424242");
   ```
   by deleting a digit from the String. Save this file.
2. Recompile your code using:
   ```bash
   mvn clean compile
   ```
3. Stop the worker using **CMD-C** on Mac or **Ctrl-C** on Windows/Linux
4. Restart the Worker by running:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
   ```
5. In another terminal, start the Workflow by executing `Starter.java`:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
   ```
6. You should see the Workflow fail in the terminal where you executed `Starter.java`.
   Also check the WebUI and view the failure there.

### This is the end of the exercise.
