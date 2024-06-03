package cinema.repository

import cinema.model.Seat
import cinema.utils.CinemaHelpers
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

        assertTrue(availableSeats.size == 81)
    }

    @Test
    fun testGetPurchasedSeats() {
        val mockSeat = Seat(
            1,
            1,
            CinemaHelpers.seatPrice(1, 1),
            true
        )

        cinemaRepository.updateSeat(1, 1, mockSeat)

        val purchasedSeats = cinemaRepository.getPurchasedSeats()

        assertTrue(purchasedSeats.size == 1)
    }
}