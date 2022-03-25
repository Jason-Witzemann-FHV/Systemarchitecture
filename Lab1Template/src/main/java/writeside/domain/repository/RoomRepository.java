package writeside.domain.repository;

import writeside.domain.model.Room;

import java.util.Optional;

public interface RoomRepository {
    void createRoom(Room room);

    Optional<Room> getRoom(String roomNumber);
}
