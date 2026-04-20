package com.comp2850.goodfood.messages

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MessageJpaRepository : JpaRepository<MessageEntity, Long> {

    @Query(
        """
        select m from MessageEntity m
        where (m.senderId = :userA and m.receiverId = :userB)
           or (m.senderId = :userB and m.receiverId = :userA)
        order by m.createdAt asc
        """
    )
    fun findConversation(
        @Param("userA") userA: String,
        @Param("userB") userB: String
    ): List<MessageEntity>
}