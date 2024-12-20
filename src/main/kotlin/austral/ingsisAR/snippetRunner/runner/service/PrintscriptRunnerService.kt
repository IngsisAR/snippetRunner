package austral.ingsisAR.snippetRunner.runner.service

import austral.ingsisAR.snippetRunner.runner.language.printscript.CustomEnvironmentProvider
import austral.ingsisAR.snippetRunner.runner.language.printscript.ListOutputProvider
import austral.ingsisAR.snippetRunner.runner.language.printscript.PrintscriptAdapter
import austral.ingsisAR.snippetRunner.runner.language.printscript.StringListInputProvider
import austral.ingsisAR.snippetRunner.runner.model.dto.request.Env
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

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
        PrintscriptAdapter().execute(
            snippetDTO.content,
            inputProvider,
            outputProvider,
            customEnvironmentProvider,
            System.getenv("DEFAULT_VERSION") ?: "1.1",
        )
        logger.info("Snippet executed successfully")
        return RunSnippetResponseDTO(outputs, listOf())
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
        logger.info("Formatting snippet for user $userId")

        val tempFilePath = Files.createTempFile("FormatterConfig_", ".json")

        val mapper =
            jacksonObjectMapper().apply {
                registerModule(kotlinModule())
            }
        val jsonString = mapper.writeValueAsString(snippetDTO.formatterRules)

        writeConfigFile(tempFilePath.toString(), jsonString)

        val result = PrintscriptAdapter().format(snippetDTO.content, tempFilePath.toString(), System.getenv("DEFAULT_VERSION") ?: "1.1")

        logger.info("Snippet formatted successfully")

        removeGeneratedConfigFile(tempFilePath.toString())

        return result
    }

    override fun lint(
        userId: String,
        snippetDTO: LintSnippetDTO,
    ): String {
        logger.info("Linting snippet ${snippetDTO.snippetId} for user $userId")

        val tempFilePath = Files.createTempFile("SCAConfig_", ".json")

        val mapper =
            jacksonObjectMapper().apply {
                registerModule(kotlinModule())
            }
        val jsonConfigs = mapper.writeValueAsString(snippetDTO.linterRules)

        writeConfigFile(tempFilePath.toString(), jsonConfigs)

        val result = PrintscriptAdapter().lint(snippetDTO.content, tempFilePath.toString(), snippetDTO.version)

        logger.info("Snippet ${snippetDTO.snippetId} linted successfully")

        Files.deleteIfExists(tempFilePath)

        return result
    }

    private fun writeConfigFile(
        configFile: String,
        jsonConfigs: String,
    ) {
        val path = Paths.get(configFile)
        Files.createDirectories(path.parent) // Ensure the directory exists
        Files.write(path, jsonConfigs.toByteArray())
    }

    private fun removeGeneratedConfigFile(configFile: String) {
        val file = File(configFile)
        if (file.exists()) {
            file.delete()
        }
    }
}
