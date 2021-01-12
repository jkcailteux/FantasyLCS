package models

data class ModelTournamentMatch(
    val flags: List<String>,
    val id: Long,
    val previousMatchId: Long?,
    val state: String,
    val teams: List<ModelTournamentTeam>,
    val result: Map<Long, ModelTournamentMatchResult>?
)

data class ModelTournamentMatchResult(
    val outcome: String?,
    val gameWins: Int
)
