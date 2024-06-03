package cinema.repository

import cinema.model.Seat
import cinema.utils.CinemaHelpers
import cinema.utils.CinemaRepositoryConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CinemaRepositoryTests {

    private lateinit var cinemaRepository: CinemaRepository

    @BeforeEach
    fun setUp() {
        cinemaRepository = CinemaRepository()
    }

    @Test
    fun testGetAvailableSeats() {
        val availableSeats: List<Seat> = cinemaRepository.getAvailableSeats()

        assertTrue(availableSeats.size == CinemaRepositoryConstants.TOTAL_SEATS)
    }

    @Test
    fun testGetPurchasedSeats() {
        val mockSeat = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            true
        )

        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockSeat)

        val purchasedSeats = cinemaRepository.getPurchasedSeats()

        assertTrue(purchasedSeats.size == 1)
    }
}