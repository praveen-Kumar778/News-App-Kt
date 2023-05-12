package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.Carousel
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mig35.carousellayoutmanager.CarouselLayoutManager
import com.mig35.carousellayoutmanager.CarouselLayoutManager.OnCenterItemSelectionListener
import com.mig35.carousellayoutmanager.CarouselZoomPostLayoutListener
import com.mig35.carousellayoutmanager.CenterScrollListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    lateinit var adapter: NewsAdapter
    private var articles = mutableListOf<Article>()
    var page = 1
    var totalResults = -1
    private var mInterstitialAd: InterstitialAd? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myRecyclerView = findViewById<RecyclerView>(R.id.news_list)
        MobileAds.initialize(this)
        loadAds()


        mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback(){
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                Toast.makeText(this@MainActivity,"ad closed",Toast.LENGTH_SHORT).show()
            }
        }
        val check = findViewById<Button>(R.id.check)
        check.setOnClickListener{
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            }
        }
        adapter = NewsAdapter(this@MainActivity,articles)
        myRecyclerView.adapter = adapter

        val layoutManager = CarouselLayoutManager(CarouselLayoutManager.VERTICAL)
        // To enable zoom effects that is enabled in gif adding this line:
        layoutManager.setPostLayoutListener(CarouselZoomPostLayoutListener())
        myRecyclerView.layoutManager = layoutManager
        myRecyclerView.setHasFixedSize(true)
        // To enable items center scrolling adding this CenterScrollListener:
        myRecyclerView.setOnScrollListener(CenterScrollListener())
        // in this function of getNews() we wrote the code of getting data from server through retrofit
        getNews()
    }

    private fun loadAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this@MainActivity,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                mInterstitialAd = interstitialAd
            }
        })
    }

    private fun getNews() {

        val news :Call<News> = NewsService.instance.getNews("in",page)
        // when we are getting the data from server it will place our requests in a que and then it will show it one by one
        news.enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                Log.d("Main Activity",response.body().toString())
                val news = response.body()
                if(news!=null){
                    totalResults = news.totalResults
                    articles.addAll(news.articles)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}