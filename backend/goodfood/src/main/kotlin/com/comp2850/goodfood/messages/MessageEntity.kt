package com.comp2850.goodfood.messages

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class MessageEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "sender_id", nullable = false)
    var senderId: String = "",

    @Column(name = "receiver_id", nullable = false)
    var receiverId: String = "",

    @Column(nullable = false, length = 500)
    var text: String = "",

    @Column(name = "is_read", nullable = false)
    var isRead: Int = 0,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)