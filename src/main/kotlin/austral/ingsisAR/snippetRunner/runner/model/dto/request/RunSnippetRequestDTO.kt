package austral.ingsisAR.snippetRunner.runner.model.dto.request

import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage
import java.io.Serializable

data class RunSnippetRequestDTO(
    val content: String,
    val language: SupportedLanguage?,
    val version: String? = null,
    val inputs: List<String>,
    val envs: List<Env>,
) : Serializable

class Env {
    val key: String = ""
    val value: String = ""
}
