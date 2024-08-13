package heartbeatsworkflow;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface MyWorkflow {

  @WorkflowMethod
  String myWorkflow(String name);

}
