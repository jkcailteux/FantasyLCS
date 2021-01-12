import models.ModelFullTeam
import models.ModelPlayer
import service.ApiClient

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val apiClient = ApiClient()
            val leagues = apiClient.getLeagues()
            val lcsLeague = leagues.firstOrNull { it.name == "LCS" }
            val tournaments = apiClient.getTournamentsForLeague(lcsLeague?.id ?: return)
            val lockInTournament = tournaments.firstOrNull { it.slug == "lcs_2021_lockin" } ?: return
            val lockInStandings = apiClient.getStandingsForTournament(lockInTournament.id)

            val fullTeams = mutableListOf<ModelFullTeam>()
            lockInStandings.forEach { stage ->
                stage.sections.forEach { section ->
                    section.rankings.values.forEach { teams ->
                        teams.forEach { team ->
                            val fullTeam = apiClient.getFullTeam(team.slug)
                            fullTeam?.let {
                                fullTeams.add(it)
                            }
                        }
                    }
                }
            }

            val playerMap = mutableMapOf<ModelFullTeam, List<ModelPlayer>>()

            println("Teams for Tournament: \"${lockInTournament.slug}\"")
            fullTeams.forEach { team ->
                println(team.getDisplayText())
                playerMap[team] = team.players
            }

            println()
            println("Players for Tournament: \"${lockInTournament.slug}\"")
            playerMap.forEach { entry ->
                val team = entry.key
                entry.value.forEach { player ->
                    println(player.getDisplayText(team.code))
                }
            }

            return
        }
    }
}