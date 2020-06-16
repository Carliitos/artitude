package art.projects.artitude

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import art.projects.artitude.Adapter.UserAdapter
import art.projects.artitude.Models.Tag
import art.projects.artitude.Models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_post_details.*
import kotlinx.android.synthetic.main.fragment_search_profiles.*
import kotlinx.android.synthetic.main.fragment_search_profiles.taglist

class searchProfiles : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_profiles, container, false)
    }
    private var userAdapter: UserAdapter?=null
    private var mUser: MutableList<User>?=null
    private var mTag: MutableList<String>?=null
    var typesearch:String?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchrecyler.setHasFixedSize(true)
        searchrecyler.layoutManager = LinearLayoutManager(context)

        mUser=ArrayList()
        mTag=ArrayList()
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
                    if(typesearch=="prof"){
                        searchUser(s.toString())
                    }else{
                        searchTags(s.toString().toLowerCase(), context)
                    }
                }
            }
        })
        hashtag.alpha=0.5f
        typesearch = "prof"
        profiles.setOnClickListener {
            typesearch = "prof"
            searchrecyler.visibility=View.VISIBLE
            taglist.visibility=View.GONE
            profiles.alpha=1f
            hashtag.alpha=0.5f
        }
        hashtag.setOnClickListener {
            typesearch = "hashtag"
            searchrecyler.visibility=View.GONE
            taglist.visibility=View.VISIBLE
            profiles.alpha=0.5f
            hashtag.alpha=1f
        }
    }

    private fun searchTags(tag: String, context: Context?) {
        val query = FirebaseDatabase.getInstance()
            .reference.child("Tags")
            .orderByKey()
            .startAt(tag).endAt(tag+"\uf0ff")
        query.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                mTag?.clear()

                for(snapshot in p0.children.iterator()){
                    val tagobj = snapshot.getValue(Tag::class.java)

                    if(tagobj!=null){
                        mTag?.add("#"+snapshot.key!!)
                    }
                }
                if(mTag!=null){
                    val adapter = ArrayAdapter(context!!, R.layout.item, mTag!!)
                    taglist.adapter = adapter
                    taglist.onItemClickListener = object : AdapterView.OnItemClickListener {

                        override fun onItemClick(parent: AdapterView<*>, view: View,
                                                 position: Int, id: Long) {

                            val itemValue = taglist.getItemAtPosition(position) as String
                            val itemwithothashtag = itemValue.removePrefix("#")
                            PostDetails.tagtext = itemwithothashtag
                            Navigation.findNavController(view).navigate(R.id.tagsView)

                        }
                    }
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

    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
    }
}
