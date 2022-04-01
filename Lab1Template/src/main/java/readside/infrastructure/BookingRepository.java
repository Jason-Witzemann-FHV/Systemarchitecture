package readside.infrastructure;


import org.springframework.stereotype.Repository;
import readside.dto.BookedStay;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

@Repository
public class BookingRepository implements BookingRepositoryInterface{


    private Set<BookedStay> bookings = new HashSet<>();

    @Override
    public void addBooking(BookedStay booking) {
        this.bookings.add(booking);
    }

    @Override
    public void cancelBooking(BookedStay booking) {
        this.bookings.remove(booking);
    }

    @Override
    public Set<BookedStay> bookingsBetween(LocalDate from, LocalDate to) {
        return bookings.stream()
                .filter(b -> !b.getFrom().isBefore(from))
                .filter(b -> !b.getTo().isAfter(to))
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<BookedStay> bookingById(UUID bookingId) {
        return bookings.stream().filter(b -> b.getBookingId().equals(bookingId)).findFirst();
    }
}
