package austral.ingsisAR.snippetRunner.runner.model.dto.request

import java.io.Serializable

data class RunSnippetRequestDTO(
    val content: String,
    val language: String?,
    val inputs: List<String>,
    val envs: List<Env>,
) : Serializable

class Env {
    val key: String = ""
    val value: String = ""
}
