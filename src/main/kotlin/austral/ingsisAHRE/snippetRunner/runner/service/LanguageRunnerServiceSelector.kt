package austral.ingsisAHRE.snippetRunner.runner.service

import austral.ingsisAHRE.snippetRunner.runner.model.SupportedLanguage
import org.springframework.stereotype.Component

@Component
class LanguageRunnerServiceSelector() {
    fun getRunnerService(language: SupportedLanguage): RunnerService {
        return when (language) {
            SupportedLanguage.PRINTSCRIPT -> PrintscriptRunnerService()
        }
    }
}
