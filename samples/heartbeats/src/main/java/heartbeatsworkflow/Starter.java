package heartbeatsworkflow;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;

import java.util.Arrays;
import java.util.List;

public class Starter {
  public static void main(String[] args) throws Exception {

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();

    WorkflowClient client = WorkflowClient.newInstance(service);

    WorkflowOptions options = WorkflowOptions.newBuilder()
        .setWorkflowId("my-workflow")
        .setTaskQueue(Constants.TASK_QUEUE_NAME)
        .build();

    MyWorkflow workflow = client.newWorkflowStub(MyWorkflow.class, options);

    String result = workflow.myWorkflow("Mason");

    System.out.printf("Workflow result: %s\n", result);
    System.exit(0);
  }

}
