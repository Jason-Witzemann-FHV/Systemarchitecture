package writeside.application.command;

import java.time.LocalDate;
import java.util.List;

public class BookRoomsCommand {
    private final String socialSecurityNumber;
    private final List<String> rooms;
    private final LocalDate arrivalDate;
    private final LocalDate departureDate;

    public BookRoomsCommand(String socialSecurityNumber, List<String> rooms, LocalDate arrivalDate, LocalDate departureDate) {
        this.socialSecurityNumber = socialSecurityNumber;
        this.rooms = List.copyOf(rooms);
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public String getSocialSecurityNumber() {
        return socialSecurityNumber;
    }

    public List<String> getRooms() {
        return List.copyOf(rooms);
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }
}
