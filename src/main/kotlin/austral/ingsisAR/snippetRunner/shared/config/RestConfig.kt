package austral.ingsisAR.snippetRunner.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class RestConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}
