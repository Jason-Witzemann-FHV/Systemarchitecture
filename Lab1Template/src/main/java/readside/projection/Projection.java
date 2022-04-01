package readside.projection;

import eventside.event.BookingCanceledEvent;
import eventside.event.BookingCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import readside.dto.BookedStay;
import readside.dto.FreeRoom;
import readside.infrastructure.BookingRepositoryInterface;
import readside.infrastructure.FreeRoomRepositoryInterface;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Projection implements IProjection{

    @Autowired
    FreeRoomRepositoryInterface freeRoomRepository;

    @Autowired
    BookingRepositoryInterface bookingRepository;

    public void processIncomingBookingCreatedEvent(BookingCreatedEvent event) {
        List<FreeRoom> freeRooms = freeRoomRepository.getBetween(event.getArrivalDate(), event.getDepartureDate());
        List<FreeRoom> adaptedRooms = new ArrayList<>();

        freeRooms.stream()
                .filter(r -> event.getRooms().contains(r.getRoomnumber()))
                .forEach(r -> {
                    if(!event.getArrivalDate().isEqual(r.getFrom())) {
                        adaptedRooms.add(new FreeRoom(
                                r.getRoomnumber(),
                                r.getAmountOfPeople(),
                                r.getFrom(),
                                event.getArrivalDate()
                        ));
                    }
                    if (!event.getDepartureDate().isEqual(r.getTo())) {
                        adaptedRooms.add(new FreeRoom(
                                r.getRoomnumber(),
                                r.getAmountOfPeople(),
                                event.getDepartureDate(),
                                r.getTo()
                        ));
                    }
                    freeRoomRepository.unfree(r);
                });

        adaptedRooms.forEach(r -> freeRoomRepository.free(r));


        BookedStay bookedStay = new BookedStay(
                event.getBookingId(),
                event.getArrivalDate(),
                event.getDepartureDate(),
                event.getRooms(),
                event.getCustomer()
        );
        bookingRepository.addBooking(bookedStay);

    }

    public void processIncomingBookingCanceledEvent(BookingCanceledEvent event) {

        bookingRepository.bookingById(event.getBookingId()).ifPresent(bookingRepository::cancelBooking);


        List<FreeRoom> edgeCases = freeRoomRepository.getBetween(event.getArrivalDate(), event.getDepartureDate())
                .stream()
                .filter(r -> event.getRooms().containsKey(r.getRoomnumber()))
                .collect(Collectors.toList());

        event.getRooms()
                .keySet()
                .forEach(room -> {
                    List<FreeRoom> tempList = edgeCases.stream()
                            .filter(free -> free.getRoomnumber().equals(room))
                            .collect(Collectors.toList());
                    if (tempList.isEmpty()) {
                        freeRoomRepository.free(new FreeRoom(
                                room,
                                event.getRooms().get(room),
                                event.getArrivalDate(),
                                event.getDepartureDate()
                        ));
                    } else {
                        freeRoomRepository.free(new FreeRoom(
                                room,
                                event.getRooms().get(room),
                                LocalDate.ofEpochDay(Math.min(event.getArrivalDate()
                                        .toEpochDay(), tempList.stream()
                                        .min(Comparator.comparing(FreeRoom::getFrom))
                                        .get()
                                        .getFrom().toEpochDay())),
                                LocalDate.ofEpochDay(Math.max(event.getDepartureDate()
                                        .toEpochDay(), tempList.stream()
                                        .max(Comparator.comparing(FreeRoom::getFrom))
                                        .get()
                                        .getFrom().toEpochDay()))
                                ));
                        tempList.forEach(r -> freeRoomRepository.unfree(r));
                    }
                });
    }
}
