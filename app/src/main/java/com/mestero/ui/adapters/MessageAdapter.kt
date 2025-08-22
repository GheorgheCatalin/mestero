package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mestero.data.models.MessageModel
import com.mestero.databinding.ItemMessageIncomingBinding
import com.mestero.databinding.ItemMessageOutgoingBinding
import com.mestero.utils.formatAsRelativeDate


class MessageAdapter(
    private val currentUserIdProvider: () -> String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items = mutableListOf<MessageModel>()

    companion object {
        private const val OUTGOING_MESSAGE = 1
        private const val INCOMING_MESSAGE = 2
    }

    fun submit(newItems: List<MessageModel>) {
        items.clear()
        items.addAll(newItems)

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item.senderId == currentUserIdProvider()) OUTGOING_MESSAGE else INCOMING_MESSAGE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == OUTGOING_MESSAGE) {
            val binding = ItemMessageOutgoingBinding.inflate(inflater, parent, false)
            OutgoingViewHolder(binding)
        } else {
            val binding = ItemMessageIncomingBinding.inflate(inflater, parent, false)
            IncomingViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is OutgoingViewHolder -> holder.bind(item)
            is IncomingViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

}

class OutgoingViewHolder(private val binding: ItemMessageOutgoingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageModel) {
        binding.messageText.text = item.text
        binding.messageMeta.text = item.createdAt.formatAsRelativeDate()
    }
}

class IncomingViewHolder(private val binding: ItemMessageIncomingBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MessageModel) {
        binding.messageText.text = item.text
        binding.messageMeta.text = item.createdAt.formatAsRelativeDate()
    }
}
