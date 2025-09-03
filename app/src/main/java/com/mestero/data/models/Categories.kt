package com.mestero.data.models

import com.mestero.R

data class Category(
    val id: String,
    val title: String,
    val iconResId: Int,
    val subcategories: List<Subcategory>
)

data class Subcategory(
    val id: String,
    val title: String,
    val iconResId: Int,
    val parentCategoryId: String,
    val description: String
)


// Predefined categories and subcategories for the app
object CategoryManager {
    
    val categories: List<Category> = listOf(
        Category(
            id = "home_improvement",
            title = "Home Improvement & Maintenance",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("plumbing", "Plumbing", R.drawable.ic_home_improvement, "home_improvement", "Pipe repair, leak fixing, drain cleaning, water heater installation, faucet replacement"),
                Subcategory("electrical", "Electrical", R.drawable.ic_home_improvement, "home_improvement", "Wiring, lighting installation, circuit breaker repair, outlet installation, smart home setup"),
                Subcategory("tiling", "Tiling", R.drawable.ic_home_improvement, "home_improvement", "Ceramic tiling, porcelain tiling, mosaic installation, tile repair, grouting"),
                Subcategory("flooring", "Flooring", R.drawable.ic_home_improvement, "home_improvement", "Hardwood installation, carpet installation, laminate flooring, vinyl flooring, floor refinishing"),
                Subcategory("carpentry", "Carpentry", R.drawable.ic_home_improvement, "home_improvement", "Furniture building, cabinetry, deck construction, framing, trim work"),
                Subcategory("painting", "Painting", R.drawable.ic_home_improvement, "home_improvement", "Interior painting, exterior painting, wallpaper installation, decorative painting"),
                Subcategory("hvac", "HVAC", R.drawable.ic_home_improvement, "home_improvement", "Air conditioning repair, heating system installation, duct cleaning, thermostat setup"),
                Subcategory("roofing", "Roofing", R.drawable.ic_home_improvement, "home_improvement", "Roof repair, shingle replacement, gutter cleaning, roof inspection"),
                Subcategory("masonry", "Masonry", R.drawable.ic_home_improvement, "home_improvement", "Brickwork, stonework, concrete pouring, chimney repair"),
                Subcategory("handyman", "General Handyman", R.drawable.ic_home_improvement, "home_improvement", "Furniture assembly, minor repairs, wall mounting, door repair")
            )
        ),
        Category(
            id = "automotive",
            title = "Automotive Services",
            iconResId = R.drawable.ic_category_auto,
            subcategories = listOf(
                Subcategory("auto_repair", "Auto Repair", R.drawable.ic_category_auto, "automotive", "Brake repair, oil changes, engine diagnostics, tire services"),
                Subcategory("detailing", "Car Detailing", R.drawable.ic_category_auto, "automotive", "Interior cleaning, exterior polishing, waxing"),
                Subcategory("towing", "Towing & Roadside Assistance", R.drawable.ic_category_auto, "automotive", "Flat tire repair, battery jumpstart, vehicle towing"),
                Subcategory("customization", "Customization", R.drawable.ic_category_auto, "automotive", "Car wrapping, custom paint jobs, audio system installation")
            )
        ),
        Category(
            id = "professional",
            title = "Professional & Business Services",
            iconResId = R.drawable.ic_category_professional,
            subcategories = listOf(
                Subcategory("legal", "Legal Services", R.drawable.ic_category_professional, "professional", "Contract drafting, legal consultation, notary services"),
                Subcategory("accounting", "Accounting & Bookkeeping", R.drawable.ic_category_professional, "professional", "Tax preparation, payroll services, financial planning"),
                Subcategory("marketing", "Marketing & Advertising", R.drawable.ic_category_professional, "professional", "Social media management, SEO, content creation, graphic design"),
                Subcategory("it", "IT & Tech Support", R.drawable.ic_category_professional, "professional", "Computer repair, software installation, network setup, cybersecurity"),
                Subcategory("consulting", "Consulting", R.drawable.ic_category_professional, "professional", "Business strategy, HR consulting, management consulting"),
                Subcategory("admin", "Administrative Support", R.drawable.ic_category_professional, "professional", "Virtual assistance, data entry, scheduling")
            )
        ),
        Category(
            id = "outdoor",
            title = "Outdoor & Landscaping",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("landscaping", "Landscaping", R.drawable.ic_home_improvement, "outdoor", "Lawn mowing, garden design, tree trimming, irrigation systems"),
                Subcategory("hardscaping", "Hardscaping", R.drawable.ic_home_improvement, "outdoor", "Patio installation, walkway construction, retaining walls"),
                Subcategory("pest_control", "Pest Control", R.drawable.ic_home_improvement, "outdoor", "Insect extermination, rodent control, eco-friendly pest solutions"),
                Subcategory("snow_removal", "Snow Removal", R.drawable.ic_home_improvement, "outdoor", "Snow plowing, ice management, sidewalk clearing"),
                Subcategory("pool", "Pool Maintenance", R.drawable.ic_home_improvement, "outdoor", "Pool cleaning, chemical balancing, pool repair")
            )
        ),
        Category(
            id = "creative",
            title = "Creative & Event Services",
            iconResId = R.drawable.ic_category_creative_events,
            subcategories = listOf(
                Subcategory("photography", "Photography", R.drawable.ic_category_creative_events, "creative", "Wedding photography, portrait photography, event photography, product photography"),
                Subcategory("videography", "Videography", R.drawable.ic_category_creative_events, "creative", "Video editing, event filming, promotional videos"),
                Subcategory("event_planning", "Event Planning", R.drawable.ic_category_creative_events, "creative", "Wedding planning, corporate events, birthday parties, catering coordination"),
                Subcategory("music", "Music & Entertainment", R.drawable.ic_category_creative_events, "creative", "DJ services, live bands, music lessons, performance artists"),
                Subcategory("graphic_design", "Graphic Design", R.drawable.ic_category_creative_events, "creative", "Logo design, branding, illustration, print design"),
                Subcategory("writing", "Writing & Editing", R.drawable.ic_category_creative_events, "creative", "Copywriting, content writing, proofreading, translation")
            )
        ),
        Category(
            id = "technical",
            title = "Technical & Skilled Trades",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("welding", "Welding", R.drawable.ic_home_improvement, "technical", "Metal fabrication, structural welding, artistic welding"),
                Subcategory("machinery", "Machinery Repair", R.drawable.ic_home_improvement, "technical", "Heavy equipment repair, small engine repair"),
                Subcategory("locksmith", "Locksmith Services", R.drawable.ic_home_improvement, "technical", "Lock installation, key duplication, emergency lockout"),
                Subcategory("security", "Security Installation", R.drawable.ic_home_improvement, "technical", "Alarm systems, CCTV installation, smart locks")
            )
        ),
        Category(
            id = "health",
            title = "Health & Medical Services",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("home_health", "Home Health Aide", R.drawable.ic_home_improvement, "health", "Post-surgery care, mobility assistance, medication reminders"),
                Subcategory("physical_therapy", "Physical Therapy", R.drawable.ic_home_improvement, "health", "In-home therapy, rehabilitation exercises"),
                Subcategory("alternative", "Alternative Medicine", R.drawable.ic_home_improvement, "health", "Acupuncture, chiropractic services, herbal consultations"),
                Subcategory("medical_transport", "Medical Transport", R.drawable.ic_home_improvement, "health", "Non-emergency medical transport, wheelchair-accessible vans")
            )
        ),
        Category(
            id = "cleaning",
            title = "Cleaning Services",
            iconResId = R.drawable.ic_category_cleaning,
            subcategories = listOf(
                Subcategory("residential", "Residential Cleaning", R.drawable.ic_category_cleaning, "cleaning", "House cleaning, deep cleaning, move-in/move-out cleaning"),
                Subcategory("commercial", "Commercial Cleaning", R.drawable.ic_category_cleaning, "cleaning", "Office cleaning, retail space cleaning, post-construction cleanup"),
                Subcategory("specialized", "Specialized Cleaning", R.drawable.ic_category_cleaning, "cleaning", "Carpet cleaning, upholstery cleaning, window washing, pressure washing"),
                Subcategory("eco_friendly", "Eco-Friendly Cleaning", R.drawable.ic_category_cleaning, "cleaning", "Green cleaning, non-toxic product cleaning"),
                Subcategory("organizing", "Organizing Services", R.drawable.ic_category_cleaning, "cleaning", "Decluttering, closet organization, storage solutions")
            )
        ),
        Category(
            id = "personal_care",
            title = "Personal Care & Wellness",
            iconResId = R.drawable.ic_category_wellness,
            subcategories = listOf(
                Subcategory("hair", "Hair Services", R.drawable.ic_category_wellness, "personal_care", "Haircuts, styling, coloring, extensions, barber services"),
                Subcategory("nails", "Nail Services", R.drawable.ic_category_wellness, "personal_care", "Manicures, pedicures, nail art, gel/acrylic nails"),
                Subcategory("spa", "Spa & Massage", R.drawable.ic_category_wellness, "personal_care", "Deep tissue massage, Swedish massage, facials, body treatments"),
                Subcategory("fitness", "Fitness Training", R.drawable.ic_category_wellness, "personal_care", "Personal training, yoga instruction, group fitness classes"),
                Subcategory("nutrition", "Nutrition & Diet", R.drawable.ic_category_wellness, "personal_care", "Meal planning, nutrition coaching, dietary consultations"),
                Subcategory("wellness", "Mental Wellness", R.drawable.ic_category_wellness, "personal_care", "Life coaching, meditation sessions, therapy referrals")
            )
        ),
        Category(
            id = "education",
            title = "Education & Tutoring",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("academic", "Academic Tutoring", R.drawable.ic_home_improvement, "education", "Math tutoring, science tutoring, language tutoring, test prep (SAT, ACT)"),
                Subcategory("language", "Language Instruction", R.drawable.ic_home_improvement, "education", "ESL, Spanish, French, language immersion"),
                Subcategory("music_lessons", "Music Lessons", R.drawable.ic_home_improvement, "education", "Piano lessons, guitar lessons, vocal coaching"),
                Subcategory("professional_skills", "Professional Skills", R.drawable.ic_home_improvement, "education", "Coding bootcamps, public speaking, leadership training"),
                Subcategory("hobby", "Hobby Classes", R.drawable.ic_home_improvement, "education", "Cooking classes, painting workshops, photography courses")
            )
        ),
        Category(
            id = "pet",
            title = "Pet & Animal Services",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("grooming", "Pet Grooming", R.drawable.ic_home_improvement, "pet", "Dog grooming, cat grooming, nail trimming"),
                Subcategory("sitting", "Pet Sitting & Walking", R.drawable.ic_home_improvement, "pet", "Dog walking, in-home pet sitting, boarding"),
                Subcategory("veterinary", "Veterinary Services", R.drawable.ic_home_improvement, "pet", "Mobile vet visits, vaccinations, pet health consultations"),
                Subcategory("training", "Pet Training", R.drawable.ic_home_improvement, "pet", "Obedience training, behavioral training, agility training")
            )
        ),
        Category(
            id = "transportation",
            title = "Transportation & Delivery",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("ride", "Ride Services", R.drawable.ic_home_improvement, "transportation", "Private drivers, airport shuttles, event transportation"),
                Subcategory("delivery", "Delivery Services", R.drawable.ic_home_improvement, "transportation", "Courier services, grocery delivery, furniture transport"),
                Subcategory("moving", "Moving Services", R.drawable.ic_home_improvement, "transportation", "Residential moving, commercial moving, packing services"),
                Subcategory("logistics", "Logistics", R.drawable.ic_home_improvement, "transportation", "Freight transport, supply chain coordination")
            )
        ),
        Category(
            id = "childcare",
            title = "Childcare & Family Services",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("babysitting", "Babysitting", R.drawable.ic_home_improvement, "childcare", "Infant care, after-school care, overnight sitting"),
                Subcategory("nanny", "Nanny Services", R.drawable.ic_home_improvement, "childcare", "Full-time nannies, part-time caregivers, au pair services"),
                Subcategory("elderly", "Elderly Care", R.drawable.ic_home_improvement, "childcare", "In-home care, companionship, medical assistance"),
                Subcategory("special_needs", "Special Needs Care", R.drawable.ic_home_improvement, "childcare", "Autism support, disability care, therapy assistance")
            )
        ),
        Category(
            id = "specialty",
            title = "Specialty & Niche Services",
            iconResId = R.drawable.ic_home_improvement,
            subcategories = listOf(
                Subcategory("staging", "Home Staging", R.drawable.ic_home_improvement, "specialty", "Real estate staging, interior decorating for sales"),
                Subcategory("eco", "Eco-Friendly Services", R.drawable.ic_home_improvement, "specialty", "Solar panel installation, energy audits, sustainable landscaping"),
                Subcategory("fabrication", "Custom Fabrication", R.drawable.ic_home_improvement, "specialty", "3D printing, custom metalwork, bespoke furniture"),
                Subcategory("spiritual", "Spiritual Services", R.drawable.ic_home_improvement, "specialty", "Tarot reading, astrology consultations, life coaching"),
                Subcategory("repair", "Repair Services", R.drawable.ic_home_improvement, "specialty", "Electronics repair, appliance repair, jewelry repair")
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
}