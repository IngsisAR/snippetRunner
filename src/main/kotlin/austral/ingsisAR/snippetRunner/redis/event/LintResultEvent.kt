package austral.ingsisAR.snippetRunner.redis.event

data class LintResultEvent(
    val userId: String,
    val snippetId: String,
    val status: LintStatus,
)
