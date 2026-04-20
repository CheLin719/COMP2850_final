package com.comp2850.goodfood.appointments

import com.comp2850.goodfood.user.Role
import com.comp2850.goodfood.user.User
import com.comp2850.goodfood.user.repository.UserStore
import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/api/appointments")
class ApiAppointmentsController(
    private val appointmentJpaRepository: AppointmentJpaRepository,
    private val userStore: UserStore
) {

    @GetMapping
    fun getAppointments(authentication: Authentication): List<ApiAppointmentResponse> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val appointments = when (currentUser.role) {
            Role.HEALTH_PROFESSIONAL -> appointmentJpaRepository.findAllByProIdOrderByDateAscTimeAsc(currentUser.id)
            Role.SUBSCRIBER -> appointmentJpaRepository.findAllByClientIdOrderByDateAscTimeAsc(currentUser.id)
        }

        return appointments.map { it.toResponse() }
    }

    @PostMapping
    fun createAppointment(
        authentication: Authentication,
        @Valid @RequestBody request: ApiAppointmentCreateRequest
    ): ResponseEntity<Map<String, Any?>> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        if (currentUser.role != Role.HEALTH_PROFESSIONAL) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "only professionals can create appointments")
        }

        val client = userStore.findById(request.clientId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "client not found")

        if (client.role != Role.SUBSCRIBER || client.proId != currentUser.id) {
            throw ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "you can only create appointments for your assigned clients"
            )
        }

        val saved = appointmentJpaRepository.save(
            AppointmentEntity(
                proId = currentUser.id,
                clientId = client.id,
                date = request.date,
                time = request.time,
                type = request.type?.trim()?.ifBlank { null },
                status = "pending"
            )
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(
            mapOf("id" to (saved.id ?: 0L))
        )
    }

    @PatchMapping("/{id}")
    fun updateAppointmentStatus(
        authentication: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody request: ApiAppointmentStatusUpdateRequest
    ): ResponseEntity<Map<String, Any?>> {
        val currentUser = userStore.findByEmail(authentication.name)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")

        val appointment = appointmentJpaRepository.findById(id).orElse(null)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "appointment not found")

        validateAppointmentAccess(currentUser, appointment)

        val newStatus = normalizeStatus(request.status)
        appointment.status = newStatus

        val saved = appointmentJpaRepository.save(appointment)

        return ResponseEntity.ok(
            mapOf(
                "id" to (saved.id ?: 0L),
                "status" to saved.status
            )
        )
    }

    private fun validateAppointmentAccess(currentUser: User, appointment: AppointmentEntity) {
        when (currentUser.role) {
            Role.HEALTH_PROFESSIONAL -> {
                if (appointment.proId != currentUser.id) {
                    throw ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "you can only access your own appointments"
                    )
                }
            }

            Role.SUBSCRIBER -> {
                if (appointment.clientId != currentUser.id) {
                    throw ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "you can only access your own appointments"
                    )
                }
            }
        }
    }

    private fun normalizeStatus(status: String): String {
        return when (status.trim().lowercase()) {
            "pending" -> "pending"
            "confirmed" -> "confirmed"
            "cancelled" -> "cancelled"
            "completed" -> "completed"
            else -> throw ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid status")
        }
    }

    private fun AppointmentEntity.toResponse(): ApiAppointmentResponse {
        return ApiAppointmentResponse(
            id = this.id ?: 0L,
            proId = this.proId,
            clientId = this.clientId,
            date = this.date.toString(),
            time = this.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            type = this.type,
            status = this.status
        )
    }
}

data class ApiAppointmentCreateRequest(
    @field:NotBlank(message = "clientId is required")
    val clientId: String,

    @field:NotNull(message = "date is required")
    @field:JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate,

    @field:NotNull(message = "time is required")
    @field:JsonFormat(pattern = "HH:mm")
    val time: LocalTime,

    val type: String? = null
)

data class ApiAppointmentStatusUpdateRequest(
    @field:NotBlank(message = "status is required")
    val status: String
)

data class ApiAppointmentResponse(
    val id: Long,
    val proId: String,
    val clientId: String,
    val date: String,
    val time: String,
    val type: String?,
    val status: String
)