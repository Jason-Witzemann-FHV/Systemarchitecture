package readside.infrastructure;

import readside.dto.BookedStay;
import java.util.Set;
import java.util.UUID;
import java.util.Optional;
import java.time.LocalDate;

public interface BookingRepositoryInterface {

    void addBooking(BookedStay booking);

    void cancelBooking(BookedStay booking);

    Set<BookedStay> bookingsBetween(LocalDate from ,LocalDate to);

    Optional<BookedStay> bookingById(UUID bookingId);


}
