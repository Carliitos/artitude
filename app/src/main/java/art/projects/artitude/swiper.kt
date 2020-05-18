package art.projects.artitude


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.AdapterView.OnItemClickListener
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import kotlinx.android.synthetic.main.fragment_swiper.*


/**
 * A simple [Fragment] subclass.
 */
class swiper : Fragment() {
    var al:ArrayList<String>?=null
    var arrayAdapter:ArrayAdapter<String>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_swiper, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as MainActivity).showBottomActionBar()
        (activity as MainActivity).showActionBar()

        val flingContainer = frame as SwipeFlingAdapterView

        al = ArrayList<String>()
        al!!.add("php")
        al!!.add("c")
        al!!.add("python")
        al!!.add("java")
        arrayAdapter = ArrayAdapter<String>(this.requireContext(), R.layout.item, R.id.name, al!!)

        //set the listener and the adapter
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(object : SwipeFlingAdapterView.onFlingListener {
            override fun onScroll(p0: Float) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!")
                al!!.removeAt(0)
                arrayAdapter!!.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                //Toast.makeText(this@MyActivity, "Left!", Toast.LENGTH_SHORT).show()
            }

            override fun onRightCardExit(dataObject: Any) {
                //Toast.makeText(this@MyActivity, "Right!", Toast.LENGTH_SHORT).show()
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {
                // Ask for more data here
                al!!.add("XML " + itemsInAdapter)
                arrayAdapter!!.notifyDataSetChanged()
                Log.d("LIST", "notified")
                //i++
            }

        })
        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(object : SwipeFlingAdapterView.OnItemClickListener {
            override fun onItemClicked(itemPosition: Int, dataObject: Any) {
                //makeToast(this@MyActivity, "Clicked!")
            }
        })
    }


}
