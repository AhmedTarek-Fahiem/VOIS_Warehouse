package com.vois.warehouse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vois.warehouse.data.DeviceData;
import com.vois.warehouse.enumerator.DeviceStatus;
import com.vois.warehouse.model.Device;
import com.vois.warehouse.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class DeviceIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private DeviceRepository deviceRepository;

    private Device device1;
    private Device device2;


    @BeforeEach
    @Transactional
    public void setUp() {
        device1 = Device.builder()
                .name("Device 01")
                .pin("1000001")
                .temperature(-1)
                .status(DeviceStatus.READY)
                .build();
        device1 = deviceRepository.save(device1);

        device2 = Device.builder()
                .name("Device 02")
                .pin("1000002")
                .temperature(5)
                .status(DeviceStatus.ACTIVE)
                .build();
        device2 = deviceRepository.save(device2);
    }

    @Test
    @Transactional
    void getAllSaleDevices() throws Exception {
        this.mockMvc
                .perform(get("/device")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].name").value(device2.getName()))
                .andExpect(jsonPath("$[*].temperature").value(device2.getTemperature()))
                .andExpect(jsonPath("$[*].status").value(device2.getStatus().toString()))
                .andReturn();
    }

    @Test
    @Transactional
    void addDevice() throws Exception {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 03")
                .pin("1000003")
                .build();

        this.mockMvc
                .perform(post("/device")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceData))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(deviceData.getName()))
                .andExpect(jsonPath("$.temperature").value(-1.0))
                .andExpect(jsonPath("$.status").value(DeviceStatus.READY.toString()))
                .andReturn();
    }

    @Test
    @Transactional
    void addExistingDevice() throws Exception {
        DeviceData deviceData = DeviceData.builder()
                .name("Device 02")
                .pin("1000002")
                .build();

        this.mockMvc
                .perform(post("/device")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceData))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Transactional
    void updateDevice() throws Exception {
        DeviceData deviceData1 = DeviceData.builder()
                .name("Device 001")
                .pin("1000001")
                .build();

        this.mockMvc
                .perform(put("/device/" + device1.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceData1))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(deviceData1.getName()))
                .andExpect(jsonPath("$.temperature").value(-1))
                .andExpect(jsonPath("$.status").value(DeviceStatus.READY.toString()))
                .andReturn();
    }

    @Test
    @Transactional
    void updateNotExistingDevice() throws Exception {
        DeviceData deviceData1 = DeviceData.builder()
                .name("Device 04")
                .pin("1000004")
                .build();

        this.mockMvc
                .perform(put("/device/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceData1))
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @Transactional
    void updateWithExistingDeviceData() throws Exception {
        DeviceData deviceData1 = DeviceData.builder()
                .name("Device 02")
                .pin("1000001")
                .build();

        this.mockMvc
                .perform(put("/device/" + device2.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deviceData1))
                )
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    @Transactional
    void deleteDevice() throws Exception {
        this.mockMvc
                .perform(delete("/device/" + device1.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @Transactional
    void deleteNotExistingDevice() throws Exception {
        this.mockMvc
                .perform(delete("/device/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
