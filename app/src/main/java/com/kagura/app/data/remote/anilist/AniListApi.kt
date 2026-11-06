package com.kagura.app.data.remote.anilist

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface AniListApi {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/")
    suspend fun queryMedia(
        @Body request: GraphQLRequest
    ): AniListGraphQLResponse
}
