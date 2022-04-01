package eventside.infrastructure;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import eventside.event.Event;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class SubscribedClient {

    private WebClient client;
    private String canceledUri;
    private String createdUri;

    public SubscribedClient(WebClient client, String createdUri, String canceledUri) {
        this.client = client;
        this.createdUri = createdUri;
        this.canceledUri = canceledUri;
    }

    public static SubscribedClient create(String host, String canceledUri, String createdUri) {
        return new SubscribedClient(WebClient.create(host), canceledUri, createdUri);
    }

    public void notify(Event event) {
        String uri = null;
        if (event instanceof BookingCreatedEvent) {
            uri = createdUri;
        } else if (event instanceof BookingCanceledEvent) {
            uri = canceledUri;
        }

        if (uri != null) {
            client.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(event), Event.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }

    }
}
