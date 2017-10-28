package net.numa08.kotlinbook.chapter2.view

import android.databinding.BindingAdapter
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.content.Context
import android.util.AttributeSet

class TimerTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): TextView(context, attrs, defStyleAttr) { // (1)

    companion object {
        @JvmStatic
        @BindingAdapter("timer_delay")
        fun TimerTextView.setDelay(delay: Long) {
            this.delay = delay
        }
    }

    interface TimerTask {
        fun onExecute(textView: TimerTextView): CharSequence?
    }

    var timerTask: TimerTask? = null
        set(value) {
            field = value
            startTask()
        }
    var delay: Long? = null
        set(value) {
            field = value
            startTask()
        }

    private val taskHandler = Handler(Looper.getMainLooper())

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startTask()
    }

    override fun onDetachedFromWindow() {
        taskHandler.removeCallbacksAndMessages(null)
        super.onDetachedFromWindow()
    }

    private fun startTask() {
        taskHandler.removeCallbacksAndMessages(null)
        val (task, delay) = (timerTask to delay)
        if (task != null && delay != null) {
            taskHandler.post(object : Runnable {
                override fun run() {
                    text = task.onExecute(this@TimerTextView)
                    taskHandler.postDelayed(this, delay)
                }
            })
        }

    }
}

@BindingAdapter("timer_delay")
fun TimerTextView.setDelay(delay: Long) {
    this.delay = delay
}

@BindingAdapter("timer_task")
fun TimerTextView.setTimerTask(task: TimerTextView.TimerTask?) {
    this.timerTask = task
}
