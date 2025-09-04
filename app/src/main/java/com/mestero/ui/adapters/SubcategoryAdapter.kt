package com.mestero.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mestero.data.models.Subcategory
import com.mestero.databinding.ItemSubcategoryBinding

class SubcategoryAdapter(
    private val subcategories: List<Subcategory>,
    private val onSubcategoryClick: (Subcategory) -> Unit
) : RecyclerView.Adapter<SubcategoryAdapter.SubcategoryViewHolder>() {

    inner class SubcategoryViewHolder(private val binding: ItemSubcategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subcategory: Subcategory) {
            binding.apply {
                subcategoryTitle.text = subcategory.getLocalizedTitle(binding.root.context)
                subcategoryDescription.text = subcategory.getLocalizedDescription(binding.root.context)
                subcategoryIcon.setImageResource(subcategory.iconResId)
                
                root.setOnClickListener {
                    onSubcategoryClick(subcategory)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubcategoryViewHolder {
        val binding = ItemSubcategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubcategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubcategoryViewHolder, position: Int) {
        holder.bind(subcategories[position])
    }

    override fun getItemCount() = subcategories.size

} 