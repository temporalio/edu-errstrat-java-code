package heartbeatsworkflow;

import java.lang.Thread;
import java.time.Duration;
import io.temporal.activity.Activity;

public class MyActivitiesImpl implements MyActivities {

  @Override
  public String myActivity(String name) {
    for(int x = 0; x < 10; x++){
      Activity.getExecutionContext().heartbeat(x);
      try {
        Thread.sleep(Duration.ofSeconds(1));
      } catch (InterruptedException e) {
        continue;
      }
    }

    return "Hello " + name;
  }
}
