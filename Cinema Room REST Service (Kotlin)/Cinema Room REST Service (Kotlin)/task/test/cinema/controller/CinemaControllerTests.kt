package cinema.controller

import cinema.service.CinemaService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc

@WebMvcTest
class CinemaControllerTests(@Autowired val mockMvc: MockMvc) {

    @MockBean
    private lateinit var cinemaService: CinemaService

    @Test
    fun testGetSeatsShouldReturn_WithStatus200() {
    }
}