package austral.ingsisAR.snippetRunner.redis.consumer

import austral.ingsisAR.snippetRunner.integration.AssetService
import austral.ingsisAR.snippetRunner.redis.event.LintRequestEvent
import austral.ingsisAR.snippetRunner.redis.event.LintResultEvent
import austral.ingsisAR.snippetRunner.redis.event.LintStatus
import austral.ingsisAR.snippetRunner.redis.producer.LintResultProducer
import austral.ingsisAR.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAR.snippetRunner.runner.service.PrintscriptRunnerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        private val groupName: String,
        redis: RedisTemplate<String, String>,
        private val assetService: AssetService,
        private val printscriptRunnerService: PrintscriptRunnerService,
        private val producer: LintResultProducer,
        private val objectMapper: ObjectMapper,
    ) : RedisStreamConsumer<String>(streamName, groupName, redis), CoroutineScope {
        private val logger: Logger = LoggerFactory.getLogger(LintRequestConsumer::class.java)

        // Create a job to manage the scope
        private val job = Job()

        // Coroutine context using Dispatchers.IO for I/O-bound work
        override val coroutineContext = Dispatchers.IO + job

        // Cleanup when the component is destroyed
        @PreDestroy
        fun cleanup() {
            job.cancel()
        }

        override fun onMessage(record: ObjectRecord<String, String>) {
            // Launch a coroutine for asynchronous processing
            launch {
                val lintRequest: LintRequestEvent = objectMapper.readValue(record.value)
                logger.info("Consuming lint request for Snippet(${lintRequest.snippetId}) for User(${lintRequest.userId})")

                try {
                    // Fetch the asset in the context of I/O operations
                    logger.info("Getting asset for Snippet(${lintRequest.snippetId})")
                    val asset =
                        withContext(Dispatchers.IO) {
                            assetService.getSnippet(lintRequest.snippetId)
                        }

                    // Perform linting asynchronously
                    val result: String =
                        withContext(Dispatchers.Default) {
                            printscriptRunnerService.lint(
                                lintRequest.userId,
                                LintSnippetDTO(
                                    content = asset.body!!,
                                    snippetId = lintRequest.snippetId,
                                    linterRules = lintRequest.linterRules,
                                ),
                            )
                        }

                    // Check linting result
                    val lintStatus: LintStatus = if (result.isBlank()) LintStatus.PASSED else LintStatus.FAILED
                    logger.info("Linting Snippet(${lintRequest.snippetId}) result: $lintStatus\n$result")

                    // Try publishing lint result with retry mechanism
                    repeat(3) {
                        try {
                            producer.publishEvent(
                                objectMapper.writeValueAsString(
                                    LintResultEvent(
                                        userId = lintRequest.userId,
                                        snippetId = lintRequest.snippetId,
                                        status = lintStatus,
                                    ),
                                ),
                            )
                            return@launch // Exit on successful publish
                        } catch (e: Exception) {
                            logger.error("Error publishing lint result for Snippet(${lintRequest.snippetId})", e)
                        }
                    }
                } catch (e: Exception) {
                    logger.error("Error consuming lint request for Snippet(${lintRequest.snippetId})", e)
                }
            }
        }

        override fun options(): StreamReceiver.StreamReceiverOptions<String, ObjectRecord<String, String>> {
            return StreamReceiver.StreamReceiverOptions.builder()
                .batchSize(5)
                .pollTimeout(java.time.Duration.ofMillis(10000))
                .targetType(String::class.java)
                .build()
        }
    }
