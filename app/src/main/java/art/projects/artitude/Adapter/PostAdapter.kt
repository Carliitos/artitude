package art.projects.artitude.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.User
import art.projects.artitude.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private var mContext: Context, private var post:List<Post>, private var isFragment: Boolean=false, private var navController: NavController):
    RecyclerView.Adapter<PostAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false)
        return PostAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return post.size
    }

    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        val post = post[position]

        holder.top.text="TOP #"+(position+1)
        Picasso.get().load(post.imageUrl).placeholder(R.drawable.profilenoimage).into(holder.postImage)
        if(post.description!=""){
            holder.postDescription.text = post.description
        }else{
            holder.postDescription.visibility=View.GONE
        }
//        if(post.avatarUrl!=""){
//            Picasso.get().load(post.avatarUrl).placeholder(R.drawable.profilenoimage).into(holder.userProfImage)
//        }
//        holder.userProfImage.setOnClickListener {
//            val editor = mContext.getSharedPreferences("USER",Context.MODE_PRIVATE).edit()
//            editor.putString("userid",post.uid)
//            editor.apply()
//
//
//            navController.navigate(R.id.accountinfo)
//        }
        val users = FirebaseDatabase.getInstance().reference.child("users").child(post.user!!)
        users.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                        val user = p0.getValue(User::class.java)
                        if(user!=null){
                            holder.userName.text = user.username
                            Picasso.get().load(user.avatarUrl).placeholder(R.drawable.profilenoimage).into(holder.profImage)

                        }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        holder.userRL.setOnClickListener {
            val editor = mContext.getSharedPreferences("USER",Context.MODE_PRIVATE).edit()
            editor.putString("userid",post.user)
            editor.apply()


            navController.navigate(R.id.accountinfo)
        }


    }
    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.username)
        var profImage: CircleImageView = itemView.findViewById(R.id.profile_image )
        var postImage: ImageView = itemView.findViewById(R.id.image)
        var postDescription: TextView = itemView.findViewById(R.id.postdescription)
        var top: TextView = itemView.findViewById(R.id.top)
        var userRL: RelativeLayout = itemView.findViewById(R.id.user)
    }

}