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
        getPosts()

        rowItems = ArrayList<cards>()

        //rowItems = rowItems?.plus(cards("Carlos","Hellooo","https://ichef.bbci.co.uk/news/410/cpsprodpb/150EA/production/_107005268_gettyimages-611696954.jpg"))

        arrayAdapter = arrayAdapter(
            this.requireContext(),
            R.layout.item,
            rowItems!!
        )

        val flingContainer = frame as SwipeFlingAdapterView


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
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(this@MyActivity, "Left!", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                startParticles()
                imagesss.alpha=0f
                //Toast.makeText(this@MyActivity, "Right!", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
                //rowItems!!.add("XML " + itemsInAdapter)
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
        val posts = FirebaseDatabase.getInstance().reference.child("Posts")
        posts.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    for(snapshot in p0.children){
                        val post = snapshot.getValue(Post::class.java)!!
                            //(rowItems as ArrayList<Post>).add(post)
                            val item= cards(
                                post.user,
                                post.description,
                                post.imageUrl,
                                post.postid
                            )

                        (rowItems as ArrayList<cards>).add(item)
                        //    rowItems = rowItems?.plus(item)
                        //Collections.reverse(postList)
                        //imageAdapter!!.notifyDataSetChanged()
                    }
                            arrayAdapter!!.notifyDataSetChanged()
                }
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
