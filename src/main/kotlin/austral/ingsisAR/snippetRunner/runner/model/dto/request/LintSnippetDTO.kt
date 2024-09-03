package austral.ingsisAR.snippetRunner.runner.model.dto.request

import austral.ingsisAR.snippetRunner.redis.event.LinterRulesDTO
import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage

data class LintSnippetDTO(
    val content: String,
    val language: SupportedLanguage? = null,
    val version: String? = null,
    val snippetId: String,
    val linterRules: LinterRulesDTO,
)
