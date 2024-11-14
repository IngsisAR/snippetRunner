package austral.ingsisAR.snippetRunner.runner.language.printscript

import utils.OutputProvider

class ListOutputProvider(private val outputs: MutableList<String>) : OutputProvider {
    override fun print(string: String) {
        outputs.add(string)
    }
}
