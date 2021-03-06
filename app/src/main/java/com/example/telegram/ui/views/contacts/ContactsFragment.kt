package com.example.telegram.ui.views.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.database.*
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.views.base.BaseFragment
import com.example.telegram.ui.views.privateChat.PrivateChatFragment
import com.example.telegram.utils.*
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_contacts.*
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactsFragment : BaseFragment(R.layout.fragment_contacts) {

    private lateinit var mContacts: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListener: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = getString(R.string.contacts)
        initRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }

    private fun initRecyclerView() {
        mContacts = contacts_rv
        mRefContacts = REF_DATABASE_ROOT.child(NODE_CONTACTS).child(CURRENT_UID)

        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()
        mAdapter = object : FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_contact, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListener = AppValueEventListener {
                    val contact = it.getCommonModel()

                    if (contact.fullname.isEmpty() || contact.fullname.isBlank()) {
                        holder.name.text = model.fullname
                    } else {
                        holder.name.text = contact.fullname
                    }
                    if (contact.state == AppStates.ONLINE.state){
                        holder.status.setTextColor(ContextCompat.getColor(APP_ACTIVITY, R.color.colorPrimary))
                    }
                    holder.status.text = contact.state
                    holder.avatar.downloadAndSetImage(contact.avatarUrl)
                    holder.itemView.setOnClickListener { replaceFragment(PrivateChatFragment(model)) }
                }

                mRefUsers.addValueEventListener(mRefUsersListener)
                mapListeners[mRefUsers] = mRefUsersListener
            }
        }

        mContacts.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.contact_fullname
        val status: TextView = view.contact_status
        val avatar: CircleImageView = view.contact_avatar
    }
}
