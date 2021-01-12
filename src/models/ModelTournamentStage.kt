package models

data class ModelTournamentStage(
    val name: String,
    val sections: List<ModelTournamentSection>,
    val slug: String,
    val type: String
)
