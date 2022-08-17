package com.example.myapplication.activities.user_profile
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.NewsFeedClass

class ProfileNewsfeedAdapter :RecyclerView.Adapter<ProfileNewsfeedAdapter.ViewHolder>() {
    private val list1 = ArrayList<NewsFeedClass>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.username.text = list1[position].username
        holder.newsfeed.text = list1[position].content
        holder.constraintLayout.setBackgroundColor(  (Color.parseColor(getRandomColor())))


    }

    private fun getRandomColor(): String {
        val colorList= listOf("#55a169", "#74dbb4" , "#72d6ca", "#72b0d6","#727fd6","#9d72d6", "#ac72d6" , "#c072d6", "#d6729d","#d67286","#81d672","#9ad672","#bbd672","#d6bf72")
        val num=(1..colorList.size).random()
        return colorList[num-1]
    }

    override fun getItemCount(): Int {

        return list1.size
    }

    fun updateList(list: ArrayList<NewsFeedClass>) {
        list1.clear()
        list1.addAll(list)

        notifyDataSetChanged()

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.TVUsername)
        val newsfeed: TextView = itemView.findViewById(R.id.TVNewsfeed)
        val constraintLayout:ConstraintLayout=itemView.findViewById(R.id.CL)

    }

}


