package austral.ingsisAHRE.snippetRunner.runner.service

import austral.ingsisAHRE.snippetRunner.runner.language.printscript.CustomEnvironmentProvider
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.ListOutputProvider
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.PrintscriptAdapter
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.StringListInputProvider
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.Env
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class PrintscriptRunnerService : RunnerService {
    private val logger = LoggerFactory.getLogger(PrintscriptRunnerService::class.java)

    override fun runSnippet(
        userId: String,
        snippetDTO: RunSnippetRequestDTO,
    ): RunSnippetResponseDTO {
        logger.info("Running snippet for user $userId")
        val inputProvider = StringListInputProvider(snippetDTO.inputs.toMutableList())
        val outputs = mutableListOf<String>()
        val outputProvider = ListOutputProvider(outputs)
        val customEnvironmentProvider = CustomEnvironmentProvider(parseEnvironmentVariables(snippetDTO.envs))
        try {
            PrintscriptAdapter().execute(
                snippetDTO.content,
                inputProvider,
                outputProvider,
                customEnvironmentProvider,
                snippetDTO.version ?: System.getenv("DEFAULT_VERSION") ?: "1.1.0",
            )
        } catch (e: IndexOutOfBoundsException) {
            logger.info("Snippet executed successfully")
            return RunSnippetResponseDTO(outputs)
        }
        logger.info("Snippet executed successfully")
        return RunSnippetResponseDTO(outputs)
    }

    private fun parseEnvironmentVariables(envs: List<Env>): HashMap<String, String> {
        val map = HashMap<String, String>()
        envs.forEach { map[it.key] = it.value }
        return map
    }

    override fun format(
        userId: String,
        snippetDTO: FormatSnippetRequestDTO,
    ): String {
        TODO("Not yet implemented")
    }
}
