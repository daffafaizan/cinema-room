package cinema.repository

import cinema.model.Seat
import org.springframework.stereotype.Repository

const val ROW = 9
const val COLUMN = 9

@Repository
class CinemaRepository {
    private final val seats = mutableListOf<Seat>()

    init {
        for (i in 1..COLUMN) {
            for (j in 1..ROW) {
                seats.add(Seat(i, j))
            }
        }
    }

    fun getAllSeats(): MutableList<Seat> {
        return seats
    }

}

