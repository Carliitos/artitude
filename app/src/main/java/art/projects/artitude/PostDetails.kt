package art.projects.artitude


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post_details.*


class PostDetails : Fragment() {

    private var postId: String = ""
    private var postObject: Post?=null
    private var imageUrl:String?=null
    private var postUserId:String?=null
    private var userPostname:String?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(preferences!=null){
            postId = preferences.getString("postId","none")!!
        }
        var btnAnimation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fragment_fade_enter)

        val animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fragment_open_enter)
        animation.duration=500
        postimage.startAnimation(animation)
        deleteButton.setOnClickListener {
            deleteButton.startAnimation(btnAnimation)
            val animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fragment_fade_enter)
            val animationout = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fragment_fade_exit)
            deleteWarning.startAnimation(animation)
            deleteWarning.visibility=View.VISIBLE
            deny.setOnClickListener {
                deleteWarning.startAnimation(animationout)
                deleteWarning.visibility=View.GONE
            }
            deleteWarning.setOnClickListener {
                deleteWarning.startAnimation(animationout)
                deleteWarning.visibility=View.GONE
            }
            confirm.setOnClickListener {
                val progressDialog = ProgressDialog(this.requireContext())
                progressDialog.setTitle("Deleting Post")
                progressDialog.setMessage("This could take a few seconds")
                progressDialog.show()

                val query = FirebaseDatabase.getInstance().reference
                    .child("Posts").child(postId).removeValue();
                query.addOnSuccessListener {
                    val deleteQuery = FirebaseStorage.getInstance().reference.child("/PostImages/$imageUrl.jpg").delete()
                    deleteQuery.addOnCompleteListener {
                        progressDialog.dismiss()
                        Navigation.findNavController(this.view!!).navigate(R.id.accountinfo)

                    }
                }
            }
        }
        user.setOnClickListener {
            val editor = this.context!!.getSharedPreferences("USER",Context.MODE_PRIVATE).edit()
            editor.putString("userid",postUserId)
            editor.apply()
            Navigation.findNavController(this.view!!).navigate(R.id.accountinfo)
        }
        share.setOnClickListener {
            share.startAnimation(animation)
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type="text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Post by "+userPostname+" artitude.com/post/"+postId)
            startActivity(Intent.createChooser(shareIntent,"Share post"))
        }

        getPost()

    }
    private fun getPost(){
        val db = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        db.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                postObject = p0.getValue<Post>(Post::class.java)
                    //println("Posts: "+postObject!!.imageUrl)
                    Picasso.get().load(postObject!!.imageUrl).into(postimage!!);
                    imageUrl = postObject!!.imageUrl
                    postUserId=postObject!!.user
                    desc.text = postObject!!.description
                    getUserInfo(postObject!!.user!!)
                    tags.text = postObject!!.tags!!
                    if(postObject!!.timesliked==1){
                        postlikes.text = postObject!!.timesliked!!.toString()+" like"
                    }else{
                        postlikes.text = postObject!!.timesliked!!.toString()+" likes"
                    }
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun getUserInfo(userId:String){
        var usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    username?.text = (user?.username)
                    userPostname = (user?.username)
                    if(user?.uid != FirebaseAuth.getInstance().uid){
                        deleteButton.visibility=View.GONE
                    }else{
                        deleteButton.visibility=View.VISIBLE
                    }
                    if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                        Picasso.get().load(user.avatarUrl).into(profile_image!!);
                    }


                }


            }

            override fun onCancelled(p0: DatabaseError) {
            }
    })
    }


}
