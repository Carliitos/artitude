package art.projects.artitude


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import art.projects.artitude.Models.Message
import art.projects.artitude.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.fragment_latest_messages.*
import kotlinx.android.synthetic.main.ultimosmensajesitem.view.*

/**
 * A simple [Fragment] subclass.
 */
class LatestMessages : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_latest_messages, container, false)
    }
    var contexts:Context?=null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contexts = this.requireContext()
        recycler.adapter=adapter
        getUsers()
        adapter.setOnItemClickListener { item, view ->
            val row = item as UsuarioLatest

            accountinfo.userobj = row.otherUser
            Navigation.findNavController(view).navigate(R.id.chat)
        }
    }

    class UsuarioLatest(val usuarioLatest: Message): Item<GroupieViewHolder>(){
        var otherUser:User?=null

        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.lastmessage.setText(usuarioLatest.text)

            val otherUserId:String
            if(usuarioLatest.fromId == FirebaseAuth.getInstance().uid){
                otherUserId=usuarioLatest.toId
            }else{
                otherUserId=usuarioLatest.fromId
            }
            val ref = FirebaseDatabase.getInstance().getReference("/users/$otherUserId")
            ref.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    otherUser = p0.getValue(User::class.java)
                    viewHolder.itemView.username.text = otherUser!!.username
                    if(otherUser!!.avatarUrl!=""&&otherUser!!.avatarUrl!=null){
                        Picasso.get().load(otherUser!!.avatarUrl!!).into(viewHolder.itemView.userimage);
                    }
                }

            })
        }
        override fun getLayout(): Int {
            return R.layout.ultimosmensajesitem
        }
    }
        val adapter = GroupAdapter<GroupieViewHolder>()
        val mensajesHashMap = HashMap<String, Message>()
    private fun refreshRecycler(){
            adapter.clear()
            mensajesHashMap.values.forEach {
            adapter.add(UsuarioLatest(it))
        }
    }
    private fun getUsers() {
        val currentUid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$currentUid")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)?:return
                if(chatMessage.text=="") chatMessage.text="Image"
                mensajesHashMap[p0.key!!] = chatMessage
                notification!!.visibility=View.GONE
                refreshRecycler()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(Message::class.java)?:return
                if(chatMessage.text=="") chatMessage.text="Image"
                mensajesHashMap[p0.key!!] = chatMessage
                refreshRecycler()
            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }

}
