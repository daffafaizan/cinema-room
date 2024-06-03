package cinema.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Ticket(
    val token: String = UUID.randomUUID().toString(),
    @JsonProperty("ticket")
    val seat: Seat
)
