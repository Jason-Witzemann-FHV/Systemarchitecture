package readside.infrastructure;

import org.springframework.stereotype.Repository;
import readside.dto.FreeRoom;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FreeRoomRepository implements FreeRoomRepositoryInterface{

    Set<FreeRoom> freeRooms = new HashSet<>();


    public FreeRoomRepository() {
        freeRooms.add(new FreeRoom("S_001", 1, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("S_002", 1, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("S_003", 1, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("S_004", 1, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("S_005", 1, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("D_001", 2, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("D_002", 2, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("D_003", 2, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("D_004", 2, LocalDate.MIN, LocalDate.MAX));
        freeRooms.add(new FreeRoom("D_005", 2, LocalDate.MIN, LocalDate.MAX));
    }

    public List<FreeRoom> getBetween(LocalDate from, LocalDate to) {
        return getAvailableRooms(from, to, 0);
    }

    public List<FreeRoom> getAvailableRooms(LocalDate from, LocalDate to, int amountOfPeople) {
        return freeRooms.stream()
                .filter(f -> f.getAmountOfPeople() >= amountOfPeople)
                .filter(f -> !f.getFrom().isAfter(from))
                .filter(f -> !f.getTo().isBefore(to))
                .collect(Collectors.toList());
    }

    public void free(FreeRoom freeRoom) {
        freeRooms.add(freeRoom);
    }

    public void unfree(FreeRoom freeRoom) {
        freeRooms.remove(freeRoom);
    }

}
