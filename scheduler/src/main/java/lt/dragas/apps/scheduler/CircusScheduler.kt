package lt.dragas.apps.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.SparseArray
import lt.dragas.apps.scheduler.exception.DuplicateKeyException
import lt.dragas.apps.scheduler.exception.NoTargetException
import lt.dragas.apps.scheduler.exception.NonExistantTaskException
import java.lang.ref.WeakReference
import java.util.*


class CircusScheduler : BroadcastReceiver()
{
    private var id: Int = -1

    private var shouldCheckForTimer: Boolean = true

    private var target: String? = null

    private var beginDelay: Long = -1

    private var iterationDelay: Long = -1

    override fun onReceive(context: Context, intent: Intent)
    {
        when (intent.action)
        {
            SCHEDULE_TASK -> scheduleNewTask(intent)
            RESTART_TASK -> restartOldTask(intent)
            CANCEL_TASK -> cancelOldTask(intent)
            else -> Log.e(TAG, "Intent filter bypassed. Are you sure you want to forcibly call this?")
        }
    }

    private fun restartOldTask(intent: Intent)
    {
        scrapIntent(intent, false)
        if (!checkForId(id))
            throw NonExistantTaskException("Task for $id doesn't exist or has finished.")
        val task = tasks[id]
        task ?: return
        task.cancel()
        scheduleNewTask(task)
    }

    private fun scheduleNewTask(intent: Intent)
    {
        scrapIntent(intent)
        if (shouldCheckForTimer && checkForId(id))
        {
            throw DuplicateKeyException("Task for $id is already scheduled. Use $RESTART_TASK if you want to restart a task instead.")
        }
        intent.putExtra(ID, id)
        val task = TargetedTimerTask(target!!, intent)
        tasks.put(id, task)
        scheduleNewTask(task)
    }

    private fun scheduleNewTask(task: TimerTask)
    {
        if (iterationDelay == -1L)
        {
            timer.schedule(task, beginDelay)
        }
        else
        {
            timer.schedule(task, beginDelay, iterationDelay)
        }
    }

    private fun cancelOldTask(intent: Intent)
    {
        val id = intent.getIntExtra(ID, -1)
        if (!checkForId(id))
            throw NonExistantTaskException("Task for $id doesn't exist or has already finished.")
        val timer = tasks[id]
        timer?.cancel() // there's no need to check if task is finished as subsequent calls have no affect
        tasks.delete(id) // there's also no need to check for this call as sparse array first checks for its existence
    }

    private fun scrapIntent(intent: Intent, requiresTarget: Boolean = true)
    {
        target = intent.getStringExtra(TARGET)
        if (requiresTarget && target == null)
            throw NoTargetException("$TARGET was not provided. Are you sure this task should be scheduled")
        id = intent.getIntExtra(ID, -1)

        if (id < 0)
        {
            val random = Random()
            do
            {
                id = Math.abs(random.nextInt())
            }
            while (tasks.indexOfKey(id) > -1) // Possible ANR
            shouldCheckForTimer = false
        }
        beginDelay = intent.getLongExtra(BEGIN_DELAY, -1)
        iterationDelay = intent.getLongExtra(ITERATION_DELAY, -1)
    }

    interface Callback
    {
        fun run(intent: Intent)
    }

    internal class TargetedTimerTask(private val target: String, private val intent: Intent) : TimerTask()
    {
        override fun run()
        {
            contextCallbacks[target]?.get()?.run(Intent(intent))
            callbacks[target]?.run(Intent(intent))
            if (intent.getLongExtra(ITERATION_DELAY, -1L) == -1L)
                tasks.delete(intent.getIntExtra(ID, -1))
        }
    }

    companion object
    {
        @JvmStatic
        private val TAG = CircusScheduler::class.java.canonicalName

        @JvmStatic
        private val contextCallbacks = HashMap<String, WeakReference<CircusScheduler.Callback>>()

        @JvmStatic
        private val callbacks = HashMap<String, CircusScheduler.Callback>()

        @JvmStatic
        private val tasks = SparseArray<TargetedTimerTask>()

        @JvmStatic
        private val timer = Timer()

        @JvmStatic
        val SCHEDULE_TASK = "$TAG.SCHEDULE_TASK"

        @JvmStatic
        val CANCEL_TASK = "$TAG.CANCEL_TASK"

        @JvmStatic
        val RESTART_TASK = "$TAG.RESTART_TASK"

        @JvmStatic
        val BEGIN_DELAY = "$TAG.BEGIN_DELAY"

        @JvmStatic
        val ITERATION_DELAY = "$TAG.ITERATION_DELAY"

        @JvmStatic
        val TARGET = "$TAG.TARGET"

        @JvmStatic
        val ID = "$TAG.ID"

        @JvmStatic
        fun registerReceiver(target: String, callback: CircusScheduler.Callback)
        {
            checkForTarget(target)
            if (callback is Context)
                contextCallbacks.put(target, WeakReference(callback))
            else
                callbacks.put(target, callback)
        }

        @JvmStatic
        fun unregisterReceiver(target: String)
        {
            if (contextCallbacks.containsKey(target))
                contextCallbacks.remove(target)
            else if (callbacks.containsKey(target))
                callbacks.remove(target)

        }

        @JvmStatic
        fun checkForId(id: Int): Boolean
        {
            return tasks.indexOfKey(id) > 0
        }

        @JvmStatic
        private fun checkForTarget(target: String)
        {
            if (callbacks.containsKey(target) || contextCallbacks.containsKey(target))
                throw DuplicateKeyException("$target is already being listened to. Are you sure you need several listeners for particular task?")
        }
    }
}




