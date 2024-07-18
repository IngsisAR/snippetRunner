package austral.ingsisAHRE.snippetRunner.runner.model.dto.request

import austral.ingsisAHRE.snippetRunner.redis.event.LinterRulesDTO
import austral.ingsisAHRE.snippetRunner.runner.model.SupportedLanguage

data class LintSnippetDTO(
    val content: String,
    val language: SupportedLanguage? = null,
    val version: String? = null,
    val snippetId: String,
    val linterRules: LinterRulesDTO,
)
