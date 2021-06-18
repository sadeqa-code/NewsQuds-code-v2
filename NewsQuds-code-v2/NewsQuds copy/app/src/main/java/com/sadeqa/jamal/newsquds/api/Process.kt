package com.sadeqa.jamal.newsquds.api

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.sadeqa.jamal.newsquds.model.News
import com.sadeqa.jamal.newsquds.model.NewsResponse
import com.sadeqa.jamal.newsquds.api.RetrofitInstance.Companion.api
import com.sadeqa.jamal.newsquds.db.NewsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sadeqa.jamal.newsquds.model.Source

object Process {

    public const val TAG = "Process"
    public const val QUADS = "quads"
    public fun getBreakingNews(countryCode: String, pageNumber: Int
                               ,onResponse: ((NewsResponse?) -> Unit)
                               ,onFailure: ((String?) -> Unit)) {
        api.getBreakingNews(countryCode,pageNumber).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>?, response: Response<NewsResponse>?) {
                onResponse(response?.body())
            }

            override fun onFailure(call: Call<NewsResponse>?, t: Throwable?) {
                onFailure(t?.message)
            }
        })
    }


    public fun searchNews(searchQuery: String, pageNumber: Int
                               ,onResponse: ((NewsResponse?) -> Unit)
                               ,onFailure: ((String?) -> Unit)) {
        api.searchForNews(searchQuery,pageNumber).enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>?, response: Response<NewsResponse>?) {
                onResponse(response?.body())
            }

            override fun onFailure(call: Call<NewsResponse>?, t: Throwable?) {
                onFailure(t?.message)
            }
        })
    }

    public fun insertNewsToFirebase(news: News){
        val db = Firebase.firestore
        val toHashMap = hashMapOf(
            "id" to news.id,
            "author" to news.author!!,
            "content" to news.content!!,
            "description" to news.description!!,
            "publishedAt" to news.publishedAt!!,
            "source" to news.source?.name,
            "title" to news.title!!,
            "url" to news.url!!,
            "urlToImage" to news.urlToImage!!,
        )


        db.collection(QUADS).document("${news.url?.replace("/","")}")
            .set(toHashMap)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }

    public fun getAllNewsFromFirebase(context : Context) : List<News> {
        var  news : ArrayList<News> = ArrayList<News>()
        Firebase.firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    news.add((News(
                        document.data.get("id") as Int,
                        document.data.get("author") as String,
                        document.data.get("content") as String,
                        document.data.get("description") as String,
                        document.data.get("publishedAt") as String,
                        Source("1",document.data.get("source") as String),
                        document.data.get("source") as String,
                        document.data.get("title") as String,
                        document.data.get("url") as String,
                        document.data.get("urlToImage") as String,
                    )))
                }
                Log.d(TAG, "news size ${news.size}")
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }

        return news
    }

    public fun deleteNewsToFirebase (context : Context,news: News){
        val db = Firebase.firestore

        db.collection(QUADS).document("${news.url?.replace("/","")}")
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

    }


//    public fun insertNews(context : Context,news: News){
//        GlobalScope.launch(Dispatchers.IO) {
//            NewsDatabase(context).getNewsDao().insertNews(news)
//        }
//    }
//
//    public fun getAllNews(context : Context) : LiveData<List<News>> {
//        return NewsDatabase(context).getNewsDao().getAllNews()
//    }
//
//    public fun deleteNews(context : Context,news: News){
//        GlobalScope.launch(Dispatchers.IO) {
//            NewsDatabase(context).getNewsDao().deleteNews(news)
//        }
//    }
}