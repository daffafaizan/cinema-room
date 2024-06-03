package cinema.service

import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.response.CinemaResponseDTO
import cinema.exception.OutOfBoundsException
import cinema.exception.TicketSoldException
import cinema.model.Seat
import cinema.model.Ticket
import cinema.repository.CinemaRepository
import cinema.repository.TicketRepository
import cinema.utils.CinemaHelpers
import cinema.utils.ControllerConstants
import cinema.utils.ServiceConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class CinemaServiceTests {

    @Mock
    private lateinit var cinemaRepository: CinemaRepository

    @Mock
    private lateinit var ticketRepository: TicketRepository

    @InjectMocks
    private lateinit var cinemaService: CinemaService

    @Test
    fun getAllSeats() {
        val expectedResponse = CinemaResponseDTO(
            ServiceConstants.TOTAL_ROWS,
            ServiceConstants.TOTAL_COLUMNS,
            availableSeats
        )

        Mockito.`when`(cinemaRepository.getAvailableSeats()).thenReturn(availableSeats)

        val actualResponse = cinemaService.getAllSeats()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testPurchaseTicketValidBody() {
        Mockito.`when`(cinemaRepository.getSeat(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN)).thenReturn(stubSeat)

        val actualResponse = cinemaService.purchaseTicket(mockRequest)

        assertEquals(stubSeat, actualResponse.seat)
    }

    @Test
    fun testPurchaseTicketInvalidOutOfBoundsBody() {
        val mockRequest = PurchaseSeatRequestDTO(
            ServiceConstants.PURCHASE_INVALID_ROW,
            ServiceConstants.PURCHASE_INVALID_COLUMN
        )

        assertThrows(OutOfBoundsException::class.java){
            val actualResponse = cinemaService.purchaseTicket(mockRequest)
        }
    }

    @Test
    fun testPurchaseTicketInvalidTicketSoldBody() {
        Mockito.`when`(cinemaRepository.getSeat(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN)).thenReturn(stubSeat)

        assertThrows(TicketSoldException::class.java){
            val actualResponse = cinemaService.purchaseTicket(mockRequest)
        }
    }

    companion object {
        val availableSeats = mutableListOf<Seat>()
        val mockRequest = PurchaseSeatRequestDTO(
            ServiceConstants.PURCHASE_VALID_ROW,
            ServiceConstants.PURCHASE_VALID_COLUMN
        )
        val stubSeat = Seat(
            ServiceConstants.PURCHASE_VALID_ROW,
            ServiceConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN),
            false
        )

        @JvmStatic
        @BeforeAll
        fun setUp() {
            for (i in 1..ControllerConstants.TOTAL_COLUMNS) {
                for (j in 1..ControllerConstants.TOTAL_ROWS) {
                    availableSeats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
                }
            }
        }
    }
}