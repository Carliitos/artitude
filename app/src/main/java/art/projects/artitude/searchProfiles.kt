package art.projects.artitude

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import art.projects.artitude.Adapter.UserAdapter
import art.projects.artitude.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search_profiles.*

class searchProfiles : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_profiles, container, false)
    }
    private var userAdapter: UserAdapter?=null
    private var mUser: MutableList<User>?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchrecyler.setHasFixedSize(true)
        searchrecyler.layoutManager = LinearLayoutManager(context)

        mUser=ArrayList()
        userAdapter=context?.let { UserAdapter(
            it,
            mUser as ArrayList<User>,
            this.findNavController()
        ) }
        searchrecyler.adapter=userAdapter

        searchedittext.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int,
                                       before: Int,
                                       count: Int) {
                if(searchedittext.text.toString()!=""){
                    searchedittext.visibility=View.VISIBLE
                    //findUsers()
                    searchUser(s.toString())
                }
            }
        })
    }

    private fun searchUser(username:String) {
        val query = FirebaseDatabase.getInstance()
            .reference.child("users")
            .orderByChild("username")
            .startAt(username).endAt(username+"\uf0ff")
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mUser?.clear()
                for(snapshot in p0.children){
                    val user = snapshot.getValue(User::class.java)
                    if(user!=null){
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
        })
    }

    private fun findUsers(){
        val users = FirebaseDatabase.getInstance().reference.child("users")
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(searchedittext.text.toString()!=""&&searchedittext!=null){
                    mUser?.clear()
                    for(snapshot in p0.children){
                        val user = snapshot.getValue(User::class.java)
                        if(user!=null){
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
    }
}
