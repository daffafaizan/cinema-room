package cinema.service

import cinema.dto.CinemaResponseDTO
import cinema.repository.CinemaRepository
import org.springframework.stereotype.Service

const val ROW = 9
const val COLUMN = 9

@Service
class CinemaService(val cinemaRepo: CinemaRepository) {

    fun getAllSeats(): CinemaResponseDTO {
        val seats = cinemaRepo.getAllSeats()
        return CinemaResponseDTO(
            ROW,
            COLUMN,
            seats
        )
    }
}