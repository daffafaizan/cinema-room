package cinema.service

import cinema.dto.response.CinemaResponseDTO
import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.request.ReturnRequestDTO
import cinema.dto.response.ReturnResponseDTO
import cinema.exception.OutOfBoundsException
import cinema.exception.TicketSoldException
import cinema.exception.WrongTokenException
import cinema.model.Ticket
import cinema.repository.CinemaRepository
import cinema.repository.TicketRepository
import org.springframework.stereotype.Service

const val ROW = 9
const val COLUMN = 9

@Service
class CinemaService(val cinemaRepository: CinemaRepository, val ticketRepository: TicketRepository) {

    fun getAllSeats(): CinemaResponseDTO {
        val seats = cinemaRepository.getAllSeats()
        return CinemaResponseDTO(
            ROW,
            COLUMN,
            seats
        )
    }

    fun purchaseTicket(request: PurchaseSeatRequestDTO): Ticket {
        if (request.row > ROW || request.row <= 0 || request.column > COLUMN || request.column <= 0) {
            throw OutOfBoundsException("The number of a row or a column is out of bounds!")
        }

        val seat = cinemaRepository.getSeat(request.row, request.column)
        if (seat == null || seat.booked) {
            throw TicketSoldException("The ticket has been already purchased!")
        } else {
            seat.booked = true
            val ticket = Ticket(seat=seat)
            cinemaRepository.updateSeat(request.row, request.column, seat)
            ticketRepository.saveTicket(ticket)

            return ticket
        }
    }

    fun returnTicket(request: ReturnRequestDTO): ReturnResponseDTO {
        val ticket = ticketRepository.getTicket(request.token)
        if (ticket == null) {
            throw WrongTokenException("Wrong token!")
        } else {
            if (ticket.seat.booked) {
                ticket.seat.booked = false
                cinemaRepository.updateSeat(ticket.seat.row, ticket.seat.column, ticket.seat)
                ticketRepository.updateTicket(request.token, ticket)

                return ReturnResponseDTO(
                    ticket.seat
                )
            } else {
                throw WrongTokenException("Wrong token!")
            }
        }
    }
}