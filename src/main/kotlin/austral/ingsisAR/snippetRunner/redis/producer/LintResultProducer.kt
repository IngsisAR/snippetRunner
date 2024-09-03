package austral.ingsisAR.snippetRunner.redis.producer

import org.austral.ingsis.redis.RedisStreamProducer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintResultProducer
    @Autowired
    constructor(
        @Value("\${redis.streams.lintResult}")
        streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {
        private val logger: Logger = LoggerFactory.getLogger(LintResultProducer::class.java)

        suspend fun publishEvent(event: String) {
            logger.info("Publishing lint result for Snippet")
            emit(event)
        }
    }
