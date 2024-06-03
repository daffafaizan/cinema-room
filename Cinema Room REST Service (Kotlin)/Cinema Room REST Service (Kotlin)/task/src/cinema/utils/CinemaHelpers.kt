package cinema.utils

class CinemaHelpers {
    companion object {
        fun seatPrice(row: Int, seat: Int): Int {
            if (row <= 4) {
                return 10
            }
            return 8
        }
    }
}