package austral.ingsisAR.snippetRunner.runner.model

data class FileType(val language: String, val extension: String)

enum class SupportedLanguage(val fileType: FileType) {
    PRINTSCRIPT(FileType("printscript", "prs")),
}
