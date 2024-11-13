package austral.ingsisAHRE.snippetRunner

import austral.ingsisAR.snippetRunner.redis.event.LinterRulesDTO
import austral.ingsisAR.snippetRunner.runner.language.printscript.PrintscriptAdapter
import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatterRulesDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.service.PrintscriptRunnerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.lenient
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PrintscriptRunnerServiceTest {
    @InjectMocks
    private lateinit var printscriptRunnerService: PrintscriptRunnerService

    @Mock
    private lateinit var printscriptAdapter: PrintscriptAdapter

    @Test
    fun runSnippetTest() {
        val userId = "testUser"
        val snippetDTO =
            RunSnippetRequestDTO(
                content = "println('Hello World!');",
                language = "printscript",
                inputs = listOf(),
                envs = listOf(),
            )

        val expectedOutput = listOf("Hello World!")

        val response = printscriptRunnerService.runSnippet(userId, snippetDTO)

        assertEquals(expectedOutput, response.outputs)
    }

    @Test
    fun formatSnippetTest() {
        val userId = "testUser"
        val snippetDTO =
            FormatSnippetRequestDTO(
                content = "a=2;",
                language = "printscript",
                formatterRules =
                    FormatterRulesDTO(
                        spaceBeforeColon = 1,
                        spaceAfterColon = 1,
                        spacesInAssignSymbol = 1,
                        lineJumpBeforePrintln = 1,
                        identationInsideConditionals = 1,
                    ),
            )

        val configFile = "resources/static/FormatterConfig.json"
        val expectedFormattedContent = "a = 2;\n"

        lenient().`when`(printscriptAdapter.format(snippetDTO.content, configFile, null))
            .thenReturn(expectedFormattedContent)

        val formattedContent = printscriptRunnerService.format(userId, snippetDTO)

        assertEquals(expectedFormattedContent, formattedContent)
    }

    @Test
    fun lintSnippetTestWithRules() {
        val userId = "testUser"
        val linterRulesDTO =
            LinterRulesDTO(
                printlnNoExpressionArguments = true,
                identifierCasing = "camel case",
                readInputNoExpressionArguments = true,
            )
        val snippetDTO =
            LintSnippetDTO(
                content =
                    """
                    println("Hello World");
                    """.trimIndent(),
                language = SupportedLanguage.PRINTSCRIPT,
                linterRules = linterRulesDTO,
                snippetId = "1",
            )

        val configFile = "resources/static/SCAConfig.json"
        val expectedLintResult = ""

        lenient().`when`(printscriptAdapter.lint(snippetDTO.content, configFile, snippetDTO.version))
            .thenReturn(expectedLintResult)

        val lintResult = printscriptRunnerService.lint(userId, snippetDTO)

        assertEquals(expectedLintResult, lintResult)
    }

    @Test
    fun runSnippetHandlesException() {
        val userId = "testUser"
        val snippetDTO =
            RunSnippetRequestDTO(
                content = "print('Hello World!');",
                language = "printscript",
                inputs = listOf(),
                envs = listOf(),
            )

        val printscriptRunnerService = PrintscriptRunnerService()
        val response = printscriptRunnerService.runSnippet(userId, snippetDTO)
        val expected = listOf("Unsupported function 'print' at (1:0)")

        assertEquals(expected, response.outputs)
    }

    @Test
    fun `test execute method with empty inputs returns IndexOutOfBoundsException`() {
        val userId = "testUser"
        val snippetDTO =
            RunSnippetRequestDTO(
                content = "println('Hello World!');\n readInput('Hello');",
                language = "printscript",
                inputs = listOf(),
                envs = listOf(),
            )
        val printscriptRunnerService = PrintscriptRunnerService()

        val response = printscriptRunnerService.runSnippet(userId, snippetDTO)

        assertEquals(listOf("Hello World!", "Hello"), response.outputs)
    }

    @Test
    fun `test execute method with empty inputs and empty version returns IndexOutOfBoundsException`() {
        val userId = "testUser"
        val snippetDTO =
            RunSnippetRequestDTO(
                content = "println('Hello World!');\n readInput('Hello');",
                language = "printscript",
                inputs = listOf(),
                envs = listOf(),
            )
        val printscriptRunnerService = PrintscriptRunnerService()

        val response = printscriptRunnerService.runSnippet(userId, snippetDTO)

        assertEquals(listOf("Hello World!", "Hello"), response.outputs)
    }
}
