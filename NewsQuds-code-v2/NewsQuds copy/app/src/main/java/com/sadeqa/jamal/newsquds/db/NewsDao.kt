package com.sadeqa.jamal.newsquds.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sadeqa.jamal.newsquds.model.News

@Dao
interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNews(article: News): Long

    @Query("SELECT * FROM news")
    fun getAllNews(): LiveData<List<News>>

    @Delete
    fun deleteNews(article: News)
}