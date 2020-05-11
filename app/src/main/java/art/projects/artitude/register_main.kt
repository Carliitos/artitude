package art.projects.artitude


import android.app.Activity
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
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide();



        registerbtn.setOnClickListener {
            if(email.text.toString().isEmpty()||password.text.toString().isEmpty()){
                errorText.text= "You must fill in all the fields ⚠️"
            }else{
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
            var intent = Intent(Intent.ACTION_PICK);
            intent.type="image/*"
            startActivityForResult(intent,0)
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
        val user = User(userId,username.text.toString(), avatar)

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity","Register complete")
                // ...
            }
            .addOnFailureListener {
                // Write failed
                // ...
            }




    }

    var selectedImage: Uri?=null
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


// Initialize Firebase Auth

}
