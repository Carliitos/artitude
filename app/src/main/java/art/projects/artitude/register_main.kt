package art.projects.artitude


import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_register_main.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class register_main : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_register_main, container, false)


    }
    var progressDialog:ProgressDialog?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide();



        registerbtn.setOnClickListener {
            if(email.text.toString().isEmpty()||password.text.toString().isEmpty()){
                errorText.text= "You must fill in all the fields ⚠️"
            }else{
                progressDialog = ProgressDialog(this.requireContext())
                progressDialog!!.setTitle("Registering...")
                progressDialog!!.setMessage("This could take a few seconds")
                progressDialog!!.show()
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = it.result?.user
                        uploadUserImage()
                    }
                }
                .addOnFailureListener {
                    errorText.text= "An error occurred: ${it.message} ⚠️"
                }
        }
        }
        goToLogin.setOnClickListener {
            Navigation.findNavController(this.view!!).navigate(R.id.toLogin)
        }
        //Hiding the action bar
        (activity as MainActivity).hideActionBar()
        imageSelect.setOnClickListener {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(context!!, this);
        }
    }

    private fun uploadUserImage() {

        val filename = UUID.randomUUID();
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedImage!!)
            .addOnSuccessListener {
                Log.d("Register","Successfully uploaded image")
                ref.downloadUrl.addOnSuccessListener {
                    it.toString()
                    Log.d("RegisterActivity","File in: $it")
                    saveUserToFirebase(it.toString()) //Image path in firebase
                }
            }
    }

    private fun saveUserToFirebase(avatar:String) {
        var database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val user = User(userId,username.text.toString(), "", avatar)

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Register complete")
                // ...
                progressDialog!!.dismiss()
                Navigation.findNavController(this.view!!).navigate(R.id.swiper)
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }




    }

    var selectedImage: Uri?=null
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


// Initialize Firebase Auth

}
