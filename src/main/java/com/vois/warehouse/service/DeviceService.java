package com.vois.warehouse.service;

import com.vois.warehouse.data.DeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.enumerator.DeviceStatus;
import com.vois.warehouse.mappers.DeviceMapper;
import com.vois.warehouse.model.Device;
import com.vois.warehouse.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DeviceService {

    @Resource
    private DeviceRepository deviceRepository;

    @Resource
    private DeviceMapper deviceMapper;

    public List<DeviceDetailsData> getDevicesForSale() {
        return deviceRepository.findAllByStatusOrderByPinDesc(DeviceStatus.ACTIVE)
                .stream()
                .map(device -> deviceMapper.toDeviceDetailsData(device))
                .toList();
    }

    @Transactional
    public DeviceDetailsData createDevice(DeviceData deviceData) throws NonUniqueResultException {
        validateDeviceUniqueness(deviceData.getName(), deviceData.getPin());

        Device device = deviceMapper.toDevice(deviceData);
        device = deviceRepository.save(device);

        return deviceMapper.toDeviceDetailsData(device);
    }

    @Transactional
    public DeviceDetailsData updateDevice(String deviceId, DeviceData deviceData) throws IllegalArgumentException, EntityNotFoundException, NonUniqueResultException {
        UUID deviceUUID = UUID.fromString(deviceId);
        Device device = deviceRepository.findById(deviceUUID)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Device not found");
                });

        validateDeviceUniqueness(deviceUUID, deviceData.getName(), deviceData.getPin());

        device.setName(deviceData.getName());
        device.setPin(deviceData.getPin());
        device = deviceRepository.save(device);

        return deviceMapper.toDeviceDetailsData(device);
    }

    @Transactional
    public void deleteDevice(String deviceId) throws EntityNotFoundException {
        Device device = deviceRepository.findById(UUID.fromString(deviceId))
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Device not found");
                });

        deviceRepository.delete(device);
    }

    private void validateDeviceUniqueness(String name, String pin) {
        validateDeviceUniqueness(null, name, pin);
    }

    private void validateDeviceUniqueness(UUID id, String name, String pin) {
        Optional<Device> existDevice = deviceRepository.findDeviceByNameIgnoreCase(name);
        if (existDevice.isPresent() && (id == null || !id.equals(existDevice.get().getId())))
            throw new NonUniqueResultException("Device with same name is already exists");
        existDevice = deviceRepository.findDeviceByPin(pin);
        if (existDevice.isPresent() && (id == null || !id.equals(existDevice.get().getId())))
            throw new NonUniqueResultException("Use different PIN");
    }

}
