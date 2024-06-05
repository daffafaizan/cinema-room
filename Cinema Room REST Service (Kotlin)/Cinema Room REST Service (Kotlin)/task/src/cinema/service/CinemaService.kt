package cinema.service

import cinema.dto.response.CinemaResponseDTO
import cinema.dto.request.PurchaseSeatRequestDTO
import cinema.dto.request.ReturnRequestDTO
import cinema.dto.response.ReturnResponseDTO
import cinema.dto.response.StatsResponseDTO
import cinema.exception.OutOfBoundsException
import cinema.exception.TicketSoldException
import cinema.exception.WrongPasswordException
import cinema.exception.WrongTokenException
import cinema.model.Ticket
import cinema.repository.CinemaRepository
import cinema.repository.TicketRepository
import cinema.utils.CinemaInitConstants.COLUMN
import cinema.utils.CinemaInitConstants.ROW
import org.springframework.stereotype.Service

const val PASSWORD = "super_secret"

@Service
class CinemaService(val cinemaRepository: CinemaRepository, val ticketRepository: TicketRepository) {

    fun getAllSeats(): CinemaResponseDTO {
        val seats = cinemaRepository.getAvailableSeats()
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
            cinemaRepository.updateSeat(seat)
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
                cinemaRepository.updateSeat(ticket.seat)
                ticketRepository.updateTicket(ticket)

                return ReturnResponseDTO(
                    ticket.seat
                )
            } else {
                throw WrongTokenException("Wrong token!")
            }
        }
    }

    fun getStats(password: String?): StatsResponseDTO {
        if (password == null || password != PASSWORD) {
            throw WrongPasswordException("The password is wrong!")
        }

        val current_income = cinemaRepository.getIncome()
        val number_of_available_seats = cinemaRepository.getAvailableSeats().count()
        val number_of_purchased_tickets = cinemaRepository.getPurchasedSeats().count()

        return StatsResponseDTO(
            current_income,
            number_of_available_seats,
            number_of_purchased_tickets
        )
    }
}