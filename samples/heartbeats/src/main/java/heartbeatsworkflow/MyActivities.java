package heartbeatsworkflow;

import io.temporal.activity.ActivityInterface;


@ActivityInterface
public interface MyActivities {

  String myActivity(String name);


}
