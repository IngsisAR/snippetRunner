package austral.ingsisAR.snippetRunner.runner.language.printscript

import utils.InputProvider

class StringListInputProvider(private val inputList: MutableList<String>) : InputProvider {
    override fun readInput(): String {
        return inputList.removeAt(0)
    }
}
