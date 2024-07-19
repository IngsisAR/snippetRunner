package austral.ingsisAHRE.snippetRunner.runner.service

import austral.ingsisAHRE.snippetRunner.redis.event.LinterRulesDTO
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.CustomEnvironmentProvider
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.ListOutputProvider
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.PrintscriptAdapter
import austral.ingsisAHRE.snippetRunner.runner.language.printscript.StringListInputProvider
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.Env
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.FormatterRulesDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO
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
            return RunSnippetResponseDTO(outputs, listOf())
        }
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
        val configFile = "resources/static/FormatterConfig.json"
        generateFormatterConfig(configFile, snippetDTO.formatterRules)
        val result = PrintscriptAdapter().format(snippetDTO.content, configFile, snippetDTO.version)
        logger.info("Snippet formatted successfully")
        removeGeneratedConfigFile(configFile)
        return result
    }

    override fun lint(
        userId: String,
        snippetDTO: LintSnippetDTO,
    ): String {
        logger.info("Linting snippet for user $userId")
        val configFile = "resources/static/SCAConfig.json"
        generateLinterConfig(configFile, snippetDTO.linterRules)
        val result = PrintscriptAdapter().lint(snippetDTO.content, configFile, snippetDTO.version)
        logger.info("Snippet linted successfully")
        removeGeneratedConfigFile(configFile)
        return result
    }

    private fun removeGeneratedConfigFile(configFile: String) {
        val file = File(configFile)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun generateFormatterConfig(
        configFile: String,
        formatterRules: FormatterRulesDTO,
    ) {
        val mapper =
            jacksonObjectMapper().apply {
                registerModule(kotlinModule())
            }
        val jsonString = mapper.writeValueAsString(formatterRules)
        val path = Paths.get(configFile)
        Files.createDirectories(path.parent) // Ensure the directory exists
        Files.write(path, jsonString.toByteArray())
    }

    private fun generateLinterConfig(
        configFile: String,
        linterRules: LinterRulesDTO,
    ) {
        val mapper =
            jacksonObjectMapper().apply {
                registerModule(kotlinModule())
            }
        val jsonString = mapper.writeValueAsString(linterRules)
        val path = Paths.get(configFile)
        Files.createDirectories(path.parent) // Ensure the directory exists
        Files.write(path, jsonString.toByteArray())
    }
}
