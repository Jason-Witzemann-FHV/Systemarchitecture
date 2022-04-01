package readside.infrastructure;

import readside.dto.FreeRoom;

import java.time.LocalDate;
import java.util.List;

public interface FreeRoomRepositoryInterface {

    public List<FreeRoom> getBetween(LocalDate from, LocalDate to);

    List<FreeRoom> getAvailableRooms(LocalDate from, LocalDate to, int amountOfPeople);

    void unfree(FreeRoom freeRoom);

    void free(FreeRoom freeRoom);
}
