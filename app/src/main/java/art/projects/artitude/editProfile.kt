package art.projects.artitude


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class editProfile : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    var updatedUser:User? = null
    var selectedImage: Uri?=null
    var firstTime = true;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var usersRef = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().uid.toString())
        usersRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user = p0.getValue<User>(User::class.java)
                    username?.setText(user?.username)
                    bio?.setText(user?.bio)
                    if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                        Picasso.get().load(user.avatarUrl).into(profile_image!!);
                    }else if(profile_image!=null){
                        Picasso.get().load(R.drawable.profilenoimage).into(profile_image!!);
                    }
                    if(firstTime){
                        updatedUser = User(user?.uid, user?.username, user?.bio, user?.avatarUrl)
                        firstTime = false
                    }

                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        update.setOnClickListener {
            //uploads the image and then updates the user
            uploadUserImage()
        }
        imageSelect.setOnClickListener {
            var intent = Intent(Intent.ACTION_PICK);
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
        deleteImg.setOnClickListener {
            updatedUser?.avatarUrl = null
            Picasso.get().load(R.drawable.profilenoimage).into(profile_image!!);
        }
    }

    private fun updateUser(updatedImg:String?) {
        var database = FirebaseDatabase.getInstance().reference
        updatedUser?.username = username.text.toString()
        updatedUser?.bio = bio.text.toString()
        updatedUser?.avatarUrl = updatedImg
        database.child("users").child(updatedUser?.uid.toString()).setValue(updatedUser)
        Navigation.findNavController(view!!).navigate(R.id.accountinfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode == Activity.RESULT_OK&&data!=null){
            //Selecting the profile picture
            selectedImage = data.data!!
            var imageStream = context?.contentResolver?.openInputStream(selectedImage!!);



            var bitmap = BitmapFactory.decodeStream(imageStream)
            bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, false)
            profile_image.setImageBitmap(bitmap) //Scaled
            imageSelect.alpha=0f

        }
    }
    private fun uploadUserImage() {
        val filename = UUID.randomUUID();
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedImage!!)
            .addOnSuccessListener {
                Log.d("Register","Successfully uploaded image")
                ref.downloadUrl.addOnSuccessListener {
                    updateUser(it.toString())
                }
            }
        }
}
