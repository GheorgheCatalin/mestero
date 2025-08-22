package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mestero.data.models.ConversationModel
import com.mestero.databinding.ItemConversationBinding
import com.mestero.utils.formatAsRelativeDate

class ConversationsAdapter(
    private val currentUserId: String,
    private val onClick: (ConversationModel) -> Unit
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {
    
    private val items = mutableListOf<ConversationModel>()
    private var userNames = mutableMapOf<String, String>()
    
    fun submit(newItems: List<ConversationModel>) {
        items.clear()
        items.addAll(newItems)
        
        notifyDataSetChanged()
    }
    
    fun updateUserNames(newUserNames: Map<String, String>) {
        userNames.clear()
        userNames.putAll(newUserNames)

        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConversationViewHolder(binding, currentUserId, userNames, onClick)
    }
    
    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ConversationViewHolder(
        private val binding: ItemConversationBinding,
        private val currentUserId: String,
        private val userNames: Map<String, String>,
        private val onClick: (ConversationModel) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(item: ConversationModel) {
            // Set the other participant's info
            val otherId = item.participants.firstOrNull { it != currentUserId }
            val otherName = userNames[otherId] ?: "Unknown User"
            binding.avatarTv.text = (otherName.firstOrNull() ?: 'U').toString()
            binding.title.text = otherName

            binding.lastMessage.text = item.lastMessage
            binding.time.text = item.lastMessageAt.formatAsRelativeDate()

            // Badge for unread messages
            val unreadCount = item.getUnreadCountsForDisplay()[currentUserId] ?: 0L
            if (unreadCount > 0) {
                binding.unreadBadge.visibility = View.VISIBLE
                binding.unreadBadge.text = unreadCount.toString()
            } else {
                binding.unreadBadge.visibility = View.GONE
            }

            binding.root.setOnClickListener { onClick(item) }
        }
    }
}