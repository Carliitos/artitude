package art.projects.artitude


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_post_details.*


class PostDetails : Fragment() {

    private var postId: String = ""
    private var postObject:Post?=null
    private var imageUrl:String?=null
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

        deleteButton.setOnClickListener {
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

        getPost()

    }
    private fun getPost(){
        val db = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                postObject = p0.getValue<Post>(Post::class.java)
                    //println("Posts: "+postObject!!.imageUrl)
                    Picasso.get().load(postObject!!.imageUrl).into(postimage!!);
                    imageUrl = postObject!!.imageUrl
                    desc.setText(postObject!!.description)
                    getUserInfo(postObject!!.user!!)
                    tags.setText(postObject!!.tags!!)
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun getUserInfo(userId:String){
        var usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    username?.text = (user?.username)
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
