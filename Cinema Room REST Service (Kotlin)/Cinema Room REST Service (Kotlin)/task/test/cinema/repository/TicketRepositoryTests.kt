package cinema.repository

import cinema.model.Seat
import cinema.model.Ticket
import cinema.utils.CinemaHelpers
import cinema.utils.RepositoryConstants
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TicketRepositoryTests {

    private lateinit var ticketRepository: TicketRepository
    private lateinit var seat: Seat
    private lateinit var ticket: Ticket

    @BeforeEach
    fun setUp() {
        ticketRepository = TicketRepository()

        seat = Seat(
            RepositoryConstants.ROW,
            RepositoryConstants.COLUMN,
            CinemaHelpers.seatPrice(RepositoryConstants.ROW, RepositoryConstants.COLUMN),
            false
        )
        ticket = Ticket(seat=seat)

        ticketRepository.saveTicket(ticket)
    }

    @Test
    fun testSaveTicket() {
        val newTicket = Ticket(seat=seat)
        ticketRepository.saveTicket(newTicket)

        val actualTicket = ticketRepository.getTicket(newTicket.token)
        assertEquals(newTicket, actualTicket)
    }

    @Test
    fun testGetTicketInvalidToken() {
        val ticket = ticketRepository.getTicket(RepositoryConstants.INVALID_TOKEN)

        assertTrue(ticket == null)
    }

    @Test
    fun testGetTicketValidToken() {
        val actualTicket = ticketRepository.getTicket(ticket.token)
        assertEquals(ticket, actualTicket)
    }

    @Test
    fun testUpdateTicket() {
        val seat = Seat(
            RepositoryConstants.ROW,
            RepositoryConstants.COLUMN,
            RepositoryConstants.NEW_PRICE,
            true
        )
        val ticket = ticketRepository.getTicket(ticket.token)
        if (ticket != null) {
            ticket.seat = seat

            ticketRepository.updateTicket(ticket)

            val actualTicket = ticketRepository.getTicket(ticket.token)
            assertEquals(ticket, actualTicket)
            if (actualTicket != null) {
                assertEquals(seat, actualTicket.seat)
            }
        }

    }
}