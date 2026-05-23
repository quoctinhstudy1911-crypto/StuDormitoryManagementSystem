package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.room.entity.*;
import com.stu.dormitory.modules.room.enums.*;
import com.stu.dormitory.modules.room.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RoomDataSeeder implements CommandLineRunner {

    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;

    @Override
    public void run(String... args) {
        if (buildingRepository.count() > 0) {
            log.info("Room data already exists, skip seeding.");
            return;
        }

        log.info("Seeding room data...");

        // Tạo tòa nhà A
        Building building = new Building();
        building.setCode("A");
        building.setName("KTX A");
        building.setDescription("Khu ký túc xá chính");
        building.setStatus(BuildingStatus.ACTIVE);
        building = buildingRepository.save(building);

        // Tầng 1 - Nam
        createFloorWithRooms(building, 1, OccupancyPolicy.MALE);
        // Tầng 2 - Nữ
        createFloorWithRooms(building, 2, OccupancyPolicy.FEMALE);

        log.info("Seeded room data successfully.");
    }

    private void createFloorWithRooms(Building building, int floorNumber, OccupancyPolicy policy) {
        Floor floor = new Floor();
        floor.setBuilding(building);
        floor.setFloorNumber(floorNumber);
        floor.setOccupancyPolicy(policy);
        floor = floorRepository.save(floor);

        for (int i = 1; i <= 10; i++) {
            String roomCode = "A" + floorNumber + String.format("%02d", i);
            Room room = new Room();
            room.setRoomCode(roomCode);
            room.setCapacity(20);
            room.setOccupiedBeds(0);
            room.setMonthlyFee(BigDecimal.valueOf(500000.0));
            room.setStatus(RoomStatus.AVAILABLE);
            room.setFloor(floor);
            room = roomRepository.save(room);

            for (int j = 1; j <= 20; j++) {
                Bed bed = new Bed();
                bed.setBedCode(roomCode + "-B" + String.format("%02d", j));
                bed.setStatus(BedStatus.AVAILABLE);
                bed.setRoom(room);
                bedRepository.save(bed);
            }
        }
    }
}