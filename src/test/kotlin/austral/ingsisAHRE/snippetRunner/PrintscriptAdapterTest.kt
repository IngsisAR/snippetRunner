package austral.ingsisAHRE.snippetRunner

import austral.ingsisAR.snippetRunner.runner.language.printscript.CustomEnvironmentProvider
import austral.ingsisAR.snippetRunner.runner.language.printscript.ListOutputProvider
import austral.ingsisAR.snippetRunner.runner.language.printscript.PrintscriptAdapter
import austral.ingsisAR.snippetRunner.runner.language.printscript.StringListInputProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import kotlin.test.assertNotEquals

class PrintscriptAdapterTest {
    val scaConfigs = "src/main/resources/static/SCAConfig.json"
    val formaterConfigs = "src/main/resources/static/FormatterConfig.json"

    @InjectMocks
    private val printscriptAdapter = PrintscriptAdapter()

    @Test
    fun `test execute method`() {
        val content = "println('Hello World');"
        val inputProvider = mock(StringListInputProvider::class.java)
        val outputProvider = mock(ListOutputProvider::class.java)
        val environmentProvider = mock(CustomEnvironmentProvider::class.java)
        val version = "1.1"

        printscriptAdapter.execute(content, inputProvider, outputProvider, environmentProvider, version)

        verify(outputProvider).print("Hello World")
    }

    @Test
    fun `test execute method with ASTBuilderFailure`() {
        val content = "println('Hello World')"
        val inputProvider = mock(StringListInputProvider::class.java)
        val outputProvider = mock(ListOutputProvider::class.java)
        val environmentProvider = mock(CustomEnvironmentProvider::class.java)
        val version = "1.1"
        val errorMessage = "Missing semicolon at (1:22)"

        printscriptAdapter.execute(content, inputProvider, outputProvider, environmentProvider, version)

        verify(outputProvider).print(errorMessage)
    }

    @Test
    fun `test lint method with ASTBuilderSuccess returns empty`() {
        val content = "println('Hello World');"
        val version = "1.1"

        val result = printscriptAdapter.lint(content, scaConfigs, version)

        assert(result.isBlank())
    }

    @Test
    fun `test lint method with ASTBuilderSuccess and rule violation returns message`() {
        val content = "println(a+b);"
        val version = "1.1"

        val result = printscriptAdapter.lint(content, scaConfigs, version)

        assert(result.isNotEmpty())
    }

    @Test
    fun `test lint method with ASTBuilderFailure returns message`() {
        val content = "println(a+b)"
        val version = "1.1"

        val result = printscriptAdapter.lint(content, scaConfigs, version)

        assert(result.isNotEmpty())
    }

    @Test
    fun `test format method with ASTBuilderSuccess returns formated content`() {
        val content = "println(a+b);"
        val version = "1.1"

        val result = printscriptAdapter.format(content, formaterConfigs, version)

        assertNotEquals(content, result)
    }

    @Test
    fun `test format method with ASTBuilderFailure throws exception`() {
        val content = "println(a+b)"
        val version = "1.1"

        assertThrows<IllegalStateException> {
            printscriptAdapter.format(content, formaterConfigs, version)
        }
    }

    @Test
    fun `test validateChunk method with ASTBuilderFailure throws exception`() {
        val content = ">"
        val version = "1.0"

        assertThrows<IllegalStateException> {
            printscriptAdapter.format(content, formaterConfigs, version)
        }
    }

    @Test
    fun `test execute method with empty inputs returns IndexOutOfBoundsException`() {
        val content = "println('Hello World');\n readInput('Hello');"
        val inputs = mutableListOf<String>()
        val inputProvider = StringListInputProvider(inputs)
        val outputs = mutableListOf<String>()
        val outputProvider = ListOutputProvider(outputs)
        val environmentProvider = mock(CustomEnvironmentProvider::class.java)
        val version = "1.1"

        printscriptAdapter.execute(content, inputProvider, outputProvider, environmentProvider, version)

        assertEquals(listOf("Hello World", "Hello"), outputs)
    }

    @Test
    fun `test execute method with blank content returns empty output`() {
        val content = " "
        val inputs = mutableListOf<String>()
        val inputProvider = StringListInputProvider(inputs)
        val outputs = mutableListOf<String>()
        val outputProvider = ListOutputProvider(outputs)
        val environmentProvider = mock(CustomEnvironmentProvider::class.java)
        val version = "1.1"

        printscriptAdapter.execute(content, inputProvider, outputProvider, environmentProvider, version)

        assert(outputs.isEmpty())
    }
}
