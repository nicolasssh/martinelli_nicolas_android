package fr.nextu.martinelli_nicolas

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import fr.nextu.martinelli_nicolas.entity.Movie
import fr.nextu.martinelli_nicolas.entity.MovieDAO

@Database(entities = [Movie::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDAO
}