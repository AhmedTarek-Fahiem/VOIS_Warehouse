package com.vois.warehouse.mappers;

import com.vois.warehouse.data.DeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DeviceMapper {

    @Mapping(source = "device.id", target = "id")
    @Mapping(source = "device.name", target = "name")
    @Mapping(source = "device.pin", target = "pin")
    @Mapping(source = "device.temperature", target = "temperature")
    @Mapping(source = "device.status", target = "status")
    DeviceDetailsData toDeviceDetailsData(Device device);

    @Mapping(target = "name", source = "deviceData.name")
    @Mapping(target = "pin", source = "deviceData.pin")
    Device toDevice(DeviceData deviceData);
}
