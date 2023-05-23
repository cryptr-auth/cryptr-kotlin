package cryptr.kotlin.interfaces

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import cryptr.kotlin.Cryptr
import io.github.oshai.KLogger
import io.github.oshai.KotlinLogging
import org.slf4j.LoggerFactory

/**
 * Interface to manage logging of Cryptr processes
 */
interface Loggable {
    /**
     * @suppress
     */
    fun logInfo(info: () -> Any?, logger: KLogger = KotlinLogging.logger {}) {
        if (!isJUnitTest()) {
            val currentLogger = currentLogger()
            if (currentLogger.isInfoEnabled) {
                logger.info(info().toString())
            } else {
                logger.warn("sorry Info level not active, current: ${currentLogger.level}")
            }
        }
    }

    /**
     * @suppress
     */
    fun logDebug(debug: () -> Any?, logger: KLogger = KotlinLogging.logger {}) {
        if (!isJUnitTest()) {
            val currentLogger = currentLogger()
            if (currentLogger.isInfoEnabled) {
                logger.debug(debug().toString())
            } else {
                logger.warn("Sorry Debug level is not active, current: ${currentLogger.level}")
            }
        }
    }

    /**
     * @suppress
     */
    fun logError(error: () -> Any?, logger: KLogger = KotlinLogging.logger {}) {
        if (!isJUnitTest()) {
            val currentLogger = currentLogger()
            if (currentLogger.isErrorEnabled) {
                logger.error(error().toString())
            } else {
                logger.warn("Sorry Debug level is not active, current: ${currentLogger.level}")
            }
        }
    }

    /**
     * @suppress
     */
    fun logException(exception: java.lang.Exception, logger: KLogger = KotlinLogging.logger {}) {
        if (!isJUnitTest()) {
            logger.error("an exception occured:\n$exception")
        }
    }

    /**
     * Define the log level for current Cryptr SDK instantiation
     *
     * @param logLevel [String] of desired log level (ex: `ERROR`, `DEBUG` ...)
     *
     */
    fun setLogLevel(logLevel: String) {
        val logger = currentLogger()
        logger.level = Level.toLevel(logLevel)
    }

    /**
     * @suppress
     */
    private fun currentLogger(): Logger {
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val packageName = Cryptr::javaClass.name
        return loggerContext.getLogger(packageName)
    }

    /**
     * @suppress
     */
    fun isJUnitTest(): Boolean {
        for (element in Thread.currentThread().stackTrace) {
            if (element.className.startsWith("org.junit.")) {
                return true
            }
        }
        return false
    }
}