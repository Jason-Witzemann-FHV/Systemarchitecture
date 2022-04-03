package readside.integration;


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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {Projection.class, FreeRoomRepository.class, BookingRepository.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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


}
