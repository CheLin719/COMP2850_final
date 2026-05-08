package com.comp2850.goodfood.websocket

import com.comp2850.goodfood.notifications.*
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

/**
 * WebSocket handler — manages client connections and pushes real-time notifications.
 */
@Component
class NotificationWebSocketHandler : TextWebSocketHandler() {

    private val sessions: MutableMap<String, WebSocketSession> = mutableMapOf()
    private val objectMapper = ObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // Extract userId from query parameter when a client connects.
        // Expected format: /ws/notifications?userId=xxx
        val query = session.uri?.query
        val userId = parseUserId(query)

        if (userId != null) {
            sessions[userId] = session
            println("WebSocket client connected: userId=$userId")
        }
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.entries.find { it.value == session }?.let {
            sessions.remove(it.key)
            println("WebSocket client disconnected: userId=${it.key}")
        }
    }

    /**
     * Handle plan update events.
     */
    @EventListener
    fun handlePlanUpdate(event: PlanUpdatedEvent) {
        sendNotificationToUser(
            event.userId,
            mapOf(
                "type" to "plan_updated",
                "planType" to event.planType,
                "planId" to event.planId,
                "message" to event.message,
                "title" to "Plan updated"
            )
        )
    }

    /**
     * Handle message received events.
     */
    @EventListener
    fun handleMessageReceived(event: MessageReceivedEvent) {
        sendNotificationToUser(
            event.userId,
            mapOf(
                "type" to "message_received",
                "messageId" to event.messageId,
                "senderId" to event.senderId,
                "senderName" to event.senderName,
                "message" to event.message,
                "title" to "New message"
            )
        )
    }

    /**
     * Handle user unbound events.
     */
    @EventListener
    fun handleUserUnbound(event: UserUnboundEvent) {
        sendNotificationToUser(
            event.userId,
            mapOf(
                "type" to "user_unbound",
                "clientId" to event.clientId,
                "clientName" to event.clientName,
                "message" to event.message,
                "title" to "Client disconnected"
            )
        )
    }

    /**
     * Handle plan deleted events.
     */
    @EventListener
    fun handlePlanDeleted(event: PlanDeletedEvent) {
        sendNotificationToUser(
            event.userId,
            mapOf(
                "type" to "plan_deleted",
                "planType" to event.planType,
                "planId" to event.planId,
                "message" to event.message,
                "title" to "Plan deleted"
            )
        )
    }

    private fun sendNotificationToUser(userId: String, data: Map<String, Any>) {
        val session = sessions[userId]
        if (session != null && session.isOpen) {
            try {
                val jsonMessage = objectMapper.writeValueAsString(data)
                session.sendMessage(TextMessage(jsonMessage))
                println("Pushed notification to $userId: ${data["message"]}")
            } catch (e: Exception) {
                println("Failed to push notification: ${e.message}")
            }
        }
    }

    private fun parseUserId(query: String?): String? {
        if (query == null) return null
        val params = query.split("&")
        for (param in params) {
            if (param.startsWith("userId=")) {
                return param.substringAfter("=")
            }
        }
        return null
    }
}
