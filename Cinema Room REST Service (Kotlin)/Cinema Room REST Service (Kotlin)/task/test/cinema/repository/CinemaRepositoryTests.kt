package cinema.repository

import cinema.model.Seat
import cinema.utils.CinemaHelpers
import cinema.utils.RepositoryConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CinemaRepositoryTests {

    private lateinit var cinemaRepository: CinemaRepository
    private lateinit var mockPurchasedSeat: Seat
    private lateinit var mockAvailableSeat: Seat

    @BeforeEach
    fun setUp() {
        cinemaRepository = CinemaRepository()
        mockPurchasedSeat = Seat(
            RepositoryConstants.ROW,
            RepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(RepositoryConstants.ROW, RepositoryConstants.COLUMN),
            true
        )
        mockAvailableSeat = Seat(
            RepositoryConstants.ROW,
            RepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(RepositoryConstants.ROW, RepositoryConstants.COLUMN),
            false
        )
    }

    @Test
    fun testGetAvailableSeats() {
        val availableSeats: List<Seat> = cinemaRepository.getAvailableSeats()

        assertTrue(availableSeats.size == RepositoryConstants.TOTAL_SEATS)
    }

    @Test
    fun testGetPurchasedSeats() {
        cinemaRepository.updateSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN, mockPurchasedSeat)

        val purchasedSeats = cinemaRepository.getPurchasedSeats()

        assertTrue(purchasedSeats.size == 1)
    }

    @Test
    fun testGetIncome() {
        cinemaRepository.updateSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN, mockPurchasedSeat)

        val income = cinemaRepository.getIncome()

        assertTrue(income == CinemaHelpers.seatPrice(RepositoryConstants.ROW, RepositoryConstants.COLUMN))
    }

    @Test
    fun testGetSeat() {
        val seat = cinemaRepository.getSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN)

        assertEquals(mockAvailableSeat, seat)
    }

    @Test
    fun testUpdateSeat() {
        val seatBeforeUpdate = cinemaRepository.getSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN)
        assertEquals(mockAvailableSeat, seatBeforeUpdate)

        cinemaRepository.updateSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN, mockPurchasedSeat)

        val seatAfterUpdate = cinemaRepository.getSeat(RepositoryConstants.ROW, RepositoryConstants.COLUMN)
        assertEquals(mockPurchasedSeat, seatAfterUpdate)
    }
}