package art.projects.artitude


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import art.projects.artitude.Adapter.PostAdapter
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.Tag
import art.projects.artitude.PostDetails.Companion.tagtext
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_tags_view.*

/**
 * A simple [Fragment] subclass.
 */
class tagsView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tags_view, container, false)
    }
    private var userAdapter: PostAdapter?=null
    private var mPost: MutableList<Post>?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "#"+tagtext
        tagsRecycler.setHasFixedSize(true)
        tagsRecycler.layoutManager = LinearLayoutManager(context)
        getTopPosts()
        mPost=ArrayList()
        userAdapter=context?.let { PostAdapter(it,mPost as ArrayList<Post>,true,this.findNavController()) }
        tagsRecycler.adapter=userAdapter
        }

    private fun getTopPosts(){
        val posts =
            FirebaseDatabase.getInstance().reference
                .child("Tags").child(tagtext!!)
                .limitToLast(10)
        posts.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                mPost?.clear()
                for(snapshot in p0.children){
                    val user = snapshot.getValue(Post::class.java)
                    if(user!=null){
                        mPost?.add(user)
                    }
                }
                mPost!!.reverse()
                userAdapter?.notifyDataSetChanged()


            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

}
