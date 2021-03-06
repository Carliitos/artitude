package art.projects.artitude


import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.navigation.Navigation
import art.projects.artitude.Adapter.CardAdapter
import art.projects.artitude.Models.Post
import art.projects.artitude.Models.cards
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.plattysoft.leonids.ParticleSystem
import kotlinx.android.synthetic.main.fragment_swiper.*
import kotlin.collections.ArrayList
import kotlin.math.absoluteValue


class swiper : Fragment() {
    var CardAdapter: CardAdapter? = null
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
    var rowItems:List<cards>?=null
    var animation:Animation?=null

    var isReported:Boolean?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = this.requireContext()

        (activity as MainActivity).showBottomActionBar()
        (activity as MainActivity).showActionBar()
        //Set the particles size, so they adjust to the screen size
        screenSize()

        openchat.setOnClickListener {
            Navigation.findNavController(view!!).navigate(R.id.latestMessages)
        }

        userId =FirebaseAuth.getInstance().uid

        //Background fade in animation
        animation = AnimationUtils.loadAnimation(this.requireContext(), R.anim.fragment_fade_enter)

        getTags()

        rowItems = ArrayList()

        val preferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if(!preferences.getBoolean("tutorial",false)){
            var timesClicked = 0
            tutorial.visibility=View.VISIBLE
            tutorial.setOnClickListener {
                timesClicked++
                image.setImageResource(R.drawable.left)
                if(timesClicked==2){
                    tutorial.visibility=View.GONE
                    val editor = this.context!!.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                    editor.putBoolean("tutorial",true)
                    editor.apply()
                }
            }
        }
        isReported=false;
        report.setOnClickListener {


                reportWarning.visibility=View.VISIBLE

            confirm.setOnClickListener {
                confirmReport.visibility=View.VISIBLE
                isReported = true;
                reportWarning.visibility=View.GONE
                report.alpha=1f
                report.setImageResource(R.drawable.warningred)
            }
            deny.setOnClickListener {
                isReported = false;
                report.setImageResource(R.drawable.warning)
                reportWarning.visibility=View.GONE
            }
        }

    }
    var tags:ArrayList<String>?=null
    private fun getPosts(){
        rowItems=ArrayList()

        for(tag in tags!!){
            val posts = FirebaseDatabase.getInstance().reference
                .child("Posts")
                .orderByChild("tags")
                .startAt(tag).endAt(tag+"\uf8ff")
                .limitToFirst(2);
            posts.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        for(snapshot in p0.children){
                            if(!snapshot.child("rated")
                                    .child("liked")
                                    .hasChild((userId!!))&&!snapshot
                                    .child("rated")
                                    .child("disliked").hasChild((userId!!))){
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
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
        }

        val postsnotags =
            FirebaseDatabase.getInstance().reference.child("Posts").limitToFirst(20);
        postsnotags.addListenerForSingleValueEvent(object: ValueEventListener {
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
                            if(!rowItems!!.contains(item)){
                                (rowItems as ArrayList<cards>).add(item)
                            }
                        }
                    }
                }
                rowItems!!.reversed()
                CardAdapter?.notifyDataSetChanged()
                checkEmpty()
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
        startAdapter()


    }

    private fun getTags() {
        tags = ArrayList()
        //Gets the liked tags
        FirebaseDatabase.getInstance().reference.child("users").child(userId!!).child("liked")
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        for(snapshot in p0.children){
                            val post = snapshot.getValue(Post::class.java)!!
                            tags?.add(post.tags.toString())
                        }
                    }
                    getPosts()
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
                .oneShot(likedislikeimg, 20)

        } catch (e: Exception) {
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

    private fun startAdapter(){
        CardAdapter = CardAdapter(
            this.requireContext(),
            R.layout.card_item,
            rowItems!!
        )
        val flingContainer = frame as SwipeFlingAdapterView
        val db = FirebaseDatabase.getInstance().reference
        //set the listener and the startAdapter
        flingContainer.adapter = CardAdapter;
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun onScroll(p0: Float) {
                if(p0>0){
                    likedislikeimg.setImageResource(R.drawable.heart)
                }else{
                    likedislikeimg.setImageResource(R.drawable.no)
                }
                likedislikeimg.alpha = (p0.absoluteValue)-0.1f
            }
            override fun removeFirstObjectInAdapter() {
                (rowItems as ArrayList<cards>).removeAt(0)
                CardAdapter!!.notifyDataSetChanged()
                checkEmpty()
            }

            override fun onLeftCardExit(dataObject: Any) {
                likedislikeimg.alpha=0f
                confirmReport.visibility=View.GONE
                report.setImageResource(R.drawable.warning)
                val postinfo = HashMap<String, Any>()
                postinfo["description"] = (dataObject as cards).name.toString()
                postinfo["tags"]= dataObject.tags.toString()
                db.child("Posts")
                    .child(dataObject.postId!!)
                    .child("rated").child("disliked")
                    .child(userId!!).setValue(true)

                increaseTimesVoted(dataObject, db)
                println("is reported "+isReported)
                if(isReported!!){
                    val increment = db.child("Posts")
                        .child(dataObject.postId!!).child("timesreported")
                    increment.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if(p0.exists()&&p0.getValue(Int::class.java)!=null){
                                println("RESPORTEEEEEEEEEEEEEEEEEED")
                                val amount = p0.getValue(Int::class.java)
                                db.child("Posts").child(dataObject.postId!!)
                                    .child("timesreported").setValue(amount!! +1)
                                shouldbedeleted(amount+1, dataObject, db)
                            }else{
                                db.child("Posts").child(dataObject.postId!!)
                                    .child("timesreported").setValue(1)
                            }

                        }
                        override fun onCancelled(p0: DatabaseError) {
                        }
                    })
                }
            }

            override fun onRightCardExit(dataObject: Any) {
                startParticles()
                confirmReport.visibility=View.GONE
                report.setImageResource(R.drawable.warning)
                likedislikeimg.alpha=0f
                val postinfo = HashMap<String, Any>()
                postinfo["description"] = (dataObject as cards).name.toString()
                postinfo["tags"]= dataObject.tags.toString()
                postinfo["imageUrl"]= dataObject.profileImageUrl.toString()
                postinfo["postid"]= dataObject.postId.toString()


                db.child("Posts").child(dataObject.postId!!)
                    .child("rated").child("liked")
                    .child(userId!!).setValue(true)
                db.child("users").child(userId!!)
                    .child("liked")
                    .child(dataObject.postId!!)
                    .setValue(postinfo)

                //Increases the times that the post has been liked
                val increment = db.child("Posts")
                    .child(dataObject.postId!!).child("timesliked")
                increment.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.exists()&&p0.getValue(Int::class.java)!=null){
                            val amount = p0.getValue(Int::class.java)
                            db.child("Posts").child(dataObject.postId!!)
                                .child("timesliked").setValue(amount!! +1)
                        }else{
                            db.child("Posts").child(dataObject.postId!!)
                                .child("timesliked").setValue(1)
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                    }
                })
                increaseTimesVoted(dataObject, db)
            }
            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                CardAdapter!!.notifyDataSetChanged()
            }
        })
        flingContainer.setOnItemClickListener { itemPosition, dataObject ->
            val editor = context!!.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            editor.putString("postId",(dataObject as cards).postId)
            editor.apply()
            Navigation.findNavController(view!!).navigate(R.id.postDetails)
        }
    }

    private fun shouldbedeleted(timesreported: Int, dataObject: cards, db: DatabaseReference) {
        val voted = db.child("Posts")
            .child(dataObject.postId!!).child("timesvoted")
        voted.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()&&p0.getValue(Int::class.java)!=null){
                    val timesvoted = p0.getValue(Int::class.java)
                    //Si se ha votado más de X veces y el porcentaje de reportes es superior a X se elimina el post
                    if(timesvoted!!>5){
                        val porcentajeReport = (timesreported*100)/timesvoted!!
                        if(porcentajeReport>=25){
                            val query =
                                FirebaseDatabase.getInstance().reference.child("Posts").child(dataObject.postId!!)
                                    .removeValue()
                            query.addOnSuccessListener {
                                val deleteQuery = FirebaseStorage
                                    .getInstance().reference.child("/PostImages/${dataObject.profileImageUrl}").delete()
                                deleteQuery.addOnCompleteListener {
                                    println("Image deleted...")
                                }
                            }
                        }
                    }

                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

    private fun increaseTimesVoted(dataObject: cards, db: DatabaseReference) {
        //Increases the times that the post has been voted
        val voted = db.child("Posts")
            .child(dataObject.postId!!).child("timesvoted")
        voted.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()&&p0.getValue(Int::class.java)!=null){
                    val amount = p0.getValue(Int::class.java)
                    db.child("Posts").child(dataObject.postId!!)
                        .child("timesvoted").setValue(amount!! +1)
                }else{
                    db.child("Posts").child(dataObject.postId!!)
                        .child("timesvoted").setValue(1)
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun checkEmpty(){
        if(up2date!=null){
            if(rowItems!!.isEmpty()){
                up2date!!.visibility=View.VISIBLE
                stars.startAnimation(animation)
                stars.visibility=View.VISIBLE
            }else{
                up2date!!.visibility=View.GONE
                stars.visibility=View.INVISIBLE
            }
        }
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
