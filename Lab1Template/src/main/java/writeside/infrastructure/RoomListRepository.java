package writeside.infrastructure;

import org.springframework.stereotype.Repository;
import writeside.domain.model.Room;
import writeside.domain.repository.RoomRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class RoomListRepository implements RoomRepository {
    private final Set<Room> rooms = new HashSet<>();

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
