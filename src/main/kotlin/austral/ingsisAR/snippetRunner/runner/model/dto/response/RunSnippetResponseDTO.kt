package austral.ingsisAR.snippetRunner.runner.model.dto.response

import java.io.Serializable

data class RunSnippetResponseDTO(
    val outputs: List<String>,
    val errors: List<String>,
) : Serializable
