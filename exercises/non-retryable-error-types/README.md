## Exercise #2: Modifying Activity Options Using Non-Retryable Error Types

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
   or, if you're in the GitPod environment:
   ```bash
   ex2
   ```
2. In one terminal, run `mvn clean compile` to install packages.

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
   2. Now, when these errors are thrown from an Activity, the Activity will not be retried.
3. Save your file.
4. Compile your file using `mvn clean compile`.
5. Verify that your Error is now being retried by attempting to execute the Workflow
   1. In one terminal, start the Worker by running:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.PizzaWorker"
      ```
      or, if you're in the GitPod environment, run:
      ```bash
      ex2w
      ```
   2. In another terminal, start the Workflow by executing `Starter.java`:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
      ```
      or, if you're in the GitPod environment, run:
      ```bash
      ex2st
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
      or, if you're in the GitPod environment, run:
      ```bash
      ex2w
      ```
   2. In another terminal, start the Workflow by executing `Starter.java`:
      ```bash
      mvn exec:java -Dexec.mainClass="pizzaworkflow.Starter"
      ```
      or, if you're in the GitPod environment, run:
      ```bash
      ex2st
      ```
   3. Go to the WebUI and view the status of the Workflow. You should see an `ActivityTaskFailed`
   error in event 18 with the message `Invalid credit card number`, and in event 22
   you should see a `WorkflowExecutionFailed` error with the message `Unable to process credit card`.
   4. Stop your Worker using **Cmd - C**

### This is the end of the exercise.
