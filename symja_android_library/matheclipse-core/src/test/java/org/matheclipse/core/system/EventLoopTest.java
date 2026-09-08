package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.tasks.EventLoop;

/**
 * Scheduled tasks and the pause that runs them.
 *
 * <p>
 * <code>Pause</code> is the point where a Wolfram Language program yields, so it is where a task
 * that has come due is evaluated. Without that, a main loop written as
 * <code>While[True, Pause[0.01]]</code> - which is how the WLJS notebook's server is written - would
 * spin forever and never do any of its work.
 */
public class EventLoopTest extends ExprEvaluatorTestCase {

  @AfterEach
  public void clearTasks() {
    EventLoop.INSTANCE.clear();
  }

  @Test
  public void testPauseRunsATaskThatIsDue() {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = true;
    try {
      check("counter = 0", //
          "0");
      check("task = SessionSubmit(ScheduledTask(counter = counter + 1, 0.01)); Head(task)", //
          "TaskObject");
      // nothing has run yet: no evaluation has waited
      check("counter", //
          "0");
      check("Pause(0.2); counter > 0", //
          "True");
      check("Head(TaskRemove(task))", //
          "TaskObject");
      check("before = counter; Pause(0.1); counter == before", //
          "True");
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }

  @Test
  public void testATaskWithACountRunsThatOften() {
    check("counter = 0", //
        "0");
    check("task = SessionSubmit(ScheduledTask(counter = counter + 1, {0.01, 3}));"
        + "Pause(0.3); counter", //
        "3");
    check("Tasks()", //
        "{}");
  }

  @Test
  public void testTaskExecuteRunsItNow() {
    check("counter = 0", //
        "0");
    check("task = SessionSubmit(ScheduledTask(counter = counter + 1, 100));" //
        + "TaskExecute(task); counter", //
        "1");
    check("TaskRemove(task); Tasks()", //
        "{}");
  }

  @Test
  public void testPauseAcceptsFractionsOfASecond() {
    // Pause used to take an integer, so Pause[0.01] - the WLJS main loop - was a no-op
    check("Pause(0.01)", //
        "");
    check("Pause(0)", //
        "");
  }
}
