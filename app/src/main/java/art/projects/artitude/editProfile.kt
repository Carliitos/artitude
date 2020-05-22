package art.projects.artitude


import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
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

    var updatedUser: User? = null
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
                        updatedUser = User(
                            user?.uid,
                            user?.username,
                            user?.bio,
                            user?.avatarUrl
                        )
                        firstTime = false
                    }

                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        update.setOnClickListener {
            //uploads the image and then updates the user
            if(selectedImage!=null){
                uploadUserImage()
            }else{
                updateUser("")
            }
        }
        imageSelect.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(context!!, this);
        }
        deleteImg.setOnClickListener {
            updatedUser?.avatarUrl = null
            Picasso.get().load(R.drawable.profilenoimage).into(profile_image!!);
        }
        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            (activity as MainActivity).hideBottomActionBar()
            (activity as MainActivity).hideActionBar()
            Navigation.findNavController(view).navigate(R.id.login)

        }
        val preferences = context?.getSharedPreferences("STETIC", Context.MODE_PRIVATE)
        if(preferences!=null){
            nightmode.isChecked = preferences.getBoolean("darkmode",false)

        }
        nightmode.setOnCheckedChangeListener { _, isChecked ->
            val editor = this.context!!.getSharedPreferences("STETIC", Context.MODE_PRIVATE).edit()
            if(isChecked){
                editor.putBoolean("darkmode",true)
            }else{
                editor.putBoolean("darkmode",false)
            }
            editor.apply()
            (activity as MainActivity).checkTheme()
        }
    }

    private fun updateUser(updatedImg:String?) {
        var database = FirebaseDatabase.getInstance().reference
        updatedUser?.username = username.text.toString()
        updatedUser?.bio = bio.text.toString()
        if(updatedImg!=""){
            updatedUser?.avatarUrl = updatedImg
        }
        database.child("users").child(updatedUser?.uid.toString()).setValue(updatedUser)

        Navigation.findNavController(view!!).navigate(R.id.accountinfo)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val result = CropImage.getActivityResult(data)
        if (resultCode == RESULT_OK &&  requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val resultUri = result.uri
            selectedImage = resultUri
            //image.setImageURI(resultUri)
            Picasso.get().load(resultUri).into(profile_image!!);
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            val error = result.error
        }
    }
    private fun uploadUserImage() {
        val progressDialog = ProgressDialog(this.requireContext())
        progressDialog.setTitle("Updating")
        progressDialog.setMessage("This could take a few seconds")
        progressDialog.show()
        val filename = UUID.randomUUID();
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedImage!!)
            .addOnSuccessListener {
                Log.d("Register","Successfully uploaded image")
                ref.downloadUrl.addOnSuccessListener {
                    progressDialog.dismiss()
                    updateUser(it.toString())
                }
            }
        }
}
