package cinema.utils

const val FIRST_HALF_PRICE = 10
const val SECOND_HALF_PRICE = 8

class CinemaHelpers {
    companion object {
        fun seatPrice(row: Int, seat: Int): Int {
            if (row <= 4) {
                return FIRST_HALF_PRICE
            }
            return SECOND_HALF_PRICE
        }
    }
}