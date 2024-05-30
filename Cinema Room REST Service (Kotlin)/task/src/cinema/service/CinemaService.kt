package cinema.service

import cinema.dto.CinemaResponseDTO
import cinema.dto.PurchaseSeatRequestDTO
import cinema.exception.OutOfBoundsException
import cinema.exception.TicketSoldException
import cinema.model.Seat
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

    fun purchaseTicket(request: PurchaseSeatRequestDTO): Seat {
        if (request.row > ROW || request.row <= 0 || request.column > COLUMN || request.column <= 0) {
            throw OutOfBoundsException("The number of a row or a column is out of bounds!")
        }

        val seat = cinemaRepo.getSeat(request.row, request.column)
        if (seat == null || seat.booked) {
            throw TicketSoldException("The ticket has been already purchased!")
        } else {
            seat.booked = true
            cinemaRepo.updateSeat(request.row, request.column, seat)

            return seat
        }
    }
}