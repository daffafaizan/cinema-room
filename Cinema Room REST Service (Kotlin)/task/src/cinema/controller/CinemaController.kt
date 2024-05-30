package cinema.controller

import cinema.dto.CinemaResponseDTO
import cinema.service.CinemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CinemaController(val cinemaService: CinemaService) {

    @GetMapping("/seats")
    fun getSeats(): CinemaResponseDTO {
        return cinemaService.getAllSeats()
    }
}