package com.vois.warehouse.repository;

import com.vois.warehouse.enumerator.DeviceStatus;
import com.vois.warehouse.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findDeviceByNameIgnoreCase(String name);
    Optional<Device> findDeviceByPin(String pin);
    List<Device> findAllByStatusOrderByPinDesc(DeviceStatus status);

}
