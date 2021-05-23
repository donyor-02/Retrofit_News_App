package com.example.retrofit_news_app

import com.federicocotogno.retro2newslist.api.NewsJSON
import retrofit2.http.GET

interface APIRequest {
    @GET("/v1/latest-news?language=en&apiKey=7goxs3CKQUAJRGhquf6BovIkIToi_vj5Uii2lTrQCVE9EtY2")
    suspend fun getNews(): NewsJSON

}