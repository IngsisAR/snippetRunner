package austral.ingsisAHRE.snippetRunner

import austral.ingsisAR.snippetRunner.runner.model.SupportedLanguage
import austral.ingsisAR.snippetRunner.runner.service.LanguageRunnerServiceSelector
import austral.ingsisAR.snippetRunner.runner.service.PrintscriptRunnerService
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.test.Test

class LanguageRunnerServiceSelectorTest {
    @Test
    fun getRunnerServiceReturnsPrintscriptRunnerService() {
        val selector = LanguageRunnerServiceSelector()
        val runnerService = selector.getRunnerService(SupportedLanguage.PRINTSCRIPT)
        assertTrue(runnerService is PrintscriptRunnerService)
    }
}
