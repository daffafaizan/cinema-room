package cinema.controller

import cinema.dto.request.*
import cinema.dto.response.*
import cinema.exception.*
import cinema.model.*
import cinema.service.CinemaService
import cinema.utils.ControllerConstants
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

    private val availableSeats = mutableListOf<Seat>()

    @BeforeEach
    fun setUp() {
        for (i in 1..ControllerConstants.TOTAL_COLUMNS) {
            for (j in 1..ControllerConstants.TOTAL_ROWS) {
                availableSeats.add(Seat(i, j, CinemaHelpers.seatPrice(i, j), false))
            }
        }
    }

    @Test
    fun testGetSeatsShouldReturnWithStatus200() {
        val mockResponse = CinemaResponseDTO(
            ControllerConstants.TOTAL_ROWS,
            ControllerConstants.TOTAL_COLUMNS,
            availableSeats
        )

        Mockito.`when`(cinemaService.getAllSeats()).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(ControllerConstants.GET_SEATS_URL))
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
            row = ControllerConstants.PURCHASE_VALID_ROW,
            column = ControllerConstants.PURCHASE_VALID_COLUMN
        )
        val stubSeat = Seat(
            ControllerConstants.PURCHASE_VALID_ROW,
            ControllerConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(ControllerConstants.PURCHASE_VALID_COLUMN, ControllerConstants.PURCHASE_VALID_ROW),
            true
        )
        val mockResponse = Ticket(seat=stubSeat)

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .post(ControllerConstants.PURCHASE_TICKET_URL)
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
            row = ControllerConstants.PURCHASE_INVALID_ROW,
            column = ControllerConstants.PURCHASE_INVALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(OutOfBoundsException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(ControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testPurchaseValidAlreadyPurchasedBodyShouldReturnWithStatus400() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = ControllerConstants.PURCHASE_VALID_ROW,
            column = ControllerConstants.PURCHASE_VALID_COLUMN
        )

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenThrow(TicketSoldException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(ControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest))
        )
        mockMvc.perform(MockMvcRequestBuilders
            .post(ControllerConstants.PURCHASE_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testReturnValidBodyShouldReturnWithStatus200() {
        val stubSeat = Seat(
            ControllerConstants.PURCHASE_VALID_ROW,
            ControllerConstants.PURCHASE_VALID_COLUMN,
            CinemaHelpers.seatPrice(ControllerConstants.PURCHASE_VALID_COLUMN, ControllerConstants.PURCHASE_VALID_ROW),
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
            .post(ControllerConstants.RETURN_TICKET_URL)
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
            ControllerConstants.INVALID_TOKEN
        )

        Mockito.`when`(cinemaService.returnTicket(mockRequest)).thenThrow(WrongTokenException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .post(ControllerConstants.RETURN_TICKET_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(
                status().isBadRequest
            )
    }

    @Test
    fun testStatsValidPasswordShouldReturnWithStatus200() {
        val mockResponse = StatsResponseDTO(
            ControllerConstants.CURRENT_INCOME,
            ControllerConstants.AVAILABLE_SEATS,
            ControllerConstants.PURCHASED_TICKETS
        )

        Mockito.`when`(cinemaService.getStats(ControllerConstants.VALID_PASSWORD)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get(ControllerConstants.STATS_URL)
            .param(ControllerConstants.STATS_PARAM, ControllerConstants.VALID_PASSWORD))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                content().json(mapper.writeValueAsString(mockResponse))
            )
    }

    @Test
    fun testStatsInvalidPasswordShouldReturnWithStatus200() {
        Mockito.`when`(cinemaService.getStats(ControllerConstants.INVALID_PASSWORD)).thenThrow(WrongPasswordException(""))

        mockMvc.perform(MockMvcRequestBuilders
            .get(ControllerConstants.STATS_URL)
            .param(ControllerConstants.STATS_PARAM, ControllerConstants.INVALID_PASSWORD))
            .andExpectAll(
                status().isUnauthorized
            )
    }
}