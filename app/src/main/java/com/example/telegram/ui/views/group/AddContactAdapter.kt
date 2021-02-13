package com.example.telegram.ui.views.group

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.utils.downloadAndSetImage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_add_contact.view.*

class AddContactAdapter : RecyclerView.Adapter<AddContactAdapter.AddContactViewHolder>() {

    private val contactsList = mutableListOf<CommonModel>()

    class AddContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.add_contact_name
        val lastTime: TextView = view.add_contact_last_time
        val contactAvatar: CircleImageView = view.add_contact_avatar
        val checkPic: CircleImageView = view.add_contact_check
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContactViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_contact, parent, false)

        val viewHolder = AddContactViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            if (contactsList[viewHolder.adapterPosition].checked){
                viewHolder.checkPic.visibility = View.INVISIBLE
                contactsList[viewHolder.adapterPosition].checked = false
                AddContactFragment.checkedContactsList.remove(contactsList[viewHolder.adapterPosition])
            } else {
                viewHolder.checkPic.visibility = View.VISIBLE
                contactsList[viewHolder.adapterPosition].checked = true
                AddContactFragment.checkedContactsList.add(contactsList[viewHolder.adapterPosition])
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: AddContactViewHolder, position: Int) {
        holder.contactName.text = contactsList[position].fullname
        holder.lastTime.text = contactsList[position].lastMessage
        holder.contactAvatar.downloadAndSetImage(contactsList[position].avatarUrl)
    }

    override fun getItemCount(): Int = contactsList.size

    fun updateChatsList(chat: CommonModel) {
        contactsList.add(chat)
        notifyItemInserted(contactsList.size)
    }
}