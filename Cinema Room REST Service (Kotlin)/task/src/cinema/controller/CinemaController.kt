package cinema.controller

import cinema.dto.response.CinemaResponseDTO
import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.request.ReturnRequestDTO
import cinema.dto.response.ReturnResponseDTO
import cinema.dto.response.StatsResponseDTO
import cinema.model.Ticket
import cinema.service.CinemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CinemaController(val cinemaService: CinemaService) {

    @GetMapping("/seats")
    fun getSeats(): CinemaResponseDTO {
        return cinemaService.getAllSeats()
    }

    @PostMapping("/purchase")
    fun purchaseSeat(@RequestBody request: PurchaseSeatRequestDTO): Ticket {
        return cinemaService.purchaseTicket(request)
    }

    @PostMapping("/return")
    fun returnTicket(@RequestBody request: ReturnRequestDTO): ReturnResponseDTO {
        return cinemaService.returnTicket(request)
    }

    @GetMapping("/stats")
    fun getStats(@RequestParam(required = false) password: String?): StatsResponseDTO {
        return cinemaService.getStats(password)
    }
}