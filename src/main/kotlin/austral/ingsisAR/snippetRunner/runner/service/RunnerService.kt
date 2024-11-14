package austral.ingsisAR.snippetRunner.runner.service

import austral.ingsisAR.snippetRunner.runner.model.dto.request.FormatSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.LintSnippetDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.request.RunSnippetRequestDTO
import austral.ingsisAR.snippetRunner.runner.model.dto.response.RunSnippetResponseDTO

interface RunnerService {
    fun runSnippet(
        userId: String,
        snippetDTO: RunSnippetRequestDTO,
    ): RunSnippetResponseDTO

    fun format(
        userId: String,
        snippetDTO: FormatSnippetRequestDTO,
    ): String

    fun lint(
        userId: String,
        snippetDTO: LintSnippetDTO,
    ): String
}
