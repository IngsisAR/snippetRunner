package austral.ingsisAHRE.snippetRunner.runner.language.printscript

import astbuilder.ASTBuilderFailure
import astbuilder.ASTBuilderResult
import astbuilder.ASTBuilderSuccess
import astbuilder.ASTProviderFactory
import interpreter.InterpreterImpl
import lexer.Lexer
import parser.Parser
import utils.ASTNode
import utils.PrintScriptChunkReader

class PrintscriptAdapter {
    private var chunkStartLine = 1
    companion object {
        private const val CHUNK_KEYWORDS_REGEX_PATH = "src/main/resources/static/ChunkKeywordsRegex.json"
    }

    fun execute(
        content: String,
        inputProvider: StringListInputProvider,
        outputProvider: ListOutputProvider,
        environmentProvider: CustomEnvironmentProvider,
        version: String,
    ) {
        var interpreter =
            InterpreterImpl(
                version = version,
                outputProvider = outputProvider,
                inputProvider = inputProvider,
                environmentProvider = environmentProvider,
            )
        val chunks = PrintScriptChunkReader(CHUNK_KEYWORDS_REGEX_PATH).readChunksFromString(content)
        for (chunk in chunks) {
            when (val ast = validateChunk(chunk, getTokenRegex(version), version)) {
                is ASTBuilderFailure -> {
                    if (ast.errorMessage == "Empty tokens") continue
                    outputProvider.print(ast.errorMessage)
                    return
                }
                is ASTBuilderSuccess -> {
                    val executeResult = executeASTNode(ast.astNode, interpreter, outputProvider) ?: return
                    interpreter = executeResult
                }
            }
        }
    }

    private fun validateChunk(
        chunk: String,
        regexPath: String,
        version: String,
    ): ASTBuilderResult {
        val lexer = Lexer(chunk, chunkStartLine, regexPath)
        val tokens =
            try {
                lexer.tokenize()
            } catch (e: Exception) {
                return ASTBuilderFailure(e.message!!)
            }
        chunkStartLine = lexer.getCurrentLineIndex() + 1
        val parser = Parser()
        return parser.parse(ASTProviderFactory(tokens, version))
    }

    private fun getTokenRegex(version: String): String {
        return "src/main/resources/static/tokenRegex$version.json"
    }

    private fun executeASTNode(
        ast: ASTNode,
        interpreter: InterpreterImpl,
        outputProvider: ListOutputProvider,
    ): InterpreterImpl? {
        try {
            return interpreter.interpret(ast)
        } catch (e: IndexOutOfBoundsException) {
            return null
        } catch (e: Exception) {
            outputProvider.print(e.message ?: "An error occurred")
            return null
        }
    }
}
