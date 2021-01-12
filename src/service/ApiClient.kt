package service

import data.Constants
import data.Endpoints
import khttp.responses.Response
import models.ModelLeague
import org.json.JSONObject

class ApiClient {

    fun getLeagues() {
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
            return
        }
        if (response.statusCode != 200) {
            print(response)
            return
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

        leagueList
    }
}