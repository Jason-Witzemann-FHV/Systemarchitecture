package readside.projection;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;

public interface IProjection {
    void processIncomingBookingCreatedEvent(BookingCreatedEvent event);
    void processIncomingBookingCanceledEvent(BookingCanceledEvent event);
}
