package com.mestero.data.models

import android.content.Context
import com.mestero.R

data class Category(
    val id: String,
    val titleResId: Int,
    val iconResId: Int,
    val subcategories: List<Subcategory>
) {
    fun getLocalizedTitle(context: Context): String {
        return context.getString(titleResId)
    }
}

data class Subcategory(
    val id: String,
    val titleResId: Int,
    val iconResId: Int,
    val parentCategoryId: String,
    val descriptionResId: Int
) {
    fun getLocalizedTitle(context: Context): String {
        return context.getString(titleResId)
    }
    
    fun getLocalizedDescription(context: Context): String {
        return context.getString(descriptionResId)
    }
}


// Predefined categories and subcategories for the app
object CategoryManager {
    
    val categories: List<Category> = listOf(
        Category(
            id = "home_improvement",
            titleResId = R.string.category_home_improvement,
            iconResId = R.drawable.ic_category_home,
            subcategories = listOf(
                Subcategory("plumbing", R.string.subcategory_plumbing, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_plumbing_description),
                Subcategory("electrical", R.string.subcategory_electrical, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_electrical_description),
                Subcategory("tiling", R.string.subcategory_tiling, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_tiling_description),
                Subcategory("flooring", R.string.subcategory_flooring, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_flooring_description),
                Subcategory("carpentry", R.string.subcategory_carpentry, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_carpentry_description),
                Subcategory("painting", R.string.subcategory_painting, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_painting_description),
                Subcategory("hvac", R.string.subcategory_hvac, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_hvac_description),
                Subcategory("roofing", R.string.subcategory_roofing, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_roofing_description),
                Subcategory("masonry", R.string.subcategory_masonry, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_masonry_description),
                Subcategory("handyman", R.string.subcategory_handyman, R.drawable.ic_home_improvement, "home_improvement", R.string.subcategory_handyman_description)
            )
        ),
        Category(
            id = "automotive",
            titleResId = R.string.category_automotive,
            iconResId = R.drawable.ic_category_auto,
            subcategories = listOf(
                Subcategory("auto_repair", R.string.subcategory_auto_repair, R.drawable.ic_category_auto, "automotive", R.string.subcategory_auto_repair_description),
                Subcategory("detailing", R.string.subcategory_detailing, R.drawable.ic_category_auto, "automotive", R.string.subcategory_detailing_description),
                Subcategory("towing", R.string.subcategory_towing, R.drawable.ic_category_auto, "automotive", R.string.subcategory_towing_description),
                Subcategory("customization", R.string.subcategory_customization, R.drawable.ic_category_auto, "automotive", R.string.subcategory_customization_description)
            )
        ),
        Category(
            id = "professional",
            titleResId = R.string.category_professional,
            iconResId = R.drawable.ic_category_professional,
            subcategories = listOf(
                Subcategory("legal", R.string.subcategory_legal, R.drawable.ic_category_professional, "professional", R.string.subcategory_legal_description),
                Subcategory("accounting", R.string.subcategory_accounting, R.drawable.ic_category_professional, "professional", R.string.subcategory_accounting_description),
                Subcategory("marketing", R.string.subcategory_marketing, R.drawable.ic_category_professional, "professional", R.string.subcategory_marketing_description),
                Subcategory("it", R.string.subcategory_it, R.drawable.ic_category_professional, "professional", R.string.subcategory_it_description),
                Subcategory("consulting", R.string.subcategory_consulting, R.drawable.ic_category_professional, "professional", R.string.subcategory_consulting_description),
                Subcategory("admin", R.string.subcategory_admin, R.drawable.ic_category_professional, "professional", R.string.subcategory_admin_description)
            )
        ),
        Category(
            id = "outdoor",
            titleResId = R.string.category_outdoor,
            iconResId = R.drawable.ic_category_outdoor,
            subcategories = listOf(
                Subcategory("landscaping", R.string.subcategory_landscaping, R.drawable.ic_category_outdoor, "outdoor", R.string.subcategory_landscaping_description),
                Subcategory("hardscaping", R.string.subcategory_hardscaping, R.drawable.ic_category_outdoor, "outdoor", R.string.subcategory_hardscaping_description),
                Subcategory("pest_control", R.string.subcategory_pest_control, R.drawable.ic_category_outdoor, "outdoor", R.string.subcategory_pest_control_description),
                Subcategory("snow_removal", R.string.subcategory_snow_removal, R.drawable.ic_category_outdoor, "outdoor", R.string.subcategory_snow_removal_description),
                Subcategory("pool", R.string.subcategory_pool, R.drawable.ic_category_outdoor, "outdoor", R.string.subcategory_pool_description)
            )
        ),
        Category(
            id = "creative",
            titleResId = R.string.category_creative,
            iconResId = R.drawable.ic_category_creative,
            subcategories = listOf(
                Subcategory("photography", R.string.subcategory_photography, R.drawable.ic_category_creative, "creative", R.string.subcategory_photography_description),
                Subcategory("videography", R.string.subcategory_videography, R.drawable.ic_category_creative, "creative", R.string.subcategory_videography_description),
                Subcategory("event_planning", R.string.subcategory_event_planning, R.drawable.ic_category_creative, "creative", R.string.subcategory_event_planning_description),
                Subcategory("music", R.string.subcategory_music, R.drawable.ic_category_creative, "creative", R.string.subcategory_music_description),
                Subcategory("graphic_design", R.string.subcategory_graphic_design, R.drawable.ic_category_creative, "creative", R.string.subcategory_graphic_design_description),
                Subcategory("writing", R.string.subcategory_writing, R.drawable.ic_category_creative, "creative", R.string.subcategory_writing_description)
            )
        ),
        Category(
            id = "technical",
            titleResId = R.string.category_technical,
            iconResId = R.drawable.ic_category_technical,
            subcategories = listOf(
                Subcategory("welding", R.string.subcategory_welding, R.drawable.ic_category_technical, "technical", R.string.subcategory_welding_description),
                Subcategory("machinery", R.string.subcategory_machinery, R.drawable.ic_category_technical, "technical", R.string.subcategory_machinery_description),
                Subcategory("locksmith", R.string.subcategory_locksmith, R.drawable.ic_category_technical, "technical", R.string.subcategory_locksmith_description),
                Subcategory("security", R.string.subcategory_security, R.drawable.ic_category_technical, "technical", R.string.subcategory_security_description)
            )
        ),
        Category(
            id = "health",
            titleResId = R.string.category_health,
            iconResId = R.drawable.ic_category_medical,
            subcategories = listOf(
                Subcategory("home_health", R.string.subcategory_home_health, R.drawable.ic_category_medical, "health", R.string.subcategory_home_health_description),
                Subcategory("physical_therapy", R.string.subcategory_physical_therapy, R.drawable.ic_category_medical, "health", R.string.subcategory_physical_therapy_description),
                Subcategory("alternative", R.string.subcategory_alternative, R.drawable.ic_category_medical, "health", R.string.subcategory_alternative_description),
                Subcategory("medical_transport", R.string.subcategory_medical_transport, R.drawable.ic_category_medical, "health", R.string.subcategory_medical_transport_description)
            )
        ),
        Category(
            id = "cleaning",
            titleResId = R.string.category_cleaning,
            iconResId = R.drawable.ic_category_cleaning,
            subcategories = listOf(
                Subcategory("residential", R.string.subcategory_residential, R.drawable.ic_category_cleaning, "cleaning", R.string.subcategory_residential_description),
                Subcategory("commercial", R.string.subcategory_commercial, R.drawable.ic_category_cleaning, "cleaning", R.string.subcategory_commercial_description),
                Subcategory("specialized", R.string.subcategory_specialized, R.drawable.ic_category_cleaning, "cleaning", R.string.subcategory_specialized_description),
                Subcategory("eco_friendly", R.string.subcategory_eco_friendly, R.drawable.ic_category_cleaning, "cleaning", R.string.subcategory_eco_friendly_description),
                Subcategory("organizing", R.string.subcategory_organizing, R.drawable.ic_category_cleaning, "cleaning", R.string.subcategory_organizing_description)
            )
        ),
        Category(
            id = "personal_care",
            titleResId = R.string.category_personal_care,
            iconResId = R.drawable.ic_category_wellness,
            subcategories = listOf(
                Subcategory("hair", R.string.subcategory_hair, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_hair_description),
                Subcategory("nails", R.string.subcategory_nails, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_nails_description),
                Subcategory("spa", R.string.subcategory_spa, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_spa_description),
                Subcategory("fitness", R.string.subcategory_fitness, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_fitness_description),
                Subcategory("nutrition", R.string.subcategory_nutrition, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_nutrition_description),
                Subcategory("wellness", R.string.subcategory_wellness, R.drawable.ic_category_wellness, "personal_care", R.string.subcategory_wellness_description)
            )
        ),
        Category(
            id = "education",
            titleResId = R.string.category_education,
            iconResId = R.drawable.ic_category_education,
            subcategories = listOf(
                Subcategory("academic", R.string.subcategory_academic, R.drawable.ic_category_education, "education", R.string.subcategory_academic_description),
                Subcategory("language", R.string.subcategory_language, R.drawable.ic_category_education, "education", R.string.subcategory_language_description),
                Subcategory("music_lessons", R.string.subcategory_music_lessons, R.drawable.ic_category_education, "education", R.string.subcategory_music_lessons_description),
                Subcategory("professional_skills", R.string.subcategory_professional_skills, R.drawable.ic_category_education, "education", R.string.subcategory_professional_skills_description),
                Subcategory("hobby", R.string.subcategory_hobby, R.drawable.ic_category_education, "education", R.string.subcategory_hobby_description)
            )
        ),
        Category(
            id = "pet",
            titleResId = R.string.category_pet,
            iconResId = R.drawable.ic_category_pet,
            subcategories = listOf(
                Subcategory("grooming", R.string.subcategory_grooming, R.drawable.ic_category_pet, "pet", R.string.subcategory_grooming_description),
                Subcategory("sitting", R.string.subcategory_sitting, R.drawable.ic_category_pet, "pet", R.string.subcategory_sitting_description),
                Subcategory("veterinary", R.string.subcategory_veterinary, R.drawable.ic_category_pet, "pet", R.string.subcategory_veterinary_description),
                Subcategory("training", R.string.subcategory_training, R.drawable.ic_category_pet, "pet", R.string.subcategory_training_description)
            )
        ),
        Category(
            id = "transportation",
            titleResId = R.string.category_transportation,
            iconResId = R.drawable.ic_category_transportation,
            subcategories = listOf(
                Subcategory("ride", R.string.subcategory_ride, R.drawable.ic_category_transportation, "transportation", R.string.subcategory_ride_description),
                Subcategory("delivery", R.string.subcategory_delivery, R.drawable.ic_category_transportation, "transportation", R.string.subcategory_delivery_description),
                Subcategory("moving", R.string.subcategory_moving, R.drawable.ic_category_transportation, "transportation", R.string.subcategory_moving_description),
                Subcategory("logistics", R.string.subcategory_logistics, R.drawable.ic_category_transportation, "transportation", R.string.subcategory_logistics_description)
            )
        ),
        Category(
            id = "childcare",
            titleResId = R.string.category_childcare,
            iconResId = R.drawable.ic_category_childcare,
            subcategories = listOf(
                Subcategory("babysitting", R.string.subcategory_babysitting, R.drawable.ic_category_childcare, "childcare", R.string.subcategory_babysitting_description),
                Subcategory("nanny", R.string.subcategory_nanny, R.drawable.ic_category_childcare, "childcare", R.string.subcategory_nanny_description),
                Subcategory("elderly", R.string.subcategory_elderly, R.drawable.ic_category_childcare, "childcare", R.string.subcategory_elderly_description),
                Subcategory("special_needs", R.string.subcategory_special_needs, R.drawable.ic_category_childcare, "childcare", R.string.subcategory_special_needs_description)
            )
        ),
        Category(
            id = "specialty",
            titleResId = R.string.category_specialty,
            iconResId = R.drawable.ic_category_more,
            subcategories = listOf(
                Subcategory("staging", R.string.subcategory_staging, R.drawable.ic_category_more, "specialty", R.string.subcategory_staging_description),
                Subcategory("eco", R.string.subcategory_eco, R.drawable.ic_category_more, "specialty", R.string.subcategory_eco_description),
                Subcategory("fabrication", R.string.subcategory_fabrication, R.drawable.ic_category_more, "specialty", R.string.subcategory_fabrication_description),
                Subcategory("spiritual", R.string.subcategory_spiritual, R.drawable.ic_category_more, "specialty", R.string.subcategory_spiritual_description),
                Subcategory("repair", R.string.subcategory_repair, R.drawable.ic_category_more, "specialty", R.string.subcategory_repair_description)
            )
        )
    )


    fun getCategoryById(categoryId: String): Category? {
        return categories.find { it.id == categoryId }
    }

    fun getSubcategoryById(subcategoryId: String): Subcategory? {
        return categories.flatMap { it.subcategories }.find { it.id == subcategoryId }
    }

    fun getSubcategoriesForCategoryId(categoryId: String): List<Subcategory> {
        return getCategoryById(categoryId)?.subcategories ?: emptyList()
    }
    
    // Helper methods for localized strings
    fun getLocalizedCategoryTitle(context: Context, categoryId: String): String {
        return getCategoryById(categoryId)?.getLocalizedTitle(context) ?: categoryId
    }
    
    fun getLocalizedSubcategoryTitle(context: Context, subcategoryId: String): String {
        return getSubcategoryById(subcategoryId)?.getLocalizedTitle(context) ?: subcategoryId
    }
}