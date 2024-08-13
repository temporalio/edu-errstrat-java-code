package heartbeatsworkflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;
import io.temporal.failure.ActivityFailure;
import io.temporal.failure.ApplicationFailure;


import java.time.Duration;
import java.util.List;

import org.slf4j.Logger;

public class MyWorkflowImpl implements MyWorkflow {

  public static final Logger logger = Workflow.getLogger(MyWorkflowImpl.class);

  RetryOptions retryOptions = RetryOptions.newBuilder()
      .setInitialInterval(Duration.ofSeconds(15))
      .setBackoffCoefficient(2.0)
      .setMaximumInterval(Duration.ofSeconds(60))
      .setMaximumAttempts(25)
      .build();

  ActivityOptions options = ActivityOptions.newBuilder()
      .setStartToCloseTimeout(Duration.ofMinutes(5))
      .setRetryOptions(retryOptions)
      .setHeartbeatTimeout(Duration.ofSeconds(3))
      .build();

  private final MyActivities activities =
      Workflow.newActivityStub(MyActivities.class, options);

  @Override
  public String myWorkflow(String name) {
  
    logger.info("Workflow Invoked");
    
    String result = activities.myActivity(name);

    return "Workflow result: " + result;

  }
}
