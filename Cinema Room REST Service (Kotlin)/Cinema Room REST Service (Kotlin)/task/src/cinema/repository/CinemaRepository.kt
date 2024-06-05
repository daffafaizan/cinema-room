package cinema.repository

import cinema.model.Seat
import cinema.utils.CinemaHelpers
import cinema.utils.CinemaInitConstants.COLUMN
import cinema.utils.CinemaInitConstants.ROW
import org.springframework.stereotype.Repository

@Repository
class CinemaRepository {
    private final val seats = mutableListOf<Seat>()

    init {
        for (i in 1..COLUMN) {
            for (j in 1..ROW) {
                seats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
            }
        }
    }

    fun getAvailableSeats(): List<Seat> {
        return seats.filter { !it.booked }
    }

    fun getPurchasedSeats(): List<Seat> {
        return seats.filter { it.booked }
    }

    fun getIncome(): Int {
        return seats.filter { it.booked }.sumOf { it.price }
    }

    fun getSeat(row: Int, column: Int): Seat? {
        return seats.find { it.row == row && it.column == column }
    }

    fun updateSeat(seat: Seat) {
        val index = seats.indexOfFirst { it.row == seat.row && it.column == seat.column }
        seats[index] = seat
    }


}

