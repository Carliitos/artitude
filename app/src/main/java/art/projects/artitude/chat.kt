package art.projects.artitude


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import art.projects.artitude.Models.Message
import art.projects.artitude.accountinfo.Companion.userobj
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.chat_row.*
import kotlinx.android.synthetic.main.chat_row.view.*
import kotlinx.android.synthetic.main.chat_row.view.textinside
import kotlinx.android.synthetic.main.chat_row_send.*
import kotlinx.android.synthetic.main.chat_row_send.view.*
import kotlinx.android.synthetic.main.fragment_chat.*
import java.sql.Timestamp
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class chat : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
    val adapter = GroupAdapter<GroupieViewHolder>()
    var userid:String?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userid = FirebaseAuth.getInstance().uid


        println("The user id for the chat is: "+userobj)

        (activity as? AppCompatActivity)?.supportActionBar?.title = userobj?.username
        recycler.adapter=adapter
        setupData()
        sendbtn.setOnClickListener {
            if(message.text.toString()!=""){
                sendMessage(message.text.toString(),"")
            }
        }
        file.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(context!!, this);
        }
    }

    private fun sendMessage(mensaje:String, image:String) {
        //Sending a message to firebase
        val currentUser = userid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$currentUser/${userobj!!.uid}").push()
        val toref = FirebaseDatabase.getInstance().getReference("/user-messages/${userobj!!.uid}/$currentUser").push()
        if(userobj!!.uid==null) return
        val chatMmessage = Message(reference.key!!, mensaje,currentUser!!, userobj!!.uid!!,image, System.currentTimeMillis()/1000)
        reference.setValue(chatMmessage)
            .addOnSuccessListener {
                message.setText("")
                recycler.scrollToPosition(adapter.itemCount-1)
            }
        toref.setValue(chatMmessage)
        FirebaseDatabase.getInstance().getReference("/latest-messages/$currentUser/${userobj!!.uid}")
            .setValue(chatMmessage)
        FirebaseDatabase.getInstance().getReference("/latest-messages/${userobj!!.uid}/$currentUser")
            .setValue(chatMmessage)
    }

    private fun setupData() {
        recycler.adapter=adapter
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$userid/${userobj!!.uid}")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)
                if(chatMessage!=null){
                    if(chatMessage.fromId == userid){
                        adapter.add(ChatToItem(chatMessage.text,chatMessage.imgurl))
                    }else{
                        adapter.add(ChatFromItem(chatMessage.text,chatMessage.imgurl))
                        if(userobj!!.avatarUrl!=""&&userobj!!.avatarUrl!=null){
                            println("The image: "+userobj!!.avatarUrl)
                        }
                    }
                }
                recycler.scrollToPosition(adapter.itemCount-1)
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
        })
    }

    class ChatFromItem(val text: String, val img:String): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_row
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.textinside.text=text
            if(userobj!!.avatarUrl!!.isNotEmpty()){
                Picasso.get().load(userobj!!.avatarUrl!!).into(viewHolder.itemView.bubblerecievedimage)
            }
            if(text!=""){
                viewHolder.itemView.textinside.text=text
            }else{
                println("The recieved images are: "+img)
                Picasso.get().load(img).into(viewHolder.itemView.bubbleimgrec!!);
                viewHolder.itemView.textinside.visibility=View.GONE
            }
        }
    }
    class ChatToItem(val text: String, val img:String): Item<GroupieViewHolder>(){
        override fun getLayout(): Int {
            return R.layout.chat_row_send
        }

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            if(text!=""){
                viewHolder.itemView.textinside.text=text
            }else{
                Picasso.get().load(img).into(viewHolder.itemView.bubbleimg!!);
                viewHolder.itemView.textinside.visibility=View.GONE
            }
        }
    }
    var selectedImage: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = CropImage.getActivityResult(data)
        if (resultCode == RESULT_OK &&  requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val resultUri = result.uri
            selectedImage = resultUri
            if(selectedImage!=null){
                val filename = UUID.randomUUID();
                val ref = FirebaseStorage.getInstance().getReference("/chat-images/$filename")
                ref.putFile(selectedImage!!)
                    .addOnSuccessListener {
                        Log.d("Register","Successfully uploaded image")
                        ref.downloadUrl.addOnSuccessListener {
                            sendMessage("",it.toString())
                        }
                    }
            }

        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            val error = result.error
            Toast.makeText(this.requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
        }
    }


}
