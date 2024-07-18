package austral.ingsisAHRE.snippetRunner.runner.language.printscript

import utils.EnvironmentProvider

class CustomEnvironmentProvider(private val environmentVars: HashMap<String, String>) : EnvironmentProvider {
    override fun getEnv(name: String): String? {
        return environmentVars[name]
    }
}
