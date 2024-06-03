package cinema.dto.response

import cinema.model.Seat

data class CinemaResponseDTO(
    val total_rows: Int,
    val total_columns: Int,
    val available_seats: List<Seat>
)
