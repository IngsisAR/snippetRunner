package austral.ingsisAHRE.snippetRunner.redis.producer

import austral.ingsisAHRE.snippetRunner.redis.event.LintRequestEvent
import org.austral.ingsis.redis.RedisStreamProducer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component

@Component
class LintRequestProducer
    @Autowired
    constructor(
        @Value("\${redis.streams.lintRequest}")
        private val streamName: String,
        redis: RedisTemplate<String, String>,
    ) : RedisStreamProducer(streamName, redis) {
        suspend fun publishEvent(event: LintRequestEvent) {
            println("Publishing lint result for Snippet(${event.snippetId}) for User(${event.userId})")
            emit(event)
        }
    }
