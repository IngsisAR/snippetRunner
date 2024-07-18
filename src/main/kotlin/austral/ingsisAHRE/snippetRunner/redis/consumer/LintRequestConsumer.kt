package austral.ingsisAHRE.snippetRunner.redis.consumer

import austral.ingsisAHRE.snippetRunner.integration.AssetService
import austral.ingsisAHRE.snippetRunner.redis.event.LintRequestEvent
import austral.ingsisAHRE.snippetRunner.runner.service.PrintscriptRunnerService
import org.austral.ingsis.redis.RedisStreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.stream.ObjectRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamReceiver
import org.springframework.stereotype.Component

@Component
class LintRequestConsumer
    @Autowired
    constructor(
        @Value("\${redis.streams.lintResult}")
        private val streamName: String,
        @Value("\${redis.groups.lint}")
        private val groupName: String,
        redis: RedisTemplate<String, String>,
        private val assetService: AssetService,
        private val printscriptRunnerService: PrintscriptRunnerService,
    ) : RedisStreamConsumer<LintRequestEvent>(streamName, groupName, redis) {
        init {
            subscription()
        }
        private val logger: Logger = LoggerFactory.getLogger(LintRequestConsumer::class.java)

        override fun onMessage(record: ObjectRecord<String, LintRequestEvent>) {
            logger.info("Consuming lint result for Snippet(${record.value.snippetId}) for User(${record.value.userId})")

            val payload = record.value
            logger.info("Getting asset for Snippet(${payload.snippetId})")
            val asset = assetService.getSnippet(payload.snippetId)
            if (!asset.statusCode.is2xxSuccessful) {
                logger.error("Asset for Snippet(${payload.snippetId}) not found")
                return
            }

            try {
                logger.info("Linting Snippet(${payload.snippetId})")
                // val result = printscriptRunnerService.lint(asset.body!!, payload.snippetId)
            } catch (e: Exception) {
                logger.error("Error running printscript for Snippet(${payload.snippetId})", e)
            }
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, LintRequestEvent>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(java.time.Duration.ofMillis(10000))
                .targetType(LintRequestEvent::class.java)
                .build()
        }
    }
