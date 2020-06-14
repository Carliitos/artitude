package art.projects.artitude


import android.app.ProgressDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import art.projects.artitude.Models.Comment
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.comment_row.view.*
import kotlinx.android.synthetic.main.fragment_post_details.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


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
    var userid:String?=null
    companion object{
    val adapter = GroupAdapter<GroupieViewHolder>()

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userid=FirebaseAuth.getInstance().currentUser!!.uid
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
            val animation = AnimationUtils
                .loadAnimation(this.requireContext(), R.anim.fragment_fade_enter)
            val animationout = AnimationUtils
                .loadAnimation(this.requireContext(), R.anim.fragment_fade_exit)
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
                deleteImage()
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

        //Comments
        recycler.adapter=adapter
        getComments()
        sendbtn.setOnClickListener {
            if(message.text.toString()!=""){
                sendComment(message.text.toString())
            }
        }

    }

    private fun sendComment(comment: String) {
        //Sending a comment to firebase
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        val reference = FirebaseDatabase.getInstance().getReference("/post-comment/$postId/").push()

        val chatMmessage = Comment(reference.key,"",comment, currentUser, System.currentTimeMillis()/1000,"")
        reference.setValue(chatMmessage)
            .addOnSuccessListener {
                message.setText("")
                recycler.scrollToPosition(adapter.itemCount-1)
            }
    }

    private fun getComments() {
        val ref = FirebaseDatabase.getInstance().getReference("/post-comment/$postId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val coment = p0.getValue(Comment::class.java)?:return
                coment.current=userid!!
                coment.postid=postId
                comments[p0.key!!] = coment
                refreshRecycler()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {
                val coment = p0.getValue(Comment::class.java)?:return
                coment.current=userid!!
                coment.postid=postId
                comments.remove(p0.key!!)
                refreshRecycler()
            }
        })
    }
    val comments = HashMap<String, Comment>()
    private fun refreshRecycler(){
        adapter.clear()
        comments.values.forEach {
            adapter.add(CommentAdapter(it))
        }
    }
    class CommentAdapter(val commentobj: Comment): Item<GroupieViewHolder>(){
        var otherUser:User?=null

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.commenttext.setText(commentobj.commenttext)

            if(commentobj.userId==commentobj.current){
                viewHolder.itemView.deletecomment.visibility=View.VISIBLE
                viewHolder.itemView.deletecomment.setOnClickListener {
                    FirebaseDatabase.getInstance().getReference("/post-comment/${commentobj.postid}").child(commentobj.commentid.toString()).removeValue().addOnSuccessListener {
                    }
                }

            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/${commentobj.userId}")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    otherUser = p0.getValue(User::class.java)
                    viewHolder.itemView.usernamecomment.text = otherUser!!.username

                }

            })
        }
        override fun getLayout(): Int {
            return R.layout.comment_row
        }
    }

    private fun deleteImage() {
        val progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("Deleting Post")
        progressDialog.setMessage("This could take a few seconds")
        progressDialog.show()

        val query =
            FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
                .removeValue()
        query.addOnSuccessListener {
            val deleteQuery = FirebaseStorage
                .getInstance().reference.child("/PostImages/$imageUrl.jpg").delete()
            deleteQuery.addOnCompleteListener {
                progressDialog.dismiss()
                Navigation.findNavController(this.view!!).navigate(R.id.accountinfo)

            }
        }
    }

    private fun getPost(){
        val db = FirebaseDatabase.getInstance()
            .reference.child("Posts").child(postId)
        db.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    postObject = p0.getValue<Post>(Post::class.java)
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
                }else{
                    postDetails.visibility=View.INVISIBLE
                    warning.visibility=View.VISIBLE
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private fun getUserInfo(userId:String){
        val usersRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        usersRef.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    username?.text = (user?.username)
                    var currentUserId = FirebaseAuth.getInstance().uid
                    userPostname = (user?.username)

                    var isAdmin:Boolean?=null
                    val preferences = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                    if(preferences!=null){
                        isAdmin = preferences.getBoolean("isAdmin",false)

                    }

                    if(user?.uid == FirebaseAuth.getInstance().uid||isAdmin!!){
                        deleteButton.visibility=View.VISIBLE
                    }else{
                        deleteButton.visibility=View.GONE
                    }
                    if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                        Picasso.get().load(user.avatarUrl).into(profile_image!!);

                        download.setOnClickListener {
                            (activity as MainActivity).checkPermissions()
                        //    (activity as MainActivity).saveImageToInternalStorage(bitmap!!)
                        }

                    }


                }


            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }




}
