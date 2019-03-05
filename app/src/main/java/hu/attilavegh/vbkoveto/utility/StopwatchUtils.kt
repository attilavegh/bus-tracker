package hu.attilavegh.vbkoveto.utility

import android.os.Handler
import android.os.Message
import android.widget.TextView
import com.google.common.base.Stopwatch
import java.util.concurrent.TimeUnit

const val MSG_START_TIMER = 0
const val MSG_STOP_TIMER = 1
const val MSG_UPDATE_TIMER = 2

const val REFRESH_RATE: Long = 100

class StopwatchUtils(textView: TextView) {

    private val stopwatchHandler = StopwatchHandler(textView)

    private class StopwatchHandler(private val textView: TextView): Handler() {
        private val timer = Stopwatch.createUnstarted()

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                MSG_START_TIMER -> {
                    timer.start()
                    this.sendEmptyMessage(MSG_UPDATE_TIMER)
                }

                MSG_UPDATE_TIMER -> {
                    textView.text = getElapsedTime()
                    this.sendEmptyMessageDelayed(MSG_UPDATE_TIMER, REFRESH_RATE)
                }

                MSG_STOP_TIMER -> {
                    this.removeMessages(MSG_UPDATE_TIMER)
                    timer.stop()
                }

                else -> {
                }
            }
        }

        private fun getElapsedTime(): String {
            val elapsedTime = timer.elapsed(TimeUnit.SECONDS)

            val minutes = (elapsedTime / 60).toString().padStart(2, '0')
            val seconds = (elapsedTime % 60).toString().padStart(2, '0')

            return "$minutes:$seconds"
        }
    }

    fun start() {
        stopwatchHandler.sendEmptyMessage(MSG_START_TIMER)
    }

    fun stop() {
        stopwatchHandler.sendEmptyMessage(MSG_STOP_TIMER)
    }
}