package art.projects.artitude


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

/**
 * A simple [Fragment] subclass.
 */
class login : Fragment() {
    private lateinit var auth: FirebaseAuth// ...
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.actionBar?.hide();
        toRegister.setOnClickListener {
            Navigation.findNavController(this.view!!).navigate(R.id.toRegister)
        }
        //Gets FirebaseAuth Instance
        auth = FirebaseAuth.getInstance()
        //Login button listener
        loginBtn.setOnClickListener {
            if(email.text.toString().isNotEmpty()&&password.text.toString().isNotEmpty()){
                auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, Navigates to the main view
                            val user = auth.currentUser
                            Navigation.findNavController(this.view!!).navigate(R.id.swiper)
                        } else {
                            // If sign in fails, display a message to the user.
                            errorText.text="An error occured ⚠ ${task.exception}"
                        }
                    }
            }else{
                errorText.text= "You must fill in all the fields ⚠️"
            }
        }

        //Hiding the action bar
        (activity as MainActivity).hideActionBar()
    }

}

