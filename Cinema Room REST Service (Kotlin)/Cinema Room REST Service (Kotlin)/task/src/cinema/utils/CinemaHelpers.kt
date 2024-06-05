package cinema.utils

import cinema.utils.PriceConditionsConstants.CINEMA_HALF_THRESHOLD
import cinema.utils.PriceConditionsConstants.FIRST_HALF_PRICE
import cinema.utils.PriceConditionsConstants.SECOND_HALF_PRICE


class CinemaHelpers {
    companion object {
        fun seatPrice(row: Int, seat: Int): Int {
            if (row <= CINEMA_HALF_THRESHOLD) {
                return FIRST_HALF_PRICE
            }
            return SECOND_HALF_PRICE
        }
    }
}