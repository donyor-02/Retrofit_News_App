package com.example.retrofit_news_app

import android.content.ClipDescription
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofit_news_app.adapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "https://api.currentsapi.services"

class MainActivity : AppCompatActivity() {

    lateinit var countDownTimer: CountDownTimer

    private var titlesList = mutableListOf<String>()
    private var descList = mutableListOf<String>()
    private var imagesList = mutableListOf<String>()
    private var linksList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        makeApiReqiest()
    }

    private fun fadefromblack(){
        v_blackScreen.animate().apply {
            alpha(0f)
            duration = 3000
        }.start()
    }

    private fun setUpRecyclerView(){
        rv_recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        rv_recyclerView.adapter = RecyclerAdapter(titlesList,descList,imagesList,linksList)
    }

    private fun addToList(title: String,description: String, image: String, link: String){
        titlesList.add(title)
        descList.add(description)
        imagesList.add(image)
        linksList.add(link)
    }

    private fun makeApiReqiest(){

        progressBar.visibility = View.VISIBLE

        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(APIRequest::class.java)

        GlobalScope.launch(Dispatchers.IO){
            try {
                val response = api.getNews()
                for (article in response.news)
                    addToList(article.title,article.description,article.image,article.url)
                    //Log.d("MainActivity","Result = $article")

                    withContext(Dispatchers.Main){
                        setUpRecyclerView()
                        fadefromblack()
                        progressBar.visibility = View.GONE
                    }
            }
            catch (e: Exception){
                Log.d("MainActivity",e.toString())

                withContext(Dispatchers.Main){
                    attemptRequestAgain()
                }
            }
        }
    }

    private fun attemptRequestAgain() {
        countDownTimer = object : CountDownTimer(5*1000,1000){
            override fun onTick(millisUntilFinished: Long) {
                Log.d("MainActivity","Could not retrive data... Try again ${millisUntilFinished/100} second")
            }

            override fun onFinish() {
                makeApiReqiest()
                countDownTimer.cancel()
            }
        }
        countDownTimer.start()
    }

}