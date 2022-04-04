package writeside.application;

import org.springframework.stereotype.Service;
import writeside.application.command.BookRoomsCommand;
import writeside.application.command.CancelRoomCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public interface BookingService {

    boolean book(BookRoomsCommand bookRooms);

    boolean cancel(CancelRoomCommand cancelRoom);
}
