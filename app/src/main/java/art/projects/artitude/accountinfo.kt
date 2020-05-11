package art.projects.artitude


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.fragment_accountinfo.*
import kotlinx.android.synthetic.main.fragment_accountinfo.profile_image
import kotlinx.android.synthetic.main.fragment_edit_profile.*


/**
 * A simple [Fragment] subclass.
 */
class accountinfo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_accountinfo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ref = FirebaseDatabase.getInstance().getReference("/users");

        var usersRef = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().uid.toString())
        usersRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        val user = p0.getValue<User>(User::class.java)
                        userProfName?.text = (user?.username)
                        println("Pic is "+user?.avatarUrl)
                        if(profile_image!=null && user?.avatarUrl!!.isNotEmpty()){
                            Picasso.get().load(user.avatarUrl).into(profile_image!!);
                        }
                        bios?.text = user?.bio

                    }


            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        edit.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.editProfile)
        }
    }

}


