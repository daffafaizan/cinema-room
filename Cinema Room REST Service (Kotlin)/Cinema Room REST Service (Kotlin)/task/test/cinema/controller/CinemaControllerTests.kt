package cinema.controller

import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.response.CinemaResponseDTO
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
        val expectedResponse = CinemaResponseDTO(
            CinemaConstants.TOTAL_ROWS,
            CinemaConstants.TOTAL_COLUMNS,
            availableSeats
        )

        Mockito.`when`(cinemaService.getAllSeats()).thenReturn(expectedResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .get("/seats"))
            .andExpectAll(status().isOk, content().contentType(MediaType.APPLICATION_JSON), content().json(
                mapper.writeValueAsString(expectedResponse)
            ))
    }

    @Test
    fun testPurchaseValidBodyShouldReturnWithStatus200() {
        val mockRequest = PurchaseSeatRequestDTO(
            row = CinemaConstants.PURCHASE_ROW,
            column = CinemaConstants.PURCHASE_COLUMN
        )
        val stubSeat = Seat(
            CinemaConstants.PURCHASE_ROW,
            CinemaConstants.PURCHASE_COLUMN,
            CinemaHelpers.seatPrice(CinemaConstants.PURCHASE_COLUMN, CinemaConstants.PURCHASE_ROW),
            true
        )
        val mockResponse = Ticket(seat=stubSeat)

        Mockito.`when`(cinemaService.purchaseTicket(mockRequest)).thenReturn(mockResponse)

        mockMvc.perform(MockMvcRequestBuilders
            .post("/purchase")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(mockRequest)))
            .andExpectAll(status().isOk, content().contentType(MediaType.APPLICATION_JSON), content().json(mapper.writeValueAsString(mockResponse)))
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