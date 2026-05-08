package com.comp2850.goodfood.notifications

/**
 * Event objects used to propagate real-time notifications via the event bus.
 */
sealed class NotificationEvent {
    abstract val userId: String
    abstract val type: String
    abstract val message: String
}

// Plan update event
data class PlanUpdatedEvent(
    override val userId: String,
    val planId: Long,
    val planType: String, // "meal" or "training"
    val proName: String
) : NotificationEvent() {
    override val type: String = "plan_updated"
    override val message: String = "$proName updated your ${if (planType == "meal") "meal" else "training"} plan"
}

// Message received event
data class MessageReceivedEvent(
    override val userId: String,
    val messageId: Long,
    val senderId: String,
    val senderName: String,
    override val message: String
) : NotificationEvent() {
    override val type: String = "message_received"
}

// User unbound event
data class UserUnboundEvent(
    override val userId: String,
    val clientId: String,
    val clientName: String
) : NotificationEvent() {
    override val type: String = "user_unbound"
    override val message: String = "Client $clientName has disconnected"
}

// Plan deleted event
data class PlanDeletedEvent(
    override val userId: String,
    val planId: Long,
    val planType: String,
    val proName: String
) : NotificationEvent() {
    override val type: String = "plan_deleted"
    override val message: String = "$proName deleted your ${if (planType == "meal") "meal" else "training"} plan"
}
