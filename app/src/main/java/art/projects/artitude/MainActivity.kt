package art.projects.artitude

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    var navController: NavController by Delegates.notNull()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hideBottomActionBar()


        //Bottom menu
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            showActionBar()
            when(item.itemId) {
                R.id.item_home -> {
                    navController.navigate(R.id.swiper)
                    hideBackButton()
                    true
                }
                R.id.item_account -> {
                    val editor = this.getSharedPreferences("USER",Context.MODE_PRIVATE).edit()
                    editor.putString("userid",FirebaseAuth.getInstance().uid.toString())
                    editor.apply()
                    navController.navigate(R.id.accountinfo)
                    hideBackButton()
                    true
                }
                R.id.item_addImg -> {
                    navController.navigate(R.id.uploadImages)
                    hideBackButton()
                    true
                }
                R.id.search->{
                    navController.navigate(R.id.searchProfiles)
                    hideBackButton()
                    true
                }
                R.id.item_trends->{
                    navController.navigate(R.id.trends)
                    hideBackButton()
                    true
                }
                else -> false
            }
        }


        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
        NavigationUI.setupActionBarWithNavController(this, navController)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser!=null){

            isAdmin(currentUser.uid)

            //DEEP LINKING
            val data: Uri? = intent?.data
            if(data!=null) {
                val params = data.pathSegments
                if(params[0].equals("post")){
                    val editor =
                        this.applicationContext
                            .getSharedPreferences("PREFS",Context.MODE_PRIVATE)
                            .edit()
                    editor.putString("postId",params[1])
                    editor.apply()
                    navController.navigate(R.id.postDetails)
                }
            }else{
                navController.navigate(R.id.swiper)
            }
        }
        checkTheme()
    }
    fun hideBackButton(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setHomeButtonEnabled(false)
    }
    fun checkTheme() {
        //Checks the userprefs so the background changes
        val preferences = this.
            getSharedPreferences("STETIC", Context.MODE_PRIVATE)
        if(preferences!=null){
            if(preferences.getBoolean("darkmode",false)){
                background.background=getDrawable(R.drawable.darkbackground)
            }else{
                background.background=getDrawable(R.drawable.background1)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settingsmenu, menu)
        hideBackButton()
        return true
    }
    fun hideActionBar(){
        this.supportActionBar!!.hide()
    }
    fun showActionBar(){
        this.supportActionBar!!.show()
    }
    fun hideBottomActionBar(){
        bottom_navigation.visibility= View.GONE
    }
    fun showBottomActionBar(){
        bottom_navigation.visibility= View.VISIBLE
    }
    fun isAdmin(userid:String){
        val context = this.applicationContext
        val users = FirebaseDatabase.getInstance().reference.child("users").child(userid).child("isAdmin")
        users.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val useradmin = p0.getValue(Boolean::class.java)

                    val editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                    if(useradmin!=null){
                        editor.putBoolean("isAdmin",useradmin)
                        editor.apply()
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
    fun saveImageToInternalStorage(drawableId: Bitmap){

        val bitmap = drawableId

        MediaStore.Images.Media.insertImage(
            getContentResolver(),
            bitmap,
            "demo_image",
            "demo_image"
        );

        Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_LONG).show();

    }
    fun checkPermissions():Boolean{
        var PERMISSIONS: Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 112 );
            return false;
        }
        return true;
    }

}
