package writeside.application;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public interface BookingService {

    boolean book(String scnr, List<String> rooms, LocalDate arrivalDate, LocalDate departureDate);

    boolean cancel(UUID bookingId);
}
