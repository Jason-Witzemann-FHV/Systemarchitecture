package writeside.domain.repository;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;

public interface WriteSideEventPublisher {

    Boolean publishBookingCreatedEvent(BookingCreatedEvent event);

    Boolean publishBookingCanceledEvent(BookingCanceledEvent event);

}
