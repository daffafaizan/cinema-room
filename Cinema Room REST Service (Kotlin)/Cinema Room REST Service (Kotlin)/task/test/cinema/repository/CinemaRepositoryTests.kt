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

    @Test
    fun testGetIncome() {
        val mockSeat = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            true
        )

        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockSeat)

        val income = cinemaRepository.getIncome()

        assertTrue(income == CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN))
    }

    @Test
    fun testGetSeat() {
        val mockSeat = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            false
        )

        val seat = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)

        assertEquals(mockSeat, seat)
    }

    @Test
    fun testUpdateSeat() {
        val mockSeatBeforeUpdate = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            false
        )
        val mockSeatAfterUpdate = Seat(
            CinemaRepositoryConstants.ROW,
            CinemaRepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN),
            true
        )

        val seatBeforeUpdate = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)
        assertEquals(mockSeatBeforeUpdate, seatBeforeUpdate)

        cinemaRepository.updateSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN, mockSeatAfterUpdate)

        val seatAfterUpdate = cinemaRepository.getSeat(CinemaRepositoryConstants.ROW, CinemaRepositoryConstants.COLUMN)
        assertEquals(mockSeatAfterUpdate, seatAfterUpdate)
    }
}