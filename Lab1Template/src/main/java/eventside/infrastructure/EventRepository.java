package eventside.infrastructure;

import eventside.event.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class EventRepository {

    private List<Event> events = new ArrayList<>();

    private Map<String, SubscribedClient> subscriber = Map.of("http://localhost:8082", SubscribedClient.create("http://localhost:8082", "/createBooking", "/cancelBooking"));

    public void processEvent(Event event) {
        events.add(event);
        subscriber.values().forEach(sub -> sub.notify(event));
    }

    public void attach(String host, String createdUri, String canceledUri) {
        SubscribedClient sub = SubscribedClient.create(host, createdUri, canceledUri);
        subscriber.put(host, sub);
        events.forEach(sub::notify);
    }

    public void detach(String host) {
        subscriber.remove(host);
    }
}
