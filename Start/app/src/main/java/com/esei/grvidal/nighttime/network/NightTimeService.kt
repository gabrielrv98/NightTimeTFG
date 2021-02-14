package com.esei.grvidal.nighttime.network

import android.util.Log
import com.esei.grvidal.nighttime.data.CalendarData
import com.esei.grvidal.nighttime.data.User
import retrofit2.Retrofit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


private const val TAG = "NightTimeService"

private const val BASE_URL = "http://192.168.1.11:8080/api/v1/"

private const val USER_URL = "user/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface NightTimeService {

    // Singleton object to make the calls to the api
    object NightTimeApi {


        // Lazy initializer so the object is not created until needed
        val retrofitService: NightTimeService by lazy {
            retrofit.create(NightTimeService::class.java)
        }
    }


    @POST(USER_URL + "login/")
    suspend fun loginAsync(@Header("username") username: String, @Header("password") password: String) : Response<Any>//Deferred<Response<Any>>

    @POST(USER_URL + "login/")
    fun login(@Header("username") username: String, @Header("password") password: String) : Response<Any>



    @GET(USER_URL+"{idUser}/{day}-{month}-{year}/{idCity}")
    suspend fun getPeopleOnDateAsync(
        @Path("idUser") id: Long,
        @Path("day") day: Int,
        @Path("day") month: Int,
        @Path("day") year: Int,
        @Path("day") idCity: Long
    ) : Response<CalendarData>


    @GET(USER_URL)
    suspend fun getProperties(): List<MarsProperty>


}

/*
    @GET("/maps/api/geocode/json?sensor=false")
    void getPositionByZip(@Query("address") String address, Callback<String> cb);

     @GET("users/{username}")
    Call<User> getUser(@Path("username") String username);

    @GET("group/{id}/users")
    Call<List<User>> groupList(@Path("id") int groupId, @Query("sort") String sort);

    @POST("users/new")
    Call<User> createUser(@Body User user);

     */


/*

    @POST("users")
    fun register(@Body authData: Map<String, String>): Call<Register>
    And create map as below:

    fun registerUser(email: String, password: String) {
        val authData = HashMap<String, String>()
        authData.put("email", email)
        authData.put("password", password)

        Singleton.service.register(authData).

        ...
    }

*/