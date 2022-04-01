package eventside.rest;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import eventside.infrastructure.EventRepository;
import eventside.event.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventRestController {

    @Autowired
    private EventRepository repository;

    @PostMapping(value = "/createBooking", consumes = "application/json", produces = "application/json")
    public boolean bookingCreated(@RequestBody BookingCreatedEvent event) {
        repository.processEvent(event);
        System.out.println("Event received: " + event);
        return true;
    }

    @PostMapping(value = "/cancelBooking", consumes = "application/json", produces = "application/json")
    public boolean bookingCanceled(@RequestBody BookingCanceledEvent event) {
        repository.processEvent(event);
        System.out.println("Event received: " + event);
        return true;
    }

    @PostMapping // eg "http://localhost/8080" "/createBooking/" "/cancelBooking/"
    public boolean subscribe(@RequestParam String host, @RequestParam String createdUri, @RequestParam String canceledUri) {
        repository.attach(host, createdUri, canceledUri);
        return true;
    }


}
