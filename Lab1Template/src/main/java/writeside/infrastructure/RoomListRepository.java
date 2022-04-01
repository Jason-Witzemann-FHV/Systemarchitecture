package writeside.infrastructure;

import org.springframework.stereotype.Repository;
import writeside.domain.model.Room;
import writeside.domain.repository.RoomRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class RoomListRepository implements RoomRepository {
    private final Set<Room> rooms = Set.of(
            new Room("S_001", 1),
            new Room("S_002", 1),
            new Room("S_003", 1),
            new Room("S_004", 1),
            new Room("S_005", 1),
            new Room("D_001", 2),
            new Room("D_002", 2),
            new Room("D_003", 2),
            new Room("D_004", 2),
            new Room("D_005", 2)
    );

    @Override
    public void createRoom(Room room) {
        rooms.add(room);
    }

    @Override
    public Optional<Room> getRoom(String roomNumber) {
        return rooms.stream().filter(r -> r.getRoomNumber().equals(roomNumber))
                .findFirst();
    }
}
