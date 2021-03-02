package com.esei.grvidal.nighttime.network

import com.esei.grvidal.nighttime.data.City
import com.esei.grvidal.nighttime.data.EventData
import com.esei.grvidal.nighttime.data.UserSnap
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


private const val BASE_URL = "http://192.168.1.11:8080/api/v1/"

private const val USER_URL = "user/"

@JsonClass(generateAdapter = true)
data class DateCityDTO(
    val nextDate: String,
    val nextCityId: Long
)

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(BASE_URL)
    .build()

data class FutureDates( val nextDate: String)

interface NightTimeService {

    // Singleton object to make the calls to the api
    object NightTimeApi {


        // Lazy initializer so the object is not created until needed
        val retrofitService: NightTimeService by lazy {
            retrofit.create(NightTimeService::class.java)
        }
    }


    @POST(USER_URL + "login/")
    suspend fun loginAsync(@Header("username") username: String, @Header("password") password: String) : Response<Any>

    @GET(USER_URL + "cities/")
    suspend fun getAllCitiesAsync() : Response<List<City>>

    @GET("$USER_URL{idUser}/{day}-{month}-{year}/{idCity}")
    suspend fun getPeopleAndEventsOnDateAsync(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Path("day") day: Int,
        @Path("month") month: Int,
        @Path("year") year: Int,
        @Path("idCity") idCity: Long
    ) : Response<List<EventData>>

    @GET("$USER_URL{idUser}/{day}-{month}-{year}/{idCity}/users")
    suspend fun getUsersOnDateAsync(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Path("day") day: Int,
        @Path("month") month: Int,
        @Path("year") year: Int,
        @Path("idCity") idCity: Long
    ) : Response<List<UserSnap>>

    @GET("$USER_URL{idUser}/day-list/{idCity}")
    suspend fun getFutureUsersDateList(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Path("idCity") idCity: Long
    ): Response<List<FutureDates>>


    @PUT("$USER_URL{idUser}/date")
    suspend fun addDateAsync(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Body dateCity: DateCityDTO
    ) : Response<Any>

    //Delete should not have body for Retrofit
    @HTTP(method = "DELETE", path = "$USER_URL{idUser}/date", hasBody = true)
    suspend fun removeDateAsync(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Body dateCity: DateCityDTO
    ) : Response<Any>


}