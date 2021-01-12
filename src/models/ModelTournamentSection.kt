package models

data class ModelTournamentSection(
    val name: String,
    val rankings: Map<Int, List<ModelTournamentTeam>>,
    val matches: List<ModelTournamentMatch>
)