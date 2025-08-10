package com.mestero.data

enum class UserType {
    CLIENT,
    PROVIDER
}

// Todo use in case firestore cant serialize enums
//enum class UserType(val label: String) {
//    CLIENT("client"),
//    PROVIDER("provider");
//
//    override fun toString(): String = label
//}