package com.vois.warehouse.service;

import com.vois.warehouse.data.ConfigureDeviceData;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
class DeviceConfigurationServiceUnitTest {

    @Resource
    private DeviceConfigurationService deviceConfigurationService;

    @MockBean
    private DeviceRepository deviceRepository;

    @Test
    void getReadyDevices() {
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("100001")
                .status(DeviceStatus.READY)
                .temperature(-1.0)
                .build();
        List<DeviceDetailsData> devicesDataList = Stream.of(deviceModel)
                .map(device -> DeviceDetailsData.builder()
                        .id(device.getId().toString())
                        .name(device.getName())
                        .temperature(device.getTemperature())
                        .status(device.getStatus().toString())
                        .build())
                .toList();

        Mockito.when(deviceRepository.findAllByStatusOrderByPinDesc(DeviceStatus.READY))
                .thenReturn(List.of(deviceModel));

        List<DeviceDetailsData> deviceDetailsDataList = deviceConfigurationService.getReadyDevices();

        assertThat(deviceDetailsDataList).hasSameElementsAs(devicesDataList);
    }

    @Test
    void configureDevice() {
        Device deviceModel = Device.builder()
                .id(UUID.randomUUID())
                .name("Device 01")
                .pin("100001")
                .status(DeviceStatus.READY)
                .temperature(-1.0)
                .build();
        ConfigureDeviceData configureDeviceData = ConfigureDeviceData.builder().temperature(8.5).build();
        DeviceDetailsData deviceData = DeviceDetailsData.builder()
                        .id(deviceModel.getId().toString())
                        .name(deviceModel.getName())
                        .temperature(configureDeviceData.getTemperature())
                        .status(DeviceStatus.ACTIVE.toString())
                        .build();

        Mockito.when(deviceRepository.findById(deviceModel.getId()))
                .thenReturn(Optional.of(deviceModel));
        Mockito.when(deviceRepository.save(Mockito.any(Device.class))).thenReturn(deviceModel);

        DeviceDetailsData deviceDetailsData = deviceConfigurationService.configureDevice(deviceModel.getId().toString(), configureDeviceData);

        assertThat(deviceDetailsData).usingRecursiveComparison().isEqualTo(deviceData);

    }
}
