package cryptr.kotlin

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.filter.AbstractMatcherFilter
import ch.qos.logback.core.spi.FilterReply

/**
 * @suppress
 */
class LogFilter : AbstractMatcherFilter<ILoggingEvent?>() {
    var loggerName: String? = null

    fun setClassName(className: String?) {
        loggerName = className
    }

    override fun start() {
        if (loggerName != null) {
            super.start()
        }
    }

    override fun decide(event: ILoggingEvent?): FilterReply {
        if (!isStarted) {
            return FilterReply.NEUTRAL
        }
        return if (event !== null && event.loggerName == loggerName) {
            onMatch
        } else {
            onMismatch
        }
    }
}