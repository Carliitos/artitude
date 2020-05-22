package art.projects.artitude


import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ListView
import androidx.navigation.Navigation
import art.projects.artitude.Adapter.arrayAdapter
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.fragment_swiper.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


/**
 * A simple [Fragment] subclass.
 */
class swiper : Fragment() {
    var cards: cards?=null
    var arrayAdapter: arrayAdapter? = null
    var userId:String?=null
    var MAX_PARTICLE_SIZE = 0f
    var MIN_PARTICLE_SIZE = 0f
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swiper, container, false)
    }
    var listview:ListView?=null
    var rowItems:List<cards>?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = this.requireContext()
        val view = this.view
        (activity as MainActivity).showBottomActionBar()
        (activity as MainActivity).showActionBar()
        screenSize()
        userId =FirebaseAuth.getInstance().uid
        getPosts()

        rowItems = ArrayList<cards>()

        //rowItems = rowItems?.plus(cards("Carlos","Hellooo","https://ichef.bbci.co.uk/news/410/cpsprodpb/150EA/production/_107005268_gettyimages-611696954.jpg"))

        arrayAdapter = arrayAdapter(
            this.requireContext(),
            R.layout.item,
            rowItems!!
        )

        val flingContainer = frame as SwipeFlingAdapterView

        val db = FirebaseDatabase.getInstance().reference

        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun onScroll(p0: Float) {
                println(p0)
                if(p0>0){
                    imagesss.setImageResource(R.drawable.heart)
                }else{
                    imagesss.setImageResource(R.drawable.no)
                }
                imagesss.alpha = (p0.absoluteValue)-0.1f

                //imagesss.layoutParams.width = (600*p0).toInt()
                //imagesss.layoutParams.height = (600*p0).toInt()
                //imagesss.layoutParams.width = (p0*100).toInt()
                //println((p0*100).toInt())
            }

            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                (rowItems as ArrayList<cards>).removeAt(0)

                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                imagesss.alpha=0f
                val postinfo = HashMap<String, Any>()
                postinfo["description"] = (dataObject as cards).name.toString()

                postinfo["tags"]= dataObject.tags.toString()
                //db.child("Rated").child(userId!!).child("disliked").child(dataObject.postId!!).updateChildren(postinfo)
                db.child("Posts").child(dataObject.postId!!).child("rated").child("disliked").child(userId!!).setValue(true)
            }

            override fun onRightCardExit(dataObject: Any) {
                startParticles()
                imagesss.alpha=0f
                val postinfo = HashMap<String, Any>()
                postinfo["description"] = (dataObject as cards).name.toString()
                postinfo["tags"]= dataObject.tags.toString()
                //db.child("Rated").child(userId!!).child("liked").child(dataObject.postId!!).updateChildren(postinfo)
                db.child("Posts").child(dataObject.postId!!).child("rated").child("liked").child(userId!!).setValue(true)

                //Increases the times that it has been liked
                val increment = db.child("Posts").child(dataObject.postId!!).child("timesliked")
                increment.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()&&p0.getValue(Int::class.java)!=null){
                            val amount = p0.getValue(Int::class.java)
                            println("THE VALUE IS: "+amount)
                            db.child("Posts").child(dataObject.postId!!).child("timesliked").setValue(amount!! +1)
                        }else{
                            db.child("Posts").child(dataObject.postId!!).child("timesliked").setValue(1)
                        }

                    }


                    override fun onCancelled(p0: DatabaseError) {

                    }
                })
            }


            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
                //rowItems!!.add("XML " + itemsInAdapter)
                //rowItems=ArrayList<cards>()
                //getPosts()
                //rowItems=ArrayList<cards>()
                arrayAdapter!!.notifyDataSetChanged()
                Log.d("LIST", "notified")
                //i++
            }

        })
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(object : SwipeFlingAdapterView.OnItemClickListener {
            override fun onItemClicked(itemPosition: Int, dataObject: Any) {

                val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                editor.putString("postId",(dataObject as cards).postId)
                editor.apply()
                Navigation.findNavController(view!!).navigate(R.id.postDetails)
            }
        })
    }
    private fun getPosts(){
        rowItems=ArrayList<cards>()
        val posts = FirebaseDatabase.getInstance().reference.child("Posts").limitToFirst(50)
        posts.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(snapshot in p0.children){
                        if(!snapshot.child("rated").child("liked").hasChild((userId!!))&&!snapshot.child("rated").child("disliked").hasChild((userId!!))){
                            val post = snapshot.getValue(Post::class.java)!!
                                //(rowItems as ArrayList<Post>).add(post)
                            val item= cards(
                                    post.user,
                                    post.description,
                                    post.imageUrl,
                                    post.postid,
                                    post.tags

                                )

                            (rowItems as ArrayList<cards>).add(item)

                        }

                    }

                }
                            if(up2date!=null){
                                if(rowItems!!.isEmpty()){
                                    up2date!!.visibility=View.VISIBLE
                                }else{
                                    up2date!!.visibility=View.GONE
                                }

                            }
                            arrayAdapter!!.notifyDataSetChanged()
            }


            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
    private fun startParticles(){
        try {
            ParticleSystem(this.activity, 40, R.drawable.particleheart, 3000)
                .setSpeedRange(0.1f, 0.3f)
                .setAcceleration(0.00005f, 270)
                .setRotationSpeed(144f)
                .setFadeOut(3000)
                .setScaleRange(MIN_PARTICLE_SIZE+0.3f, MAX_PARTICLE_SIZE+0.3f)
                .oneShot(imagesss, 20)

        } catch (e: Exception) {
            // Must be safe
        }
    }
    fun screenSize(){
        //DETERMINE SCREEN SIZE
        val screenLayout = resources.displayMetrics.densityDpi
        if (screenLayout == DisplayMetrics.DENSITY_LOW) {
            MAX_PARTICLE_SIZE = 0.002f
            MIN_PARTICLE_SIZE = 0.05f
            println("The screen size is small")
        } else if (screenLayout == DisplayMetrics.DENSITY_MEDIUM) {
            MAX_PARTICLE_SIZE = 0.04f
            MIN_PARTICLE_SIZE = 0.8f
            println("The screen size is normal")


        } else if (screenLayout == DisplayMetrics.DENSITY_HIGH) {
            MAX_PARTICLE_SIZE = 0.04f
            MIN_PARTICLE_SIZE = 0.1f
            println("The screen size is large")


        } else if (screenLayout == DisplayMetrics.DENSITY_XHIGH){
            MAX_PARTICLE_SIZE = 0.06f
            MIN_PARTICLE_SIZE = 1.2f
            println("The screen size is xlarge")


        }else if (screenLayout == DisplayMetrics.DENSITY_XXHIGH){
            MAX_PARTICLE_SIZE = 0.04f
            MIN_PARTICLE_SIZE = 0.8f
            println("The screen size is undefined")

        }
        else if (screenLayout == DisplayMetrics.DENSITY_XXXHIGH){
            MAX_PARTICLE_SIZE = 0.04f
            MIN_PARTICLE_SIZE = 0.8f
            println("The screen size is undefined")

        }
    }


}
