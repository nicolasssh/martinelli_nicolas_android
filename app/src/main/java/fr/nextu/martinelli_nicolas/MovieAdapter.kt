package fr.nextu.martinelli_nicolas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.nextu.martinelli_nicolas.entity.Movies

class MovieAdapter(private val movies: Movies) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.title_tv)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_item, parent, false)

        return MovieViewHolder(view)
    }

    override fun getItemCount() = movies.movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.textView.text = movies.movies[position].title
    }
}