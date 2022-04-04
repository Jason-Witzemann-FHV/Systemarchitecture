package writeside.application.command;

import java.util.UUID;

public class CancelRoomCommand {
    private final UUID bookingId;

    public CancelRoomCommand(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public UUID getBookingId() {
        return bookingId;
    }
}
