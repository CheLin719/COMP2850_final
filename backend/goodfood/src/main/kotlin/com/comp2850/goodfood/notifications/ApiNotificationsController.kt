package com.comp2850.goodfood.notifications

import com.comp2850.goodfood.user.repository.UserStore
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/notifications")
class ApiNotificationsController(
    private val notificationJpaRepository: NotificationJpaRepository,
    private val userStore: UserStore
) {

    /**
     * GET /api/notifications — get all notifications for the current user
     */
    @GetMapping
    fun getNotifications(
        authentication: Authentication,
        @RequestParam(defaultValue = "false") unreadOnly: Boolean
    ): List<ApiNotificationResponse> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val notifications = if (unreadOnly) {
            notificationJpaRepository.findUnreadByUserId(currentUser.id)
        } else {
            notificationJpaRepository.findByUserId(currentUser.id)
                .sortedByDescending { it.createdAt }
        }

        return notifications.map { it.toResponse() }
    }

    /**
     * GET /api/notifications/unread-count — get the number of unread notifications
     */
    @GetMapping("/unread-count")
    fun getUnreadCount(authentication: Authentication): Map<String, Int> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val count = notificationJpaRepository.countUnreadByUserId(currentUser.id)
        return mapOf("unreadCount" to count)
    }

    /**
     * PUT /api/notifications/{notificationId}/read — mark a notification as read
     */
    @PutMapping("/{notificationId}/read")
    fun markAsRead(
        authentication: Authentication,
        @PathVariable notificationId: Long
    ): ApiNotificationResponse {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val notification = notificationJpaRepository.findById(notificationId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "notification not found") }

        if (notification.userId != currentUser.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "this notification is not yours")
        }

        notification.isRead = 1
        val saved = notificationJpaRepository.save(notification)
        return saved.toResponse()
    }

    /**
     * PUT /api/notifications/read-all — mark all notifications as read
     */
    @PutMapping("/read-all")
    fun markAllAsRead(authentication: Authentication): Map<String, String> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val unreadNotifications = notificationJpaRepository.findUnreadByUserId(currentUser.id)
        unreadNotifications.forEach { it.isRead = 1 }
        notificationJpaRepository.saveAll(unreadNotifications)

        return mapOf("message" to "all notifications marked as read")
    }

    /**
     * DELETE /api/notifications/{notificationId} — delete a notification
     */
    @DeleteMapping("/{notificationId}")
    fun deleteNotification(
        authentication: Authentication,
        @PathVariable notificationId: Long
    ): Map<String, String> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val notification = notificationJpaRepository.findById(notificationId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "notification not found") }

        if (notification.userId != currentUser.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "this notification is not yours")
        }

        notificationJpaRepository.deleteById(notificationId)
        return mapOf("message" to "notification deleted")
    }

    private fun NotificationEntity.toResponse(): ApiNotificationResponse {
        return ApiNotificationResponse(
            id = this.id ?: 0L,
            type = this.type,
            title = this.title,
            message = this.message,
            relatedId = this.relatedId,
            isRead = this.isRead == 1,
            createdAt = this.createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        )
    }
}

data class ApiNotificationResponse(
    val id: Long,
    val type: String,
    val title: String,
    val message: String,
    val relatedId: String?,
    val isRead: Boolean,
    val createdAt: String
)
