package data

@Suppress("FunctionName")
object Endpoints {

    const val GET_LEAGUES = "https://esports-api.lolesports.com/persisted/gw/getLeagues?hl=en-US"

    fun GET_TOURNAMENTS_FOR_LEAGUE(leagueId: Long) =
        "https://esports-api.lolesports.com/persisted/gw/getTournamentsForLeague?hl=en-US&leagueId=$leagueId"

    fun GET_FULL_TEAM(teamName:String) = "https://esports-api.lolesports.com/persisted/gw/getTeams?hl=en-US&id=$teamName"

    fun GET_STANDINGS_FOR_TOURNAMENT(tournamentId: Long) =
        "https://esports-api.lolesports.com/persisted/gw/getStandings?hl=en-US&tournamentId=$tournamentId"
}