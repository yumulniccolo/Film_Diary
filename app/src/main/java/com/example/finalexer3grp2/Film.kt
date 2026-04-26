package com.example.finalexer3grp2

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "films_table")
data class Film(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val year: String,
    val director: String,
    val genres: String,
    val description: String,
    val posterUri: String? = null
)
