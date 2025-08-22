package com.mestero.ui.dashboard.messages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mestero.data.models.ConversationModel
import com.mestero.databinding.FragmentConversationsBinding
import com.mestero.ui.adapters.ConversationsAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConversationsFragment : Fragment() {

    private var _binding: FragmentConversationsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConversationsViewModel by viewModels()

    private lateinit var adapter: ConversationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ConversationsAdapter(
            currentUserId = viewModel.currentUserId,
            onClick = { conversation ->
                val action = ConversationsFragmentDirections.actionConversationsToChat(
                    conversationId = conversation.id,
                    otherUserId = null
                )
                findNavController().navigate(action)
            })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


        viewModel.startObserving()

        viewModel.conversations.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
            binding.conversationsEmptyView.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.userNames.observe(viewLifecycleOwner) { userNames ->
            adapter.updateUserNames(userNames)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}