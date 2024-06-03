package cinema.controller

import cinema.dto.request.*
import cinema.dto.response.*
import cinema.exception.*
import cinema.model.*
import cinema.service.CinemaService
import cinema.utils.CinemaControllerConstants
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
            CinemaControllerConstants.TOTAL_ROWS,
            CinemaControllerConstants.TOTAL_COLUMNS,
            availableSeats
        )

        Mockito.`when`(cinemaService.getAllSeats()).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaControllerConstants.GET_SEATS_URL))
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
            row = CinemaControllerConstants.PURCHASE_VALID_ROW,
            column = CinemaControllerConstants.PURCHASE_VALID_COLUMN
        )
        val stubSeat = Seat(
            CinemaControllerConstants.PURCHASE_VALID_ROW,
            CinemaControllerConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(CinemaControllerConstants.PURCHASE_VALID_COLUMN, CinemaControllerConstants.PURCHASE_VALID_ROW),
            true
        )
        val mockResponse = Ticket(seat=stubSeat)

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaControllerConstants.PURCHASE_TICKET_URL)
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
            row = CinemaControllerConstants.PURCHASE_INVALID_ROW,
            column = CinemaControllerConstants.PURCHASE_INVALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(OutOfBoundsException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testPurchaseValidAlreadyPurchasedBodyShouldReturnWithStatus400() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = CinemaControllerConstants.PURCHASE_VALID_ROW,
            column = CinemaControllerConstants.PURCHASE_VALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(TicketSoldException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest))
        )
        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testReturnValidBodyShouldReturnWithStatus200() {
        val stubSeat = Seat(
            CinemaControllerConstants.PURCHASE_VALID_ROW,
            CinemaControllerConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(CinemaControllerConstants.PURCHASE_VALID_COLUMN, CinemaControllerConstants.PURCHASE_VALID_ROW),
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
            .post(CinemaControllerConstants.RETURN_TICKET_URL)
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
            CinemaControllerConstants.INVALID_TOKEN
        )

        Mockito.`when`(cinemaService.returnTicket(mockRequest)).thenThrow(WrongTokenException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(CinemaControllerConstants.RETURN_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testStatsValidPasswordShouldReturnWithStatus200() {
        val mockResponse = StatsResponseDTO(
            CinemaControllerConstants.CURRENT_INCOME,
            CinemaControllerConstants.AVAILABLE_SEATS,
            CinemaControllerConstants.PURCHASED_TICKETS
        )

        Mockito.`when`(cinemaService.getStats(CinemaControllerConstants.VALID_PASSWORD)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaControllerConstants.STATS_URL)
            .param(CinemaControllerConstants.STATS_PARAM, CinemaControllerConstants.VALID_PASSWORD))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(mapper.writeValueAsString(mockResponse))
            )
    }

    @Test
    fun testStatsInvalidPasswordShouldReturnWithStatus200() {
        Mockito.`when`(cinemaService.getStats(CinemaControllerConstants.INVALID_PASSWORD)).thenThrow(WrongPasswordException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .get(CinemaControllerConstants.STATS_URL)
            .param(CinemaControllerConstants.STATS_PARAM, CinemaControllerConstants.INVALID_PASSWORD))
            .andExpectAll(
                status().isUnauthorized
            )
    }

    companion object {
        private val availableSeats = mutableListOf<Seat>()

        @BeforeEach
        fun setUp() {
            for (i in 1..CinemaControllerConstants.TOTAL_COLUMNS) {
                for (j in 1..CinemaControllerConstants.TOTAL_ROWS) {
                    availableSeats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
                }
            }
        }
    }
}