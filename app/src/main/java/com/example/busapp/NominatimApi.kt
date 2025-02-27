import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search?format=json")
    fun search(@Query("q") query: String): Call<List<NominatimResult>>
}