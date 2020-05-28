package art.projects.artitude


import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import art.projects.artitude.Models.User
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


        uploadbtn.setOnClickListener {
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
        //SET DARK MODE
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

    private fun uploadUserImage() {
        //The user chose a profile image
        if(selectedImage!=null){
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
        //The user didn't pick any image
        else{
            saveUserToFirebase("")
        }
    }
    private fun saveUserToFirebase(avatar:String) {
        val database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val user = User(userId, username.text.toString(), "", avatar,false)

        database.child("users").child(userId).setValue(user).addOnSuccessListener {
            progressDialog!!.dismiss()
            Navigation.findNavController(this.view!!).navigate(R.id.swiper)
        }.addOnFailureListener {
            Toast.makeText(this.requireContext(),it.message,Toast.LENGTH_SHORT).show()
        }
    }

    var selectedImage: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = CropImage.getActivityResult(data)
        if (resultCode == RESULT_OK &&  requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val resultUri = result.uri
            selectedImage = resultUri
            Picasso.get().load(resultUri).into(profile_image!!);
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            val error = result.error
            Toast.makeText(this.requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
        }
    }
}
