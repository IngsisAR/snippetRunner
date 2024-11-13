package austral.ingsisAR.snippetRunner.runner.controller

import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage
import austral.ingsisAR.snippetRunner.runner.model.dto.request.Env
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatterRulesDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO
import austral.ingsisAR.snippetRunner.runner.service.LanguageRunnerServiceSelector
import austral.ingsisAR.snippetRunner.runner.service.RunnerService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.jwt.Jwt

class RunnerControllerTest {
    private lateinit var languageRunnerServiceSelector: LanguageRunnerServiceSelector
    private lateinit var runnerService: RunnerService
    private lateinit var runnerController: RunnerController

    @BeforeEach
    fun setUp() {
        languageRunnerServiceSelector = mock(LanguageRunnerServiceSelector::class.java)
        runnerService = mock(RunnerService::class.java)
        runnerController = RunnerController(languageRunnerServiceSelector)
    }

    @Test
    fun `runSnippet should return RunSnippetResponseDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val runSnippetRequestDTO =
            RunSnippetRequestDTO(
                content = "println(\"test\");",
                language = "printscript",
                inputs = listOf("input1"),
                envs =
                    listOf(
                        Env().apply {
                            this::class.java.getDeclaredField("key").apply { isAccessible = true }.set(this, "key1")
                            this::class.java.getDeclaredField("value").apply { isAccessible = true }.set(this, "value1")
                        },
                    ),
            )

        val expectedResponse = RunSnippetResponseDTO(listOf("output"), listOf())
        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.runSnippet("testUser", runSnippetRequestDTO)).thenReturn(expectedResponse)

        val response = runnerController.runSnippet(jwt, runSnippetRequestDTO)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
    }

    @Test
    fun `runSnippet should handle exception`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val runSnippetRequestDTO =
            RunSnippetRequestDTO(
                content = "println(\"test\");",
                language = "printscript",
                inputs = listOf("input1"),
                envs =
                    listOf(
                        Env().apply {
                            this::class.java.getDeclaredField("key").apply { isAccessible = true }.set(this, "key1")
                            this::class.java.getDeclaredField("value").apply { isAccessible = true }.set(this, "value1")
                        },
                    ),
            )

        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.runSnippet("testUser", runSnippetRequestDTO)).thenThrow(RuntimeException("Test Exception"))

        val response = runnerController.runSnippet(jwt, runSnippetRequestDTO)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Test Exception", response.body?.errors?.first())
    }

    @Test
    fun `formatSnippet should return formatted string`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val formatSnippetRequestDTO =
            FormatSnippetRequestDTO(
                content = "println(\"test\");",
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

        val expectedResponse = "formatted code"
        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.format("testUser", formatSnippetRequestDTO)).thenReturn(expectedResponse)

        val response = runnerController.formatSnippet(jwt, formatSnippetRequestDTO)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
    }

    @Test
    fun `formatSnippet should handle exception`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val formatSnippetRequestDTO =
            FormatSnippetRequestDTO(
                content = "println(\"test\");",
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

        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.format("testUser", formatSnippetRequestDTO)).thenThrow(RuntimeException("Test Exception"))

        val response = runnerController.formatSnippet(jwt, formatSnippetRequestDTO)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Test Exception", response.body)
    }

    @Test
    fun `formatSnippet with no language selected`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val formatSnippetRequestDTO =
            FormatSnippetRequestDTO(
                content = "println(\"test\");",
                language = null,
                formatterRules =
                    FormatterRulesDTO(
                        spaceBeforeColon = 1,
                        spaceAfterColon = 1,
                        spacesInAssignSymbol = 1,
                        lineJumpBeforePrintln = 1,
                        identationInsideConditionals = 1,
                    ),
            )

        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.format("testUser", formatSnippetRequestDTO)).thenThrow(RuntimeException("Test Exception"))

        val response = runnerController.formatSnippet(jwt, formatSnippetRequestDTO)

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Test Exception", response.body)
    }

    @Test
    fun `runSnippet with no language selected should return RunSnippetResponseDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val runSnippetRequestDTO =
            RunSnippetRequestDTO(
                content = "println(\"test\");",
                language = null,
                inputs = listOf("input1"),
                envs =
                    listOf(
                        Env().apply {
                            this::class.java.getDeclaredField("key").apply { isAccessible = true }.set(this, "key1")
                            this::class.java.getDeclaredField("value").apply { isAccessible = true }.set(this, "value1")
                        },
                    ),
            )

        val expectedResponse = RunSnippetResponseDTO(listOf("output"), listOf())
        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.runSnippet("testUser", runSnippetRequestDTO)).thenReturn(expectedResponse)

        val response = runnerController.runSnippet(jwt, runSnippetRequestDTO)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
    }

    @Test
    fun `runSnippet with random language selected should return RunSnippetResponseDTO`() {
        val jwt = mock(Jwt::class.java)
        `when`(jwt.subject).thenReturn("testUser")

        val runSnippetRequestDTO =
            RunSnippetRequestDTO(
                content = "println(\"test\");",
                language = "randomLanguage",
                inputs = listOf("input1"),
                envs =
                    listOf(
                        Env().apply {
                            this::class.java.getDeclaredField("key").apply { isAccessible = true }.set(this, "key1")
                            this::class.java.getDeclaredField("value").apply { isAccessible = true }.set(this, "value1")
                        },
                    ),
            )

        val expectedResponse = RunSnippetResponseDTO(listOf("output"), listOf())
        `when`(languageRunnerServiceSelector.getRunnerService(SupportedLanguage.PRINTSCRIPT)).thenReturn(runnerService)
        `when`(runnerService.runSnippet("testUser", runSnippetRequestDTO)).thenReturn(expectedResponse)

        val response = runnerController.runSnippet(jwt, runSnippetRequestDTO)

        assertEquals(ResponseEntity.ok(expectedResponse), response)
    }
}
