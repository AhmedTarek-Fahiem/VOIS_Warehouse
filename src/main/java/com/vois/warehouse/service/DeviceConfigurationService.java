package com.vois.warehouse.service;

import com.vois.warehouse.data.ConfigureDeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.enumerator.DeviceStatus;
import com.vois.warehouse.mappers.DeviceMapper;
import com.vois.warehouse.model.Device;
import com.vois.warehouse.repository.DeviceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class DeviceConfigurationService {

    @Resource
    private DeviceRepository deviceRepository;

    @Resource
    private DeviceMapper deviceMapper;

    public List<DeviceDetailsData> getReadyDevices() {
        return deviceRepository.findAllByStatusOrderByPinDesc(DeviceStatus.READY)
                .stream()
                .map(device -> deviceMapper.toDeviceDetailsData(device))
                .toList();
    }

    @Transactional
    public DeviceDetailsData configureDevice(String deviceId, ConfigureDeviceData configureDeviceData) throws IllegalArgumentException, EntityNotFoundException {
        UUID deviceUUID = UUID.fromString(deviceId);
        Device device = deviceRepository.findById(deviceUUID)
                .orElseThrow(() -> {
                    throw new EntityNotFoundException("Device not found");
                });

        if (DeviceStatus.ACTIVE.equals(device.getStatus())) {
            throw new IllegalArgumentException("Device already configured!");
        }

        device.setTemperature(configureDeviceData.getTemperature());
        device.setStatus(DeviceStatus.ACTIVE);
        device = deviceRepository.save(device);

        return deviceMapper.toDeviceDetailsData(device);
    }
}
