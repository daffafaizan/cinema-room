package cinema.controller

import cinema.dto.CinemaResponseDTO
import cinema.dto.PurchaseSeatRequestDTO
import cinema.model.Seat
import cinema.service.CinemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CinemaController(val cinemaService: CinemaService) {

    @GetMapping("/seats")
    fun getSeats(): CinemaResponseDTO {
        return cinemaService.getAllSeats()
    }

    @PostMapping("/purchase")
    fun purchaseSeat(@RequestBody request: PurchaseSeatRequestDTO): Seat {
        return cinemaService.purchaseTicket(request)
    }
}