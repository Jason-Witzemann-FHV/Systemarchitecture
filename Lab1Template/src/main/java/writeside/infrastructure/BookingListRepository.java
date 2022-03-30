package writeside.infrastructure;

import org.springframework.stereotype.Repository;
import writeside.domain.model.Booking;
import writeside.domain.repository.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class BookingListRepository implements BookingRepository {
    private final List<Booking> bookings = new ArrayList<>();

    @Override
    public void createBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public void cancelBooking(UUID bookingId) throws IllegalArgumentException{
        bookings.stream()
                .filter(b -> b.getBookingId().equals(bookingId))
                .findFirst()
                .ifPresentOrElse(bookings::remove, () -> {throw new IllegalArgumentException("booking with id " + bookingId + " not found");});
    }
}
