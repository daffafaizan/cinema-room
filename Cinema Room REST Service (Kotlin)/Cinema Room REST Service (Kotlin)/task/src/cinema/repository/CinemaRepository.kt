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
                seats.add(Seat(i, j, seatPrice(i, j), false))
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

    fun updateSeat(row: Int, column: Int, seat: Seat) {
        val index = seats.indexOfFirst { it.row == row && it.column == column }
        seats[index] = seat
    }

    private final fun seatPrice(row: Int, seat: Int): Int {
        if (row <= 4) {
            return 10
        }
        return 8
    }

}

