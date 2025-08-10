package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.mestero.data.models.CategoryManager
import com.mestero.data.models.ListingModel
import com.mestero.databinding.ItemListingBinding

class ListingAdapter(
    private val onListingClick: (ListingModel) -> Unit
) : ListAdapter<ListingModel, ListingAdapter.ListingViewHolder>(ListingDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val binding = ItemListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ListingViewHolder(private val binding: ItemListingBinding) :
        androidx.recyclerview.widget.RecyclerView.ViewHolder(binding.root) {

        fun bind(listing: ListingModel) {
            binding.apply {
                val categoryTitle = CategoryManager.getCategoryById(listing.category)?.title ?: listing.category
                val subcategoryTitle = CategoryManager.getSubcategoryById(listing.subcategory)?.title ?: listing.subcategory
                categoryTextView.text = categoryTitle
                subcategoryTextView.text = subcategoryTitle

                titleTextView.text = listing.title
                descriptionTextView.text = listing.description
                priceTextView.text = listing.formattedPrice

                locationTextView.text = listing.displayLocation

                root.setOnClickListener {
                    onListingClick(listing)
                }
            }
        }
    }
}

class ListingDiffCallback : DiffUtil.ItemCallback<ListingModel>() {
    override fun areItemsTheSame(oldItem: ListingModel, newItem: ListingModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ListingModel, newItem: ListingModel): Boolean {
        return oldItem == newItem
    }
}