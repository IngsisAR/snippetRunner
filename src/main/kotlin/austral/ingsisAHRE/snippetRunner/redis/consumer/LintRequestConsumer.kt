package austral.ingsisAHRE.snippetRunner.redis.consumer

import austral.ingsisAHRE.snippetRunner.integration.AssetService
import austral.ingsisAHRE.snippetRunner.redis.event.LintRequestEvent
import austral.ingsisAHRE.snippetRunner.redis.event.LintResultEvent
import austral.ingsisAHRE.snippetRunner.redis.event.LintStatus
import austral.ingsisAHRE.snippetRunner.redis.producer.LintResultProducer
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAHRE.snippetRunner.runner.service.PrintscriptRunnerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.runBlocking
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
        @Value("\${redis.streams.lintRequest}")
        streamName: String,
        @Value("\${redis.groups.lint}")
        groupName: String,
        redis: RedisTemplate<String, String>,
        private val assetService: AssetService,
        private val printscriptRunnerService: PrintscriptRunnerService,
        private val producer: LintResultProducer,
        private val objectMapper: ObjectMapper,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis) {
        init {
            subscription()
        }
        private val logger: Logger = LoggerFactory.getLogger(LintRequestConsumer::class.java)

        override fun onMessage(record: ObjectRecord<String, String>) {
            val lintRequest: LintRequestEvent = objectMapper.readValue(record.value)
            logger.info("Consuming lint request for Snippet(${lintRequest.snippetId}) for User(${lintRequest.userId})")

            logger.info("Getting asset for Snippet(${lintRequest.snippetId})")
            val asset = assetService.getSnippet(lintRequest.snippetId)

            try {
                logger.info("Linting Snippet(${lintRequest.snippetId})")
                val result: String =
                    printscriptRunnerService.lint(
                        lintRequest.userId,
                        LintSnippetDTO(
                            content = asset.body!!,
                            snippetId = lintRequest.snippetId,
                            linterRules = lintRequest.linterRules,
                        ),
                    )

                if (result.isNotBlank()) logger.error("Linting Snippet(${lintRequest.snippetId}) failed - error: $result")
                val lintStatus: LintStatus = if (result.isBlank()) LintStatus.PASSED else LintStatus.FAILED

                runBlocking {
                    producer.publishEvent(
                        objectMapper.writeValueAsString(
                            LintResultEvent(
                                userId = lintRequest.userId,
                                snippetId = lintRequest.snippetId,
                                status = lintStatus,
                            ),
                        ),
                    )
                }
            } catch (e: Exception) {
                logger.error("Error linting Snippet(${lintRequest.snippetId})", e)
                runBlocking {
                    producer.publishEvent(
                        objectMapper.writeValueAsString(
                            LintResultEvent(
                                userId = lintRequest.userId,
                                snippetId = lintRequest.snippetId,
                                status = LintStatus.FAILED,
                            ),
                        ),
                    )
                }
            } finally {
                logger.info("Linting Snippet(${lintRequest.snippetId}) finished")
            }
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .pollTimeout(java.time.Duration.ofMillis(10000))
                .targetType(String::class.java)
                .build()
        }
    }
