package art.projects.artitude

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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
            when(item.itemId) {
                R.id.item_home -> {
                    navController.navigate(R.id.swiper)
                    true

                }
                R.id.item_account -> {
                    navController.navigate(R.id.accountinfo)
                    true
                }
                R.id.item_addImg -> {
                    navController.navigate(R.id.uploadImages)
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

        if(FirebaseAuth.getInstance().currentUser!=null){
            navController.navigate(R.id.swiper)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.settingsmenu, menu)
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


}
