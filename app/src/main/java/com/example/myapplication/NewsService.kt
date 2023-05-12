package com.example.myapplication

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//https://newsapi.org/v2/everything?q=apple&from=2023-05-10&to=2023-05-10&sortBy=popularity&apiKey=API_KEY
// https://newsapi.org/v2/top-headlines?country=in&apiKey=4cd6bb2b1bb7435aa9b3571fd8ee4a14
const val BASE_URL = "https://newsapi.org/"
const val API_KEY = "4cd6bb2b1bb7435aa9b3571fd8ee4a14"
interface NewsInterface{

    // so this get method will be hitted when we want the news from server in the form of page because we have 100 or 1000 news online so we don't access all data in one go so it will accessed by page form like first 10 news then again 10 news
    @GET("v2/top-headlines?apiKey=$API_KEY")
    fun getNews(@Query("country") country:String,@Query("page") page:Int) :Call<News>

    //Our url which will be hitted looks like
    //https://newsapi.org/v2/top-headlines?apiKey=4cd6bb2b1bb7435aa9b3571fd8ee4a14&country=in&page=1
}
object NewsService{
    // that's how we make singleton object
    val instance:NewsInterface
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        instance = retrofit.create(NewsInterface::class.java)
    }
}