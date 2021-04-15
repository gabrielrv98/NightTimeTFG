package com.esei.grvidal.nighttime.network

import com.esei.grvidal.nighttime.data.*
import com.squareup.moshi.JsonClass
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*


const val BASE_URL = "http://192.168.1.11:8080/api/v1/"

const val USER_URL = "user/"
const val BAR_URL = "bar/"

const val ERROR_HEADER_TAG = "error"

@JsonClass(generateAdapter = true)
data class DateCityDTO(
    val nextDate: String,
    val nextCityId: Long
)

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))//.asLenient())
    .baseUrl(BASE_URL)
    .build()

data class FutureDates( val nextDate: String)

@Suppress("unused")
interface NightTimeService {

    // Singleton object to make the calls to the api
    object NightTimeApi {


        // Lazy initializer so the object is not created until needed
        val retrofitService: NightTimeService by lazy {
            retrofit.create(NightTimeService::class.java)
        }
    }

    // Login
    @POST(USER_URL + "login/")
    suspend fun loginAsync(
        @Header("username") username: String, @Header("password") password: String
    ) : Response<Any>

    // City
    @GET(USER_URL + "cities/")
    suspend fun getAllCitiesAsync() : Response<List<City>>

    // Calendar
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

    @GET(BAR_URL+"byCity/{idCity}")
    suspend fun listByCity(
        @Path("idCity") idCity: Long,
        @Query("page") page: Int = 0
    ) : Response<List<BarDTO>>

    @GET("$BAR_URL{id}/details")
    suspend fun getBarDetails(
        @Path("id") id: Long
    ): Response<BarDetailsDTO>

    @GET("$USER_URL{idUser}")
    suspend fun getUserDetails(
        @Path("idUser") id: Long
    ): Response<UserDTO>

    @GET("$USER_URL{idUser}/private")
    suspend fun getUserPrivate(
        @Header("auth") auth: String,
        @Path("idUser") id: Long
    ): Response<UserViewPrivate>

    @PATCH("$USER_URL{idUser}")
    suspend fun updateUser(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Body user: UserDTOEdit
    ): Response<Any>

    @Multipart
    @POST("$USER_URL{idUser}/picture")
    suspend fun setPicture(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Part img: MultipartBody.Part
    ): Response<Any>

    @POST(USER_URL)
    suspend fun newUser(
        @Body user: UserDTOInsert
    ): Response<Boolean>

    @GET("$USER_URL/{idUser}/chat")
    suspend fun getChats(
        @Header("auth") auth: String,
        @Path("idUser") id: Long
    ): Response<List<ChatView>>

    @GET(USER_URL+"search/{userNickname}")
    suspend fun searchUsers(
        @Path("userNickname") string: String,
        @Query("page") page: Int = 0
    ): Response<List<UserSnap>>

    /*
    @POST("$USER_URL/{idUser}/friends")
    suspend fun addUserRequest(
        @Header("auth") auth: String,
        @Path("idUser") id: Long,
        @Body friendship : FriendshipInsertRequest
    ): Response<List<ChatView>>

     */

}