package cinema.controller

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
import cinema.service.CinemaService
import cinema.utils.CinemaConstants
import cinema.utils.CinemaHelpers
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest
class CinemaControllerTests(@Autowired val mockMvc: MockMvc, @Autowired val mapper: ObjectMapper) {

    @MockBean
    private lateinit var cinemaService: CinemaService

    @Test
    fun testGetSeatsShouldReturnWithStatus200() {
        val mockResponse = CinemaResponseDTO(
            CinemaConstants.TOTAL_ROWS,
            CinemaConstants.TOTAL_COLUMNS,
            availableSeats
        )

        Mockito.`when`(cinemaService.getAllSeats()).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaConstants.GET_SEATS_URL))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(
                    mapper.writeValueAsString(mockResponse)
                )
            )
    }

    @Test
    fun testPurchaseValidBodyShouldReturnWithStatus200() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = CinemaConstants.PURCHASE_VALID_ROW,
            column = CinemaConstants.PURCHASE_VALID_COLUMN
        )
        val stubSeat = Seat(
            CinemaConstants.PURCHASE_VALID_ROW,
            CinemaConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(CinemaConstants.PURCHASE_VALID_COLUMN, CinemaConstants.PURCHASE_VALID_ROW),
            true
        )
        val mockResponse = Ticket(seat=stubSeat)

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(mapper.writeValueAsString(mockResponse))
            )
    }

    @Test
    fun testPurchaseInvalidOutOfBoundsBodyShouldReturnWithStatus400() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = CinemaConstants.PURCHASE_INVALID_ROW,
            column = CinemaConstants.PURCHASE_INVALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(OutOfBoundsException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testPurchaseValidAlreadyPurchasedBodyShouldReturnWithStatus400() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = CinemaConstants.PURCHASE_VALID_ROW,
            column = CinemaConstants.PURCHASE_VALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(TicketSoldException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest))
        )
        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testReturnValidBodyShouldReturnWithStatus200() {
        val stubSeat = Seat(
            CinemaConstants.PURCHASE_VALID_ROW,
            CinemaConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(CinemaConstants.PURCHASE_VALID_COLUMN, CinemaConstants.PURCHASE_VALID_ROW),
            true
        )
        val stubTicket = Ticket(seat=stubSeat)
        val mockRequest = ReturnRequestDTO(
            stubTicket.token
        )

        stubSeat.booked = false
        val mockResponse = ReturnResponseDTO(
            stubSeat
        )

        Mockito.`when`(cinemaService.returnTicket(mockRequest)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.RETURN_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(mapper.writeValueAsString(mockResponse))
            )
    }

    @Test
    fun testReturnInvalidBodyShouldReturnWithStatus400() {
        val mockRequest = ReturnRequestDTO(
            CinemaConstants.INVALID_TOKEN
        )

        Mockito.`when`(cinemaService.returnTicket(mockRequest)).thenThrow(WrongTokenException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaConstants.RETURN_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testStatsValidPasswordShouldReturnWithStatus200() {
        val mockResponse = StatsResponseDTO(
            CinemaConstants.CURRENT_INCOME,
            CinemaConstants.AVAILABLE_SEATS,
            CinemaConstants.PURCHASED_TICKETS
        )

        Mockito.`when`(cinemaService.getStats(CinemaConstants.VALID_PASSWORD)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaConstants.STATS_URL)
            .param(CinemaConstants.STATS_PARAM, CinemaConstants.VALID_PASSWORD))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(mapper.writeValueAsString(mockResponse))
            )
    }

    @Test
    fun testStatsInvalidPasswordShouldReturnWithStatus200() {
        Mockito.`when`(cinemaService.getStats(CinemaConstants.INVALID_PASSWORD)).thenThrow(WrongPasswordException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaConstants.STATS_URL)
            .param(CinemaConstants.STATS_PARAM, CinemaConstants.INVALID_PASSWORD))
            .andExpectAll(
                status().isUnauthorized
            )
    }

    companion object {
        private val availableSeats = mutableListOf<Seat>()

        @BeforeEach
        fun setUp() {
            for (i in 1..CinemaConstants.TOTAL_COLUMNS) {
                for (j in 1..CinemaConstants.TOTAL_ROWS) {
                    availableSeats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
                }
            }
        }
    }
}