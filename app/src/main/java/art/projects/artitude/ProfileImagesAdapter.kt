package art.projects.artitude

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ProfileImagesAdapter(private val mContext: Context, posts:List<Post>)
    :RecyclerView.Adapter<ProfileImagesAdapter.ViewHolder?>()
{
    private var mPost : List<Post>? = null

    init {
        this.mPost = posts
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val view = LayoutInflater.from(this.mContext).inflate(R.layout.images_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post: Post = mPost!![position]
        Picasso.get()!!.load(post.imageUrl!!).into(holder.postImage)
    }

    inner class ViewHolder(@NonNull itemView:View)
        : RecyclerView.ViewHolder(itemView){
        var postImage : ImageView
        init {
            postImage=itemView.findViewById(R.id.post_image)
        }
    }
}