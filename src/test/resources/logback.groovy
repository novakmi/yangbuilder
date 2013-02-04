//This is free software licensed under MIT License, see LICENSE file
//(https://bitbucket.org/novakmi/yangbuilder/src/LICENSE)

import ch.qos.logback.classic.boolex.JaninoEventEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import ch.qos.logback.core.filter.EvaluatorFilter
import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.TRACE
import static ch.qos.logback.core.spi.FilterReply.*

appender("FILE_LOG", FileAppender) {
        append = false
        file = "test.log"
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(LevelFilter) {
                level = ERROR
                onMatch = DENY
                onMismatch = NEUTRAL
        }
        filter(EvaluatorFilter) {
                evaluator(JaninoEventEvaluator) {
                        expression = 'logger.contains(".test.")'
                }
                onMatch = DENY
                onMismatch = ACCEPT
        }
}

appender("FILE_TEST", FileAppender) {
        append = false
        file = 'test.testng'
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(EvaluatorFilter) {
                evaluator(JaninoEventEvaluator) {
                        expression = 'logger.contains(".test.")'
                }
                onMatch = ACCEPT
                onMismatch = DENY
        }
}

appender("FILE_ERR", FileAppender) {
        append = false
        file = 'test.err'
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
        filter(LevelFilter) {
                level = ERROR
                onMatch = ACCEPT
                onMismatch = DENY
        }
}

appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
                pattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
        }
}

root(TRACE, ["STDOUT", "FILE_LOG", "FILE_ERR", "FILE_TEST"])