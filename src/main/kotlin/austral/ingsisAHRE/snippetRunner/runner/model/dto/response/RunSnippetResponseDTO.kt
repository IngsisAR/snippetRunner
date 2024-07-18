package austral.ingsisAHRE.snippetRunner.runner.model.dto.response

import java.io.Serializable

data class RunSnippetResponseDTO(
    val output: List<String>,
) : Serializable
