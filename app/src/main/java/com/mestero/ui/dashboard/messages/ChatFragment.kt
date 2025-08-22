package com.mestero.ui.dashboard.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.util.Log
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.databinding.FragmentChatBinding
import com.mestero.ui.adapters.MessageAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()

    private lateinit var adapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = MessageAdapter(
            currentUserIdProvider = { viewModel.currentUserId })
        binding.messagesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.messagesRecyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
            sendMessageAndHideKeyboard()
        }

        binding.messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessageAndHideKeyboard()
                true
            } else
                false
        }

        viewModel.messages.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)

            if (list.isNotEmpty()) {
                binding.messagesRecyclerView.scrollToPosition(list.size - 1)
            }
        }

        viewModel.chatTitle.observe(viewLifecycleOwner) { title ->
            binding.chatTitle.text = title
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null)
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show()
        }

        val conversationId = args.conversationId
        val otherUserId = args.otherUserId
        if (!conversationId.isNullOrEmpty()) {
            viewModel.observeConversation(conversationId)

        } else if (!otherUserId.isNullOrEmpty()) {
            viewModel.startWithUser(otherUserId)

        }
    }

    private fun sendMessageAndHideKeyboard() {
        val text = binding.messageInput.text?.toString().orEmpty()
        viewModel.sendMessage(text)

        binding.messageInput.setText("")
        val imm = requireContext().getSystemService<InputMethodManager>()
        imm?.hideSoftInputFromWindow(
            binding.messageInput.windowToken,
            0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}