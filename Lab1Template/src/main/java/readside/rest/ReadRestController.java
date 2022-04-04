package readside.rest;

import eventside.event.BookingCreatedEvent;
import eventside.event.BookingCanceledEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import readside.dto.BookedStay;
import readside.dto.FreeRoom;
import readside.infrastructure.BookingRepositoryInterface;
import readside.infrastructure.FreeRoomRepositoryInterface;
import readside.projection.IProjection;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin(origins = {"http://localhost:8080"})
public class ReadRestController {

    @Autowired // Todo autowire interface
    private IProjection projection;

    @Autowired
    private FreeRoomRepositoryInterface freeRoomRepository;

    @Autowired
    private BookingRepositoryInterface bookingRepository;

    @PostMapping(value = "/createBooking", consumes = "application/json", produces = "application/json")
    public boolean bookingCreated(@RequestBody BookingCreatedEvent event) {
        System.out.println("Event received: " + event);
        projection.processIncomingBookingCreatedEvent(event);

        return true;
    }

    @PostMapping(value = "/cancelBooking", consumes = "application/json", produces = "application/json")
    public boolean bookingCanceled(@RequestBody BookingCanceledEvent event) {
        System.out.println("Event received: " + event);
        projection.processIncomingBookingCanceledEvent(event);
        return true;
    }

    @GetMapping(value = "/freeRooms") // 2022-02-22
    public List<FreeRoom> getFreeRooms(@RequestParam String from, @RequestParam String to, @RequestParam int numberOfPeople) {
        LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);

        return freeRoomRepository.getAvailableRooms(fromDate, toDate, numberOfPeople);
    }

    @GetMapping(value = "/bookings") // 2022-02-22
    public Set<BookedStay> getFreeRooms(@RequestParam String from, @RequestParam String to) {
        LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ISO_DATE);
        LocalDate toDate = LocalDate.parse(to, DateTimeFormatter.ISO_DATE);

        return bookingRepository.bookingsBetween(fromDate, toDate);
    }
}
