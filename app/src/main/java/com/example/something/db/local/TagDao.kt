package com.example.something.db.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.something.entity.Tag
import kotlinx.coroutines.flow.Flow


@Dao
interface TagDao {


    @Upsert
    fun upsert(tag : Tag) : Long

    @Delete
    fun delete(tag : Tag)

    @Query("SELECT  * FROM tags")
    fun getAllTags() : Flow<List<Tag>>


     @Query("SELECT * FROM tags WHERE tag = :tagName LIMIT 1")
     fun getTagByName(tagName:String): Tag?


}