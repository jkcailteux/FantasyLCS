package models

data class ModelTournamentTeam(
    val id: Long,
    val code: String,
    val image: String,
    val name: String,
    val slug: String,
    val record: ModelTeamRecord?
)

data class ModelTeamRecord(
    val wins: Int,
    val losses: Int
)