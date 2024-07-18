package austral.ingsisAHRE.snippetRunner.redis.producer

import austral.ingsisAHRE.snippetRunner.redis.event.LintResultEvent
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
        @Value("\${redis.streams.lintRequest}")
        private val streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {
        private val logger: Logger = LoggerFactory.getLogger(LintResultProducer::class.java)

        suspend fun publishEvent(event: LintResultEvent) {
            logger.info("Publishing lint result for Snippet(${event.snippetId}) for User(${event.userId})")
            emit(event)
        }
    }
