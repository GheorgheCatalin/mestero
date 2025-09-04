package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mestero.data.models.Category
import com.mestero.databinding.ItemCategoryHorizontalBinding

class CategoryHorizontalAdapter(
    private var categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryHorizontalAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryHorizontalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.apply {
                categoryTitle.text = category.getLocalizedTitle(binding.root.context)
                categoryIcon.setImageResource(category.iconResId)
                
                root.setOnClickListener {
                    onCategoryClick(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
    
    fun submit(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }
} 