package austral.ingsisAR.snippetRunner.redis.event

data class LintRequestEvent(
    val userId: String,
    val snippetId: String,
    val linterRules: LinterRulesDTO = LinterRulesDTO(),
)