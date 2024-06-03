package cinema.service

import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.request.ReturnRequestDTO
import cinema.dto.response.CinemaResponseDTO
import cinema.dto.response.ReturnResponseDTO
import cinema.dto.response.StatsResponseDTO
import cinema.exception.OutOfBoundsException
import cinema.exception.TicketSoldException
import cinema.exception.WrongPasswordException
import cinema.exception.WrongTokenException
import cinema.model.Seat
import cinema.model.Ticket
import cinema.repository.CinemaRepository
import cinema.repository.TicketRepository
import cinema.utils.CinemaHelpers
import cinema.utils.ControllerConstants
import cinema.utils.ServiceConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
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

    private lateinit var mockRequest: PurchaseSeatRequestDTO
    private lateinit var stubAvailableSeat: Seat
    private lateinit var stubPurchasedSeat: Seat
    private lateinit var availableSeats: MutableList<Seat>

    @BeforeEach
    fun setUp() {
        availableSeats = mutableListOf()
        mockRequest = PurchaseSeatRequestDTO(
            ServiceConstants.PURCHASE_VALID_ROW,
            ServiceConstants.PURCHASE_VALID_COLUMN
        )
        stubAvailableSeat = Seat(
            ServiceConstants.PURCHASE_VALID_ROW,
            ServiceConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN),
            false
        )
        stubPurchasedSeat = Seat(
            ServiceConstants.PURCHASE_VALID_ROW,
            ServiceConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN),
            true
        )

        for (i in 1..ControllerConstants.TOTAL_COLUMNS) {
            for (j in 1..ControllerConstants.TOTAL_ROWS) {
                availableSeats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
            }
        }
    }

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
        Mockito.`when`(cinemaRepository.getSeat(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN)).thenReturn(stubAvailableSeat)
        val actualResponse = cinemaService.purchaseTicket(mockRequest)

        assertEquals(stubAvailableSeat, actualResponse.seat)
    }

    @Test
    fun testPurchaseTicketInvalidOutOfBoundsBody() {
        val mockRequest = PurchaseSeatRequestDTO(
            ServiceConstants.PURCHASE_INVALID_ROW,
            ServiceConstants.PURCHASE_INVALID_COLUMN
        )

        assertThrows(OutOfBoundsException::class.java){
            cinemaService.purchaseTicket(mockRequest)
        }
    }

    @Test
    fun testPurchaseTicketInvalidTicketSoldBody() {
        Mockito.`when`(cinemaRepository.getSeat(ServiceConstants.PURCHASE_VALID_ROW, ServiceConstants.PURCHASE_VALID_COLUMN)).thenReturn(stubAvailableSeat)
        cinemaService.purchaseTicket(mockRequest)

        assertThrows(TicketSoldException::class.java){
            cinemaService.purchaseTicket(mockRequest)
        }
    }

    @Test
    fun testReturnTicketValidBody() {
        val mockRequest = ReturnRequestDTO(
            ServiceConstants.TOKEN
        )
        val ticket = Ticket(mockRequest.token, seat=stubPurchasedSeat)

        Mockito.`when`(ticketRepository.getTicket(mockRequest.token)).thenReturn(ticket)
        Mockito.doNothing().`when`(cinemaRepository).updateSeat(ticket.seat.row, ticket.seat.column, ticket.seat)
        Mockito.doNothing().`when`(ticketRepository).updateTicket(mockRequest.token, ticket)

        val actualResponse = cinemaService.returnTicket(mockRequest)
        val excpectedResponse = ReturnResponseDTO(
            ticket.seat
        )
        assertEquals(excpectedResponse, actualResponse)
    }

    @Test
    fun testReturnTicketInvalidBody() {
        val mockRequest = ReturnRequestDTO(
            ""
        )
        val ticket = Ticket(mockRequest.token, seat=stubPurchasedSeat)

        Mockito.`when`(ticketRepository.getTicket(mockRequest.token)).thenThrow(WrongTokenException(""))
        Mockito.doNothing().`when`(cinemaRepository).updateSeat(ticket.seat.row, ticket.seat.column, ticket.seat)
        Mockito.doNothing().`when`(ticketRepository).updateTicket(mockRequest.token, ticket)

        assertThrows(WrongTokenException::class.java) {
            cinemaService.returnTicket(mockRequest)
        }
    }

    @Test
    fun testGetStatsValidPassword() {
        val expectedResponse = StatsResponseDTO(
            ServiceConstants.CURRENT_INCOME,
            ServiceConstants.AVAILABLE_SEATS,
            ServiceConstants.PURCHASED_TICKETS
        )

        Mockito.`when`(cinemaRepository.getIncome()).thenReturn(ServiceConstants.CURRENT_INCOME)
        Mockito.`when`(cinemaRepository.getAvailableSeats()).thenReturn(availableSeats)
        Mockito.`when`(cinemaRepository.getPurchasedSeats()).thenReturn(emptyList<Seat>())

        val actualResponse = cinemaService.getStats(ServiceConstants.VALID_PASSWORD)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testGetStatsInvalidPassword() {
        assertThrows(WrongPasswordException::class.java) {
            cinemaService.getStats(ServiceConstants.INVALID_PASSWORD)
        }
    }
}