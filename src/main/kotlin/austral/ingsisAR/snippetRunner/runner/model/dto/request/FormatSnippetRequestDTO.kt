package austral.ingsisAR.snippetRunner.runner.model.dto.request

import java.io.Serializable

data class FormatSnippetRequestDTO(
    val content: String,
    val language: String?,
    val formatterRules: FormatterRulesDTO,
) : Serializable

data class FormatterRulesDTO(
    var spaceBeforeColon: Int?,
    var spaceAfterColon: Int?,
    var spacesInAssignSymbol: Int?,
    var lineJumpBeforePrintln: Int?,
    var identationInsideConditionals: Int?,
)
