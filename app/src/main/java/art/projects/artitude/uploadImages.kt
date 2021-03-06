package art.projects.artitude


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_upload_images.*
import java.util.*
import kotlin.collections.HashMap



class uploadImages : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_images, container, false)
    }
    var selectedImage: Uri? = null;
    var storageRef:StorageReference? = null;
    var mContext:Context?=null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext=this.context
        storageRef = FirebaseStorage.getInstance().reference.child("PostImages")

        CropImage.activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1, 1)
            .start(context!!, this);

        uploadbtn.setOnClickListener {
            uploadImage()
        }
    }

    private fun uploadImage() {
        when{
            selectedImage == null -> Toast.makeText(this.requireContext(), "An image must be selected first", Toast.LENGTH_SHORT).show()
            else->{
                val progressDialog = ProgressDialog(this.requireContext())
                progressDialog.setTitle("Creating Post")
                progressDialog.setMessage("This could take a few seconds")
                progressDialog.show()

                val filename = UUID.randomUUID();
                val fileRef = storageRef!!.child("$filename.jpg")
                val uploadTask = fileRef.putFile(selectedImage!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot,Task<Uri>>{ task ->
                    if(!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener {
                    //When the image has been uploaded
                    val userId=FirebaseAuth.getInstance().uid.toString()
                    if(it.isSuccessful){
                        val imageUrl = it.result
                        val db = FirebaseDatabase
                            .getInstance().reference.child("Posts")
                        val postId = db.push().key
                        val postinfo = HashMap<String, Any>()
                        postinfo["postid"] = postId!!.toString()
                        postinfo["description"] = description.text.toString()
                        postinfo["tags"]= tags.text.toString().toLowerCase()
                        postinfo["timesliked"]=0
                        postinfo["user"] = userId
                        postinfo["imageUrl"] =imageUrl.toString()

                        val tags = tags.text.toString().toLowerCase().split(" ").toTypedArray()

                        uploadTags(tags, postinfo, postId)

                        db.child(postId).updateChildren(postinfo)

                        progressDialog.dismiss()

                        val editor = mContext!!.getSharedPreferences("USER", Context.MODE_PRIVATE).edit()
                        editor.putString("userid",userId)
                        editor.apply()
                        Navigation.findNavController(this.view!!).navigate(R.id.accountinfo)
                    }
                }

            }
        }
    }

    private fun uploadTags(
        tags: Array<String>,
        post: HashMap<String, Any>,
        postId: String
    ) {
        val db = FirebaseDatabase
            .getInstance().reference.child("Tags")
        for(tag: String in tags){
            db.child(tag).child(postId).setValue(post)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK && requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                val resultUri = result.uri
                selectedImage = resultUri
                Picasso.get().load(resultUri).into(image!!);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this.requireContext(),error.toString(),Toast.LENGTH_SHORT).show()
            }
    }
}
