## Exercise #2: Modifying Activity Options Using Non-Retryable Error Types and Heartbeats

During this exercise, you will:

- Configure non-retryable error types for Activities
- Implement customize retry policies for Activities

Make your changes to the code in the `practice` subdirectory (look for `TODO`
comments that will guide you to where you should make changes to the code). If
you need a hint or want to verify your changes, look at the complete version in
the `solution` subdirectory.

## Setup

You'll need two terminal windows for this exercise.

1. In all terminals, change to the `exercises/non-retryable-error-types/practice`
   directory using the following command:
   ```bash
   cd exercises/non-retryable-error-types/practice
   ```
2. In one terminal, run `mvn clean compile` to install packages.
3. Note that the `Starter.java` file has had the credit card number modified to be
   invalid.
   ```java
    // This only has 15 digits
    CreditCardInfo cardInfo = new CreditCardInfo("Lisa Anderson", "424242424242424");
   ``` 

## Part A: Convert Non-Retryable Errors to Be Handled By a Retry Policy

In this part of the exercise, you will modify the `ApplicationFailure` you defined
in `processCreditCard` method in the first exercise to not be set as non-retryable
by default. After consideration, you've determined that while you may want to
immediately fail your Workflow on failure, others who call your Activity may not.

1. Open `PizzaActivitiesImpl.java`.
2. In the `processCreditCard` method, modify the exception that is being thrown
   to be retryable.
   1. There are two ways you can do this. You can either throw an `Application.newFailure`
      or you can use `Activity.wrap()` to wrap and throw a new `CreditCardProcessingException`.
   2. Now, when these errors are thrown from an Activity, the Activity will be retried.
3. Save your file.
4. Compile your file using `mvn clean compile`.
5. Verify that your Error is now being retried by attempting to execute the Workflow
   1. In one terminal, start the Worker by running:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
      ```
   2. In another terminal, start the Workflow by executing `Starter.java`:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
      ```
   3. Go to the WebUI and view the status of the Workflow. It should be
      **Running**. Inspect the Workflow and see that it is currently retrying
      the exception, verifying that the exception is no longer non-retryable.
   4. Terminate this Workflow in the WebUI, as it will never successfully complete.
   5. Stop your Worker using **Cmd - C**

## Part B: Configure Retry Policies to set Non-Retryable Error Types

Now that the exception from the `processCreditCard` Activity is no longer set
to non-retryable, others who call your Activity may decide how to handle the failure.
However, you have decided that you do not want the Activity to retry upon failure.
In this part of the exercise, you will configure a Retry Policy to disallow this
using non-retryable error types.

Recall that a Retry Policy has the following attributes:

- Initial Interval: Amount of time that must elapse before the first retry occurs
- Backoff Coefficient: How much the retry interval increases (default is 2.0)
- Maximum Interval: The maximum interval between retries
- Maximum Attempts: The maximum number of execution attempts that can be made in the presence of failures

You can also specify errors types that are not retryable in the Retry Policy. These
are known as non-retryable error types.

1. In the `processCreditCard` Activity in `PizzaActivitiesImpl.java`, you threw an
   `ApplicationFailure` where the type was set to `CreditCardProcessingException`.
   Now you will specify that error type as non-retryable.
2. Open `PizzaWorkflowImpl.java`.
3. A RetryPolicy has already been defined with the following configuration:

```java
  RetryOptions retryOptions = RetryOptions.newBuilder()
      .setInitialInterval(Duration.ofSeconds(15))
      .setBackoffCoefficient(2.0)
      .setMaximumInterval(Duration.ofSeconds(60))
      .setMaximumAttempts(25)
      .build();
```

