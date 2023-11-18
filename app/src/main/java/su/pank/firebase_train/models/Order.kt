package su.pank.firebase_train.models

import kotlinx.datetime.LocalDateTime

data class Order(val dateTime: LocalDateTime, val location: String, val menuItems: List<MenuItem>, val uid: String)
