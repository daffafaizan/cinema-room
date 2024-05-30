package cinema.repository

import cinema.model.Seat
import cinema.model.Ticket
import org.springframework.stereotype.Repository

@Repository
class TicketRepository {
    private final val tickets = mutableListOf<Ticket>()

    fun saveTicket(ticket: Ticket) {
        tickets.add(ticket)
    }

    fun getTicket(token: String): Ticket? {
        return tickets.find { it.token == token }
    }

    fun updateTicket(token: String, ticket: Ticket) {
        val index = tickets.indexOfFirst { it.token == token }
        tickets[index] = ticket
    }
}