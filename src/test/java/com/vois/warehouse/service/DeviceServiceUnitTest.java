package com.vois.warehouse.service;

import com.vois.warehouse.data.DeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.enumerator.DeviceStatus;
import com.vois.warehouse.model.Device;
import com.vois.warehouse.repository.DeviceRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class DeviceServiceUnitTest {

    @Resource
    private DeviceService deviceService;

    @MockBean
    private DeviceRepository deviceRepository;

    @Test
    void getDevicesForSale() {
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("100001")
                .status(DeviceStatus.ACTIVE)
                .temperature(8.5)
                .build();
        List<DeviceDetailsData> devicesDataList = Stream.of(deviceModel)
                .map(device -> DeviceDetailsData.builder()
                        .id(device.getId().toString())
                        .name(device.getName())
                        .temperature(device.getTemperature())
                        .status(device.getStatus().toString())
                        .build())
                .toList();

        Mockito.when(deviceRepository.findAllByStatusOrderByPinDesc(DeviceStatus.ACTIVE))
                .thenReturn(List.of(deviceModel));

        List<DeviceDetailsData> deviceDetailsDataList = deviceService.getDevicesForSale();

        assertThat(deviceDetailsDataList).hasSameElementsAs(devicesDataList);

    }

    @Test
    void createDevice() {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 01")
                .pin("1000001")
                .build();
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name(deviceData.getName())
                .pin(deviceData.getPin())
                .build();
        DeviceDetailsData expectedDeviceDetailsData = DeviceDetailsData.builder()
                .id(deviceModel.getId().toString())
                .name(deviceModel.getName())
                .temperature(deviceModel.getTemperature())
                .status(deviceModel.getStatus().toString())
                .build();

        Mockito.when(deviceRepository.findDeviceByNameIgnoreCase(deviceData.getName())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.findDeviceByPin(deviceData.getPin())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(deviceModel);

        DeviceDetailsData deviceDetailsData = deviceService.createDevice(deviceData);

        assertThat(deviceDetailsData).usingRecursiveComparison().isEqualTo(expectedDeviceDetailsData);
    }

    @Test
    void createExistingDevice() {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 01")
                .pin("1000001")
                .build();
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name(deviceData.getName())
                .pin(deviceData.getPin())
                .build();

        Mockito.when(deviceRepository.findDeviceByNameIgnoreCase(deviceData.getName())).thenReturn(Optional.of(deviceModel));
        Mockito.when(deviceRepository.findDeviceByPin(deviceData.getPin())).thenReturn(Optional.of(deviceModel));
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(deviceModel);

        final Throwable raisedException = catchThrowable(() -> deviceService.createDevice(deviceData));
        assertThat(raisedException).isInstanceOf(NonUniqueResultException.class);
    }

    @Test
    void updateDevice() {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 001")
                .pin("1000001")
                .build();
        Device oldDeviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("1000001")
                .build();
        Device newDeviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name(deviceData.getName())
                .pin(deviceData.getPin())
                .build();
        DeviceDetailsData expectedDeviceDetailsData = DeviceDetailsData.builder()
                .id(newDeviceModel.getId().toString())
                .name(newDeviceModel.getName())
                .temperature(newDeviceModel.getTemperature())
                .status(newDeviceModel.getStatus().toString())
                .build();

        Mockito.when(deviceRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(oldDeviceModel));
        Mockito.when(deviceRepository.findDeviceByNameIgnoreCase(deviceData.getName())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.findDeviceByPin(deviceData.getPin())).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(newDeviceModel);

        DeviceDetailsData deviceDetailsData = deviceService.updateDevice(UUID.randomUUID().toString(), deviceData);

        assertThat(deviceDetailsData).usingRecursiveComparison().isEqualTo(expectedDeviceDetailsData);
    }

    @Test
    void updateNotExistingDevice() {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 001")
                .pin("1000001")
                .build();
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("1000001")
                .build();

        Mockito.when(deviceRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());
        Mockito.when(deviceRepository.findDeviceByNameIgnoreCase(deviceData.getName())).thenReturn(Optional.of(deviceModel));
        Mockito.when(deviceRepository.findDeviceByPin(deviceData.getPin())).thenReturn(Optional.of(deviceModel));
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(deviceModel);

        final Throwable raisedException = catchThrowable(() -> deviceService.updateDevice(UUID.randomUUID().toString(), deviceData));
        assertThat(raisedException).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateWithExistingDevice() {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 02")
                .pin("1000001")
                .build();
        Device deviceModel1 = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("1000001")
                .build();
        Device deviceModel2 = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 02")
                .pin("1000002")
                .build();

        Mockito.when(deviceRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.of(deviceModel1));
        Mockito.when(deviceRepository.findDeviceByNameIgnoreCase(deviceData.getName())).thenReturn(Optional.of(deviceModel2));
        Mockito.when(deviceRepository.findDeviceByPin(deviceData.getPin())).thenReturn(Optional.of(deviceModel2));
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(deviceModel1);

        final Throwable raisedException = catchThrowable(() -> deviceService.updateDevice(UUID.randomUUID().toString(), deviceData));
        assertThat(raisedException).isInstanceOf(NonUniqueResultException.class);
    }
}
