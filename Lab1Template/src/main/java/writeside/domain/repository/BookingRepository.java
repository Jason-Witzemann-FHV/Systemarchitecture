package writeside.domain.repository;

import writeside.domain.model.Booking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {

    void createBooking(Booking booking);

    void cancelBooking(UUID bookingId) throws IllegalArgumentException;

    Optional<Booking> bookingById(UUID bookingId);
}
