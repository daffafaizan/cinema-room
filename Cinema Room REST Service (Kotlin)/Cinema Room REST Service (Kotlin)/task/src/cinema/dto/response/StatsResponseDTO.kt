package cinema.dto.response

data class StatsResponseDTO(
    val current_income: Int,
    val number_of_available_seats: Int,
    val number_of_purchased_tickets: Int
)