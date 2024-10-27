package austral.ingsisAR.snippetRunner.runner.controller

import austral.ingsisAR.snippetRunner.runner.model.FileType
import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage
import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO
import austral.ingsisAR.snippetRunner.runner.service.LanguageRunnerServiceSelector
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/runner")
@Validated
class RunnerController(
    private val languageRunnerServiceSelector: LanguageRunnerServiceSelector,
) {
    @PostMapping("/run")
    fun runSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody snippetDTO: RunSnippetRequestDTO,
    ): ResponseEntity<RunSnippetResponseDTO> {
        try {
            val runnerService =
                languageRunnerServiceSelector.getRunnerService(
                    snippetDTO.language?.let { SupportedLanguage.entries.find { language -> language.fileType.language == it } }
                        ?: enumValueOf(System.getenv("DEFAULT_LANGUAGE") ?: "PRINTSCRIPT"),
                )
            return ResponseEntity.ok(runnerService.runSnippet(jwt.subject, snippetDTO))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(RunSnippetResponseDTO(listOf(), listOf(e.message ?: "An error occurred")))
        }
    }

    @PostMapping("/format")
    fun formatSnippet(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody snippetDTO: FormatSnippetRequestDTO,
    ): ResponseEntity<String> {
        val runnerService =
            languageRunnerServiceSelector.getRunnerService(
                snippetDTO.language?.let { SupportedLanguage.entries.find { language -> language.fileType.language == it } }
                    ?: enumValueOf(System.getenv("DEFAULT_LANGUAGE") ?: "PRINTSCRIPT"),
            )
        try {
            val result = runnerService.format(jwt.subject, snippetDTO)
            return ResponseEntity.ok(result)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
    }

    @GetMapping("/languages")
    fun getLanguages(): ResponseEntity<List<FileType>> {
        return ResponseEntity.ok(SupportedLanguage.entries.map { it.fileType })
    }
}
