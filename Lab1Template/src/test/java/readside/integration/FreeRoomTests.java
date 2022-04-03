package readside.integration;


import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import readside.infrastructure.BookingRepository;
import readside.infrastructure.FreeRoomRepository;
import readside.infrastructure.FreeRoomRepositoryInterface;
import readside.projection.IProjection;
import readside.projection.Projection;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {Projection.class, FreeRoomRepository.class, BookingRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // should reset repo after every test
class FreeRoomTests {

    @Autowired
    private IProjection projection;

    @Autowired
    private FreeRoomRepositoryInterface repo;


    private BookingCreatedEvent createEvent(int dayFrom, int dayTo) {
        return new BookingCreatedEvent(
                UUID.randomUUID(),
                LocalDate.of(2022, 3, dayFrom),
                LocalDate.of(2022, 3, dayTo),
                Set.of("S_001"),
                "Noah");
    }

    private BookingCanceledEvent canceledEventFromCreateEvent(BookingCreatedEvent e) {
        return new BookingCanceledEvent(e.getBookingId(), e.getArrivalDate(), e.getDepartureDate(), Map.of("S_001", 1));
    }

    // Single-Booking test

    @Test
    void given_booking_when_free_rooms_2022_03_02_to_2022_03_05_then_none() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 2);
        var to = LocalDate.of(2022, 3,5);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_booking_when_free_rooms_2022_03_01_to_2022_03_02_then_one() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,2);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_booking_when_free_rooms_2022_03_05_to_2022_03_10_then_one() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 5);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_booking_when_free_rooms_2022_03_03_to_2022_03_10_then_none() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 3);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_booking_when_free_rooms_2022_03_01_to_2022_03_03_then_none() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,3);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_booking_when_free_rooms_2022_03_01_to_2022_03_10_then_none() {

        var event = createEvent(2, 5);
        projection.processIncomingBookingCreatedEvent(event);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }


    // Double booking with no time between test


    @Test
    void given_two_bookings_when_free_rooms_2022_03_02_to_2022_03_05_then_none() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 2);
        var to = LocalDate.of(2022, 3,5);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_two_bookings_when_free_rooms_2022_03_01_to_2022_03_02_then_one() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,2);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_two_bookings_when_free_rooms_2022_03_05_to_2022_03_10_then_one() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 5);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_two_bookings_when_free_rooms_2022_03_03_to_2022_03_10_then_none() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 3);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_two_bookings_when_free_rooms_2022_03_01_to_2022_03_03_then_none() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,3);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

    @Test
    void given_two_bookings_when_free_rooms_2022_03_01_to_2022_03_10_then_none() {

        var event1 = createEvent(2, 3);
        var event2 = createEvent(3, 5);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,10);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }

   // 2 bookings with time between

    @Test
    void given_two_bookings_with_days_between_when_free_rooms_before_then_one() {
        var event1 = createEvent(3, 5);
        var event2 = createEvent(7, 10);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 1);
        var to = LocalDate.of(2022, 3,3);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_two_bookings_with_days_between_when_free_rooms_between_then_one() {
        var event1 = createEvent(3, 5);
        var event2 = createEvent(7, 10);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 5);
        var to = LocalDate.of(2022, 3,7);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_two_bookings_with_days_between_when_free_rooms_after_then_one() {
        var event1 = createEvent(3, 5);
        var event2 = createEvent(7, 10);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 10);
        var to = LocalDate.of(2022, 3,15);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
    }

    @Test
    void given_two_bookings_with_days_between_when_free_rooms_at_occupied_time_then_none() {
        var event1 = createEvent(3, 5);
        var event2 = createEvent(7, 10);
        projection.processIncomingBookingCreatedEvent(event1);
        projection.processIncomingBookingCreatedEvent(event2);

        var from = LocalDate.of(2022, 3, 5);
        var to = LocalDate.of(2022, 3,8);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isEmpty());
    }


    // cancellation tests


    @Test
    void given_only_one_booking_when_cancel_then_one_big_free_space() {
        var createdEvent = createEvent(10, 15);
        var canceledEvent = canceledEventFromCreateEvent(createdEvent);
        projection.processIncomingBookingCreatedEvent(createdEvent);
        projection.processIncomingBookingCanceledEvent(canceledEvent);

        var from = LocalDate.of(2022, 3, 10);
        var to = LocalDate.of(2022, 3,15);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
        assertEquals(LocalDate.MIN, response.get().getFrom());
        assertEquals(LocalDate.MAX, response.get().getTo());
    }

    @Test
    void given_two_bookings_next_to_each_other_booking_when_cancel_first_then_first_free_time_expands() {
        var createdEvent1 = createEvent(10, 15);
        var createdEvent2 = createEvent(15, 20);
        var canceledEvent = canceledEventFromCreateEvent(createdEvent1);
        projection.processIncomingBookingCreatedEvent(createdEvent1);
        projection.processIncomingBookingCreatedEvent(createdEvent2);
        projection.processIncomingBookingCanceledEvent(canceledEvent);

        var from = LocalDate.of(2022, 3, 10);
        var to = LocalDate.of(2022, 3,15);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
        assertEquals(LocalDate.MIN, response.get().getFrom());
        assertEquals(to, response.get().getTo());
    }

    @Test
    void given_two_bookings_next_to_each_other_booking_when_cancel_second_then_second_free_time_expands() {
        var createdEvent1 = createEvent(10, 15);
        var createdEvent2 = createEvent(15, 20);
        var canceledEvent = canceledEventFromCreateEvent(createdEvent2);
        projection.processIncomingBookingCreatedEvent(createdEvent1);
        projection.processIncomingBookingCreatedEvent(createdEvent2);
        projection.processIncomingBookingCanceledEvent(canceledEvent);

        var from = LocalDate.of(2022, 3, 15);
        var to = LocalDate.of(2022, 3,20);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
        assertEquals(from, response.get().getFrom());
        assertEquals(LocalDate.MAX, response.get().getTo());
    }

    @Test
    void given_three_bookings_next_to_each_other_booking_when_cancel_middle_then_free_space_created() {
        var createdEvent1 = createEvent(10, 15);
        var createdEvent2 = createEvent(15, 20);
        var createdEvent3 = createEvent(20, 25);
        var canceledEvent = canceledEventFromCreateEvent(createdEvent2);
        projection.processIncomingBookingCreatedEvent(createdEvent1);
        projection.processIncomingBookingCreatedEvent(createdEvent2);
        projection.processIncomingBookingCreatedEvent(createdEvent3);
        projection.processIncomingBookingCanceledEvent(canceledEvent);

        var from = LocalDate.of(2022, 3, 15);
        var to = LocalDate.of(2022, 3,20);

        var response = repo.getBetween(from, to).stream().filter(r -> r.getRoomnumber().equals("S_001")).findAny();

        assertTrue(response.isPresent());
        assertEquals(from, response.get().getFrom());
        assertEquals(to, response.get().getTo());
    }

}