4. In this `RetryOptions` builder, use the `.setDoNotRetry()` method to specify
   that the Activity should not retry on a `CreditCardProcessingException`.
   1. Hint: To get the FQDN of the class name, use `CreditCardProcessingException.class.getName()
5. Next, add the `retryOptions` object to the `ActivityOptions` object using `.setRetryOptions()`
6. Save your file.
7. Compile your file using `mvn clean compile`.
8. Verify that your Error is once again failing the Workflow
   1. In one terminal, start the Worker by running:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
      ```
   2. In another terminal, start the Workflow by executing `Starter.java`:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
      ```
   3. Go to the WebUI and view the status of the Workflow. You should see an `ActivityTaskFailed`
      error in event 18 with the message `Invalid credit card number`, and in event 22
      you should see a `WorkflowExecutionFailed` error with the message `Unable to process credit card`.
   4. Stop your Worker using **Cmd - C**

## Part C: Add Heartbeats

In this part of the exercise, you will add heartbeating to your `notifyDeliveryDriver`
Activity. The `notifyDeliveryDriver` method attempts to contact a driver
to deliver the customers pizza. It may take a while for a delivery driver to accept
the delivery, and you want to ensure that the Activity is still alive and processing.
Heartbeats are used to do this, and fail fast if a failure is detected.

In this exercise, instead of attempting to call out to an external service, success
of the `notifyDeliveryDriver` method call will be simulated.

**How the simulation works**: The simulation starts by generating a number from
0 - 14. From there a loop is iterated over from 0 < 10, each time checking to
see if the random number matches the loop counter and then sleeping for 5 seconds.
Each iteration of the loop sends a heartbeat back letting the Workflow know that
progress is still being made. If the number matches a loop counter, it is a success
and `true` is returned. If it doesn't, then a delivery driver was unable to be
contacted and false is returned and the `status` of the `OrderConfirmation` will
be updated to reflect this.


1. Open `PizzaActivitiesImpl.java`
2. Locate the `notifyDeliveryDriver` method.
3. Within the loop, after the success condition add a heartbeat, providing the
   iteration number as the details.
   ```java
      Activity.getExecutionContext().heartbeat("Heartbeat: " + x);
   ```
4. Save and close the file.
5. Open `PizzaWorkflowImpl.java`
6. Locate the call to the `notifyDeliveryDriver` Activity and following code
   and uncomment it. 
7. Delete the return statement at the end of the Workflow, as the uncommented
   code will now make this statement unreachable.
8. Save the file.
9. Update `Starter.java` file to have a valid test card number - "4242424242424242", otherwise the validation would still be failing.

## Part D: Add a Heartbeat Timeout

In the previous part of the exercise, you added a Heartbeat to an Activity. However,
you didn't set how long the Heartbeat should be inactive for before it is considered
a failed Heartbeat.

In this section, we will add a Heartbeat Timeout to your Activities.

1. Open `PizzaWorkflowImpl.java`.
2. In the `ActivityOptions` builder, set the HeartbeatTimeout to a duration of 10
   seconds using the `.setHeartbeatTimeout` method. This sets the maximum time allowed
   between Activity Heartbeats before the Heartbeat is considered failed.
3. Save and close the file.

## Part E: Run the Workflow

Now you will run the Workflow and witness the Heartbeats happening in the
Web UI.

1. Compile your code with `mvn clean compile`
2. In one terminal, start the Worker by running:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
   ```
3. In another terminal, start the Workflow by executing `Starter.java`:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
   ```
4. Now go to the WebUI and find your workflow, which should be in the `Running`
   state. Click on it to enter the details page. Once you see `Heartbeat: <A_NUMBER>` 
   in your Worker output refresh the page and look for a **Pending Activities** 
   section. In this section you should see **Heartbeat Details** and JSON representing
   the payload.
      1. Remember, the simulation will finish at a random interval. You may
         need to run this a few times to see the results.

You have now seen how heartbeats are implemented and appear when an Activity is
running.

## (Optional) Part F: Failing a Heartbeat

Now that you've seen what a successful Heartbeat looks like, you should experience
a Heartbeat that is timing out. 

1. Open `PizzaActivitiesImpl.java`.
2. In the `notifyDeliveryDriver` method, update the duration in the `sleep` statement
   from 5000ms to 15000ms. This is longer than the Heartbeat Timeout you set in Step D.
3. Kill the Worker from the previous exercise and recompile your code using `mvn clean compile`.
4. In one terminal, start the Worker by running:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
   ```
5. In another terminal, start the Workflow by executing `Starter.java`:
   ```bash
   mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
   ```
6. Once you see the first Heartbeat message appear in the logs, wait 15s and 
   refresh the WebUI. You should see the same **Pending Activities** section, but
   now there is a failure indicating that the Heartbeat timed out. You will
   also see how many retries are left and how long until the next retry. If the 
   Activity isn't fixed before the final attempt it will fail.

You have now seen what happens when a Heartbeat times out. 

### This is the end of the exercise.
