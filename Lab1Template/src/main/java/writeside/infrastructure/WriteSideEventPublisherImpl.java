package writeside.infrastructure;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import writeside.domain.repository.WriteSideEventPublisher;

@Repository
public class WriteSideEventPublisherImpl implements WriteSideEventPublisher {

    private final WebClient localApiClient = WebClient.create("http://localhost:8080");

    @Override
    public Boolean publishBookingCreatedEvent(BookingCreatedEvent event) {
        System.out.println("Event published: " + event);
        return localApiClient
                .post()
                .uri("/createBooking/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(event), BookingCreatedEvent.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

    @Override
    public Boolean publishBookingCanceledEvent(BookingCanceledEvent event) {
        System.out.println("Event published: " + event);
        return localApiClient
                .post()
                .uri("/cancelBooking/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(event), BookingCanceledEvent.class)
                .retrieve()
                .bodyToMono(Boolean.class)
                .block();
    }

}