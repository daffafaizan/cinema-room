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
    private lateinit var mockPurchasedSeat: Seat
    private lateinit var mockAvailableSeat: Seat

    @BeforeEach
    fun setUp() {
        cinemaRepository = CinemaRepository()
        mockPurchasedSeat = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            true
        )
        mockAvailableSeat = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            false
        )
    }

    @Test
    fun testGetAvailableSeats() {
        val availableSeats: List<Seat> = cinemaRepository.getAvailableSeats()

        assertTrue(availableSeats.size == CinemaRepositoryConstants.TOTAL_SEATS)
    }

    @Test
    fun testGetPurchasedSeats() {
        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockPurchasedSeat)

        val purchasedSeats = cinemaRepository.getPurchasedSeats()

        assertTrue(purchasedSeats.size == 1)
    }

    @Test
    fun testGetIncome() {
        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockPurchasedSeat)

        val income = cinemaRepository.getIncome()

        assertTrue(income == CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN))
    }

    @Test
    fun testGetSeat() {
        val seat = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)

        assertEquals(mockAvailableSeat, seat)
    }

    @Test
    fun testUpdateSeat() {
        val seatBeforeUpdate = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)
        assertEquals(mockAvailableSeat, seatBeforeUpdate)

        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockPurchasedSeat)

        val seatAfterUpdate = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)
        assertEquals(mockPurchasedSeat, seatAfterUpdate)
    }
}