package com.example.opsc_companioncare.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.opsc_companioncare.data.Message
import com.example.opsc_companioncare.databinding.MessageItemsBinding
import com.example.opsc_companioncare.utils.Constants.RECEIVE_ID
import com.example.opsc_companioncare.utils.Constants.SEND_ID
class MessagingAdapter : RecyclerView.Adapter<MessagingAdapter.MessageViewHolder>() {

    var messagesList = mutableListOf<Message>()

    inner class MessageViewHolder(val binding: MessageItemsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                messagesList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val currentMessage = messagesList[position]

        when (currentMessage.id) {
            SEND_ID -> {
                holder.binding.tvMessage.apply {
                    text = currentMessage.message
                    visibility = android.view.View.VISIBLE
                }
                holder.binding.tvBotMessage.visibility = android.view.View.GONE
            }
            RECEIVE_ID -> {
                holder.binding.tvBotMessage.apply {
                    text = currentMessage.message
                    visibility = android.view.View.VISIBLE
                }
                holder.binding.tvMessage.visibility = android.view.View.GONE
            }
        }
    }

    fun insertMessage(message: Message) {
        this.messagesList.add(message)
        notifyItemInserted(messagesList.size - 1)
    }
}
