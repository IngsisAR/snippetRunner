package austral.ingsisAR.snippetRunner.integration

import austral.ingsisAR.snippetRunner.shared.log.CorrelationIdFilter.Companion.CORRELATION_ID_KEY
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class AssetService
    @Autowired
    constructor(
        val rest: RestTemplate,
        @Value("\${bucket.url}")
        val bucketUrl: String,
    ) {
        fun getSnippet(snippetId: String): ResponseEntity<String> {
            try {
                val request = HttpEntity<String>(getHeaders())
                return rest.exchange("$bucketUrl/$snippetId", HttpMethod.GET, request, String::class.java)
            } catch (e: Exception) {
                return ResponseEntity.badRequest().build()
            }
        }

        private fun getHeaders(): HttpHeaders {
            return HttpHeaders().apply {
                set("X-Correlation-Id", MDC.get(CORRELATION_ID_KEY))
            }
        }
    }
