package models

data class ModelPlayer(
    val firstName: String,
    val id: Long,
    val image: String,
    val lastName: String,
    val role: String,
    val summonerName: String
) {
    fun getDisplayText(teamName: String? = null): String {
        var displayText = ""
        if (teamName != null) {
            displayText += "$teamName "
        }
        if (firstName.isNotEmpty()) {
            displayText += "$firstName "
        }

        displayText += "\"$summonerName\" "

        if (lastName.isNotEmpty()) {
            displayText +="$lastName "
        }
        displayText +=", $role ($id)"


        return displayText
    }
}
