package cinema.dto.response

import cinema.model.Seat

data class ReturnResponseDTO(
    val returned_ticket: Seat
)