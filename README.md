# Circus/Scheduler
Somewhat simplifies usage of timer related broadcasts.

Register a callback by calling `CircusScheduler.registerCallback(String target, CircusScheduler.Callback callback)`

Schedule a new task by calling `Context.startBroadcast` with `Intent(CircusScheduler.SCHEDULE_TASK)`.
Possible values for that intent:
 * `CircusScheduler.TARGET` - required. A string based value, which notes the callback that's supposed
  to be invoked.
 * `CircusScheduler.BEGIN_DELAY` - required. A long based value which notes the delay until next execution of callback.
 * `CircusScheduler.ID` - optional. An int based value, which allows you to either cancel, or restart a task.
 By default it's -1, which specifies that a random ID should be generated for it.
 * `CircusScheduler.ITERATION_DELAY` - optional. A long based value which notes that task is repeatable.
 Also notes the period between executions.

You can also provide `CircusScheduler.CANCEL_TASK` intent type to cancel a particular scheduled task.
The only necessary value is `CircusScheduler.ID`, which you provided scheduling your task earlier.