package cryptr.kotlin.interfaces

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import cryptr.kotlin.Cryptr
import io.github.oshai.KLogger
import io.github.oshai.KotlinLogging
import org.slf4j.LoggerFactory

interface Loggable {
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

    fun logException(exception: java.lang.Exception, logger: KLogger = KotlinLogging.logger {}) {
        if (!isJUnitTest()) {
            logger.error("an exception occured:\n$exception")
        }
    }

    fun setLogLevel(logLevel: String) {
        val logger = currentLogger()
        logger.level = Level.toLevel(logLevel)
    }

    private fun currentLogger(): Logger {
        val loggerContext: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        val packageName = Cryptr::javaClass.name
        return loggerContext.getLogger(packageName)
    }

    fun isJUnitTest(): Boolean {
        for (element in Thread.currentThread().stackTrace) {
            if (element.className.startsWith("org.junit.")) {
                return true
            }
        }
        return false
    }
}