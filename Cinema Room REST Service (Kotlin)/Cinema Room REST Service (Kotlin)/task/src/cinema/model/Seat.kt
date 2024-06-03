package cinema.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Seat(
    var row: Int,
    var column: Int,
    var price: Int,
    @JsonIgnore
    var booked: Boolean
)