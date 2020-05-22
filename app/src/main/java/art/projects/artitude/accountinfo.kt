package art.projects.artitude


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import art.projects.artitude.Adapter.ProfileImagesAdapter
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_accountinfo.*
import kotlinx.android.synthetic.main.fragment_accountinfo.profile_image
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class accountinfo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accountinfo, container, false)
    }

        var postList:List<Post>? = null
        var imageAdapter: ProfileImagesAdapter? = null
        var userId:String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = context?.getSharedPreferences("USER", Context.MODE_PRIVATE)
        if(preferences!=null){
            userId = preferences.getString("userid","none")!!

        }else{
            userId=FirebaseAuth.getInstance().uid.toString()
        }
        if(userId!=FirebaseAuth.getInstance().uid.toString()){
            edit.visibility=View.GONE
        }
        var usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        val user = p0.getValue<User>(User::class.java)
                        userProfName?.text = (user?.username)
                        println("Pic is "+user?.avatarUrl)
                        if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                            Picasso.get().load(user.avatarUrl).into(profile_image!!);
                        }
                        if(user?.bio!=""){
                            bios?.text = user?.bio
                        }else{
                            bios.visibility=View.GONE
                        }

                    }


            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })

        edit.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.editProfile)
        }
        //Recicler
        reciclerView.setHasFixedSize(true)

        var linearLayoutManager:LinearLayoutManager = GridLayoutManager(context, 3)
        reciclerView.layoutManager = linearLayoutManager

        postList = ArrayList()
        imageAdapter = context?.let {
            ProfileImagesAdapter(
                it,
                postList as ArrayList<Post>,
                Navigation.findNavController(this.view!!)
            )
        }
        reciclerView.adapter = imageAdapter
        getUserPictures()

    }

    private fun getUserPictures(){
        val posts = FirebaseDatabase.getInstance().reference.child("Posts")
        posts.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (postList as ArrayList<Post>).clear()
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)!!
                        if(post.user.equals(userId)){
                            (postList as ArrayList<Post>).add(post)
                        }
                        Collections.reverse(postList)
                        imageAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}


