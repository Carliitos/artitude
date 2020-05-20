package art.projects.artitude.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import art.projects.artitude.Models.Post
import art.projects.artitude.R
import com.squareup.picasso.Picasso

class ProfileImagesAdapter(private val mContext: Context, posts:List<Post>,
                           private var navController: NavController
)
    :RecyclerView.Adapter<ProfileImagesAdapter.ViewHolder?>()
{
    private var mPost : List<Post>? = null

    init {
        this.mPost = posts
    }
    var view: View?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         view = LayoutInflater.from(this.mContext).inflate(R.layout.images_item,parent,false)
        return ViewHolder(view!!)
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]
        Picasso.get()!!.load(post.imageUrl!!).into(holder.postImage)
        holder.postImage.setOnClickListener {
            val editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            editor.putString("postId",post.postid)
            editor.apply()


            navController.navigate(R.id.postDetails)
        }
    }

    inner class ViewHolder(@NonNull itemView:View)
        : RecyclerView.ViewHolder(itemView){
        var postImage : ImageView
        init {
            postImage=itemView.findViewById(R.id.post_image)
        }
    }
}