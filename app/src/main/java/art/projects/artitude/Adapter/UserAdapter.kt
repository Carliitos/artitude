package art.projects.artitude.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import art.projects.artitude.Models.User
import art.projects.artitude.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var mContext: Context,
    private var mUser: List<User>,
    private var navController: NavController
):RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mUser.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userName.text = user.username
        if(user.avatarUrl!=""){
            Picasso.get().load(user.avatarUrl)
                .placeholder(R.drawable.profilenoimage)
                .into(holder.userProfImage)
        }
        holder.userProfImage.setOnClickListener {
            val editor = mContext
                .getSharedPreferences("USER",Context.MODE_PRIVATE)
                .edit()
            editor.putString("userid",user.uid)
            editor.apply()
            navController.navigate(R.id.accountinfo)
        }
    }
    class ViewHolder(@NonNull itemView: View):RecyclerView.ViewHolder(itemView){
        var userName: TextView = itemView.findViewById(R.id.userprofname)
        var userProfImage: CircleImageView = itemView.findViewById(R.id.userprofimage )
    }

}