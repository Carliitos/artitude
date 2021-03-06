package art.projects.artitude


import android.content.Context
import android.media.Image
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import art.projects.artitude.Adapter.LikedImagesAdapter
import art.projects.artitude.Adapter.ProfileImagesAdapter
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

import kotlinx.android.synthetic.main.fragment_accountinfo.*
import kotlinx.android.synthetic.main.fragment_accountinfo.profile_image
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class accountinfo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accountinfo, container, false)
    }
        var postList:List<Post>? = null
        var likedPostList:List<Post>? = null
        var imageAdapter: ProfileImagesAdapter? = null
        var likedImageAdapter: LikedImagesAdapter? = null
        var userId:String?=null
    companion object{
        var userobj:User?=null
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).hideBackButton()

        val preferences = context?.getSharedPreferences("USER", Context.MODE_PRIVATE)
        val currentUser = FirebaseAuth.getInstance().uid.toString()
        if(preferences==null){
            userId=currentUser
        }else{
            userId = preferences.getString("userid","none")!!
            if(userId=="none"){
                alert.visibility=View.VISIBLE
                reciclerView.visibility=View.GONE
            }
        }

        if(userId==currentUser){
            getLikedPictures()
            sendmessage.visibility=View.GONE
        }else{
            likebar.visibility=View.INVISIBLE
            edit.visibility=View.GONE
        }
        val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userId!!)
        usersRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    userobj = user
                    userProfName?.text = (user?.username)
                    if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                        Picasso.get().load(user.avatarUrl).into(profile_image!!);
                    }
                    if(user?.bio!=""){
                        bios?.text = user?.bio
                    }else{
                        bios?.visibility=View.GONE
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

        val linearLayoutManager:LinearLayoutManager = GridLayoutManager(context, 3)
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

        //Liked recycler

        val linearLayoutManagerliked:LinearLayoutManager = GridLayoutManager(context, 3)
        likedrecycler.setHasFixedSize(true)

        likedrecycler.layoutManager = linearLayoutManagerliked

        likedPostList = ArrayList()
        likedImageAdapter = context?.let {
            LikedImagesAdapter(
                it,
                likedPostList as ArrayList<Post>,
                Navigation.findNavController(this.view!!)
            )
        }
        likedrecycler.adapter = likedImageAdapter

        getUserPictures()

        //Liked images
        likedimages.setOnClickListener {
            likedrecycler.visibility=View.VISIBLE
            reciclerView.visibility=View.GONE
            likedimages.setImageResource(R.drawable.heartfill)
            images.setImageResource(R.drawable.squarenofil)
        }
        images.setOnClickListener {
            reciclerView.visibility=View.VISIBLE
            likedrecycler.visibility=View.GONE
            likedimages.setImageResource(R.drawable.heartnofill)
            images.setImageResource(R.drawable.filledsquare)
        }
        sendmessage.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.toChat)
        }

    }

    private fun getLikedPictures() {
        val posts = FirebaseDatabase.getInstance().reference.child("users").child(userId.toString()).child("liked")
        posts.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (likedPostList as ArrayList<Post>).clear()
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)!!

                            if(post.imageUrl!=""&&Picasso.get().load(post.imageUrl!!)!=null){
                                (likedPostList as ArrayList<Post>).add(post)

                            }
                            println("There is a liked image")
                        //Collections.reverse(postList!!)
                    }
                            likedImageAdapter?.notifyDataSetChanged()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    private fun getUserPictures(){
        val posts = FirebaseDatabase.getInstance().reference.child("Posts")
        posts.addListenerForSingleValueEvent(object: ValueEventListener{
            var totalLikes = 0;
            var totalPosts = 0
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    (postList as ArrayList<Post>).clear()
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)!!
                        if(post.user.equals(userId)){
                            (postList as ArrayList<Post>).add(post)
                            totalLikes += post.timesliked!!
                            imageAdapter?.notifyDataSetChanged()
                            totalPosts++;
                        }
                        Collections.reverse(postList!!)
                    }
                }
                postnumber?.text=totalPosts.toString()
                likes?.text =totalLikes.toString()
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
        //Gets the total liked images
        FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("liked")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    liked?.text = p0.childrenCount.toString()
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }

}


