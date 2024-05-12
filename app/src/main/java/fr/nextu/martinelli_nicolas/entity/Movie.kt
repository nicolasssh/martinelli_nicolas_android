package fr.nextu.martinelli_nicolas.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie")
data class Movie (
    @PrimaryKey var id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "poster", defaultValue = "") val poster: String
)