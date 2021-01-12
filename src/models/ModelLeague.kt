package models

data class ModelLeague(
    val id: Long,
    val slug: String,
    val name: String,
    val priority: Int,
    val region: String,
    val image:String
)