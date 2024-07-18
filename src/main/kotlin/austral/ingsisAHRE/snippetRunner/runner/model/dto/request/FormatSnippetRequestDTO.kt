package austral.ingsisAHRE.snippetRunner.runner.model.dto.request

import austral.ingsisAHRE.snippetRunner.runner.model.SupportedLanguage
import java.io.Serializable

data class FormatSnippetRequestDTO(
    val content: String,
    val language: SupportedLanguage?,
    val version: String? = null,
    val formatterRules: FormatterRulesDTO,
) : Serializable

data class FormatterRulesDTO(
    var spaceBeforeColon: Int?,
    var spaceAfterColon: Int?,
    var spacesInAssignSymbol: Int?,
    var lineJumpBeforePrintln: Int?,
    var identationInsideConditionals: Int?,
)
