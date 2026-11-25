package id.pamtirtakemuning.watermeter.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @GET("assignments")
    suspend fun getAssignments(): List<Assignment>

    @GET("assignments/{id}")
    suspend fun getAssignmentById(@Path("id") id: String): Assignment

    @POST("assignments/{id}/confirm")
    suspend fun confirmAssignment(
        @Path("id") id: String,
        @Body confirmation: Confirmation
    ): Response<Assignment>

    @PUT("assignments/{id}/draft")
    suspend fun saveDraft(
        @Path("id") id: String,
        @Body report: Report
    ): Response<Assignment>

    @POST("assignments/{id}/report")
    suspend fun submitReport(
        @Path("id") id: String,
        @Body report: Report
    ): Response<Assignment>

    @GET("user/profile")
    suspend fun getUserProfile(): User
}
