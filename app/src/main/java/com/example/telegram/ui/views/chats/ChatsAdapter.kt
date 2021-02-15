package com.example.telegram.ui.views.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.telegram.R
import com.example.telegram.models.CommonModel
import com.example.telegram.ui.views.group.GroupFragment
import com.example.telegram.ui.views.privateChat.PrivateChatFragment
import com.example.telegram.utils.TYPE_GROUP_CHAT
import com.example.telegram.utils.TYPE_PRIVATE_CHAT
import com.example.telegram.utils.downloadAndSetImage
import com.example.telegram.utils.replaceFragment
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatsAdapter : RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private val chatsList = mutableListOf<CommonModel>()

    class ChatsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chatName: TextView = view.chat_name
        val lastMessage: TextView = view.chat_last_message
        val chatAvatar: CircleImageView = view.chat_avatar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)

        val viewHolder = ChatsViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            when (chatsList[viewHolder.adapterPosition].type) {
                TYPE_PRIVATE_CHAT -> replaceFragment(PrivateChatFragment(chatsList[viewHolder.adapterPosition]))
                TYPE_GROUP_CHAT -> replaceFragment(GroupFragment(chatsList[viewHolder.adapterPosition]))
            }
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.chatName.text = chatsList[position].fullname
        holder.lastMessage.text = chatsList[position].lastMessage
        holder.chatAvatar.downloadAndSetImage(chatsList[position].avatarUrl)
    }

    override fun getItemCount(): Int = chatsList.size

    fun updateChatsList(chat: CommonModel) {
        chatsList.add(chat)
        notifyItemInserted(chatsList.size)
    }
}