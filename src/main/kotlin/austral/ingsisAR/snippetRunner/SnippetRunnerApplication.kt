package austral.ingsisAR.snippetRunner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SnippetRunnerApplication

fun main(args: Array<String>) {
    runApplication<SnippetRunnerApplication>(*args)
}
