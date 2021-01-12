package service

import data.Constants
import data.Endpoints
import khttp.responses.Response
import models.*
import org.json.JSONArray
import org.json.JSONObject

class ApiClient {

    fun getLeagues(): List<ModelLeague> {
        val response: Response?
        try {
            response = khttp.get(
                url = Endpoints.GET_LEAGUES,
                headers = mapOf(
                    "x-api-key" to Constants.LOLESPORTS_API_KEY
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        if (response.statusCode != 200) {
            print(response)
            return emptyList()
        }

        val rawLeagues = response.jsonObject.getJSONObject("data").getJSONArray("leagues")

        val leagueList = mutableListOf<ModelLeague>()

        rawLeagues.forEach { rawLeague ->
            rawLeague as JSONObject
            val league = ModelLeague(
                id = rawLeague.getLong("id"),
                name = rawLeague.getString("name"),
                slug = rawLeague.getString("slug"),
                priority = rawLeague.getInt("priority"),
                region = rawLeague.getString("region"),
                image = rawLeague.getString("image")
            )
            leagueList.add(league)
        }

        return leagueList
    }

    fun getTournamentsForLeague(leagueId: Long): List<ModelTournament> {
        val response: Response?
        try {
            response = khttp.get(
                url = Endpoints.GET_TOURNAMENTS_FOR_LEAGUE(leagueId),
                headers = mapOf(
                    "x-api-key" to Constants.LOLESPORTS_API_KEY
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        if (response.statusCode != 200) {
            print(response)
            return emptyList()
        }

        val rawLeaguesTournaments = response.jsonObject.getJSONObject("data").getJSONArray("leagues")

        val tournamentList = mutableListOf<ModelTournament>()

        rawLeaguesTournaments.forEach { league ->
            league as JSONObject
            league.getJSONArray("tournaments").forEach { rawTournament ->
                rawTournament as JSONObject
                tournamentList.add(
                    ModelTournament(
                        id = rawTournament.getLong("id"),
                        endDate = rawTournament.getString("endDate"),
                        slug = rawTournament.getString("slug"),
                        startDate = rawTournament.getString("startDate")
                    )
                )
            }

        }
        return tournamentList
    }

    fun getFullTeam(teamName: String): ModelFullTeam? {
        val response: Response?
        try {
            response = khttp.get(
                url = Endpoints.GET_FULL_TEAM(teamName),
                headers = mapOf(
                    "x-api-key" to Constants.LOLESPORTS_API_KEY
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        if (response.statusCode != 200) {
            print(response)
            return null
        }

        val rawTeam = response.jsonObject.getJSONObject("data").getJSONArray("teams")[0] as JSONObject
        val players = mutableListOf<ModelPlayer>()

        if (rawTeam.has("players")) {
            (rawTeam.get("players") as? JSONArray)?.forEach { player ->
                player as JSONObject
                players.add(
                    ModelPlayer(
                        firstName = player.getString("firstName"),
                        id = player.getLong("id"),
                        image = player.getString("image"),
                        lastName = player.getString("lastName"),
                        role = player.getString("role"),
                        summonerName = player.getString("summonerName")
                    )
                )
            }
        }

        val rawHomeLeague = rawTeam.get("homeLeague") as? JSONObject

        return ModelFullTeam(
            alternativeImage = rawTeam.get("alternativeImage") as? String,
            backgroundImage = rawTeam.get("backgroundImage") as? String,
            code = rawTeam.getString("code"),
            homeLeague = if (rawHomeLeague == null) null else ModelFullTeamLeague(
                name = rawHomeLeague.getString("name"),
                region = rawHomeLeague.getString("region")
            ),
            id = rawTeam.getLong("id"),
            image = rawTeam.getString("image"),
            name = rawTeam.getString("name"),
            slug = rawTeam.getString("slug"),
            status = rawTeam.get("status") as? String,
            players = players
        )

    }

    fun getStandingsForTournament(tournamentId: Long): List<ModelTournamentStage> {
        val response: Response?
        try {
            response = khttp.get(
                url = Endpoints.GET_STANDINGS_FOR_TOURNAMENT(tournamentId),
                headers = mapOf(
                    "x-api-key" to Constants.LOLESPORTS_API_KEY
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
        if (response.statusCode != 200) {
            print(response)
            return emptyList()
        }

        val rawStages = (response.jsonObject.getJSONObject("data")
            .getJSONArray("standings")[0] as JSONObject).getJSONArray("stages")

        val stagesList = mutableListOf<ModelTournamentStage>()
        rawStages.forEach { rawStage ->
            rawStage as JSONObject

            val sections = mutableListOf<ModelTournamentSection>()
            rawStage.getJSONArray("sections").forEach { rawSection ->
                rawSection as JSONObject

                val matchesList = mutableListOf<ModelTournamentMatch>()
                rawSection.getJSONArray("matches").forEach { rawMatch ->
                    rawMatch as JSONObject
                    val teamsList = mutableListOf<ModelTournamentTeam>()
                    val resultMap = mutableMapOf<Long, ModelTournamentMatchResult>()
                    rawMatch.getJSONArray("teams").forEach { rawTeam ->
                        rawTeam as JSONObject
                        val rawResult =
                            if (rawTeam.has("result"))
                                rawTeam.get("result") as? JSONObject
                            else
                                null
                        resultMap[rawTeam.getLong("id")] = ModelTournamentMatchResult(
                            outcome = if (rawResult?.has("outcome") == true)
                                rawResult.get("outcome").toString()
                            else
                                null,
                            gameWins = rawResult?.getInt("gameWins") ?: 0
                        )
                        teamsList.add(
                            ModelTournamentTeam(
                                id = rawTeam.getLong("id"),
                                code = rawTeam.getString("code"),
                                image = rawTeam.getString("image"),
                                name = rawTeam.getString("name"),
                                slug = rawTeam.getString("slug"),
                                record = null
                            )
                        )
                    }


                    matchesList.add(
                        ModelTournamentMatch(
                            flags = rawMatch.getJSONArray("flags").toList() as List<String>,
                            id = rawMatch.getLong("id"),
                            previousMatchId = if (rawMatch.has("previousMatchId")) rawMatch.getLong("previousMatchId") else null,
                            state = rawMatch.getString("state"),
                            teams = teamsList,
                            result = resultMap
                        )
                    )
                }

                val rankingMap = LinkedHashMap<Int, List<ModelTournamentTeam>>()
                rawSection.getJSONArray("rankings").forEach { rawRanking ->
                    rawRanking as JSONObject
                    val ordinal = rawRanking.getInt("ordinal")
                    val teamsList = mutableListOf<ModelTournamentTeam>()

                    rawRanking.getJSONArray("teams").forEach { rawTeam ->
                        rawTeam as JSONObject
                        teamsList.add(
                            ModelTournamentTeam(
                                id = rawTeam.getLong("id"),
                                code = rawTeam.getString("code"),
                                image = rawTeam.getString("image"),
                                name = rawTeam.getString("name"),
                                slug = rawTeam.getString("slug"),
                                record = null
                            )
                        )
                    }

                    rankingMap[ordinal] = teamsList
                }

                sections.add(
                    ModelTournamentSection(
                        name = rawSection.getString("name"),
                        matches = matchesList,
                        rankings = rankingMap
                    )
                )
            }

            stagesList.add(
                ModelTournamentStage(
                    name = rawStage.getString("name"),
                    slug = rawStage.getString("slug"),
                    type = rawStage.getString("type"),
                    sections = sections
                )
            )
        }

        return stagesList
    }

}