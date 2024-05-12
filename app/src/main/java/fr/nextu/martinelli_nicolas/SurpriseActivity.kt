package fr.nextu.martinelli_nicolas

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import fr.nextu.martinelli_nicolas.entity.Movies
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import android.Manifest
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SurpriseActivity : AppCompatActivity() {

    private lateinit var json: TextView
    private lateinit var movieRecycler: RecyclerView
    lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_surprise)

        movieRecycler = findViewById<RecyclerView>(R.id.movie_recycler).apply {
            adapter = MovieAdapter(Movies(emptyList()))
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this@SurpriseActivity)
        }

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "cour_android.db"
        ).build()

        json = findViewById(R.id.json)
        json.movementMethod = ScrollingMovementMethod()

        createNotificationChannel()
    }

    override fun onStart() {
        super.onStart()
        updateViewFromDB()
        getPictureList()
    }

    fun updateViewFromDB() {
        CoroutineScope(Dispatchers.IO).launch {
            val flow = db.movieDao().getFlowData()
            flow.collect {
                CoroutineScope(Dispatchers.Main).launch {
                    movieRecycler.adapter = MovieAdapter(Movies(it))
                }
            }
        }
    }

    fun getPictureList() {
        CoroutineScope(Dispatchers.IO).launch {
            requestPictureList(::moviesFromJson)
        }
    }

    private fun moviesFromJson(json: String) {
        val gson = Gson()
        val om = gson.fromJson(json, Movies::class.java)
        db.movieDao().insertAll(*om.movies.toTypedArray())
    }

    fun requestPictureList(callback: (String)-> Unit) {
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url("https://api.betaseries.com/movies/list")
            .get()
            .addHeader("X-BetaSeries-Key", getString(R.string.betaseries_api_key))
            .build()

        val response: Response = client.newCall(request).execute()
        val data = response.body?.string() ?: ""

        CoroutineScope(Dispatchers.Main).launch {
            notifyNewData(data)
        }
        callback(data)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Movie update"
            val descriptionText = "A update notifiation when new movies come"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun notifyNewData(response: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("New movies")
            .setContentText(response)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                this@SurpriseActivity,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
                ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                return@with
            }
            notify(1, builder.build())
        }
    }

    companion object {
        const val CHANNEL_ID = "fr_nextu_martinelli_nicolas_channel_notification"
    }
}