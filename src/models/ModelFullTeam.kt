package models

data class ModelFullTeam(
    val alternativeImage: String?,
    val backgroundImage: String?,
    val code: String,
    val homeLeague: ModelFullTeamLeague?,
    val id: Long,
    val image: String,
    val name: String,
    val players: List<ModelPlayer>,
    val slug: String,
    val status: String?
){
    fun getDisplayText() = "$name ($id)"
}

data class ModelFullTeamLeague(
    val name: String,
    val region: String
)
