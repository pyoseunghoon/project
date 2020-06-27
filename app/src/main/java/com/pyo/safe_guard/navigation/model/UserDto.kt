package com.pyo.safe_guard.navigation.model

data class UserDto(
    var email: String? = null,
    var password : String? = null,
    var name : String? = null,
    var uid : String? = null,
    var timestamp : Long? = null
)