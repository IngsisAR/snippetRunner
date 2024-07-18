package austral.ingsisAHRE.snippetRunner.runner.service

import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAHRE.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO

interface RunnerService {
    fun runSnippet(
        userId: String,
        snippetDTO: RunSnippetRequestDTO,
    ): RunSnippetResponseDTO

    fun format(
        userId: String,
        snippetDTO: FormatSnippetRequestDTO,
    ): String
//    fun lint(snippetContent: String, languageVersion: String): List<String>
}
