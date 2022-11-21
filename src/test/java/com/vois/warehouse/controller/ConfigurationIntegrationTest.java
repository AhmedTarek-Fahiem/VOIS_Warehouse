package com.vois.warehouse.controller;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ConfigurationIntegrationTest {

    @Resource
    private MockMvc mockMvc;

    @Resource
    private DeviceRepository deviceRepository;

    private Device device1;

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

        Device device2 = Device.builder()
                .name("Device 02")
                .pin("1000002")
                .temperature(5)
                .status(DeviceStatus.ACTIVE)
                .build();
        deviceRepository.save(device2);
    }

    @Test
    void getReadyDevices() throws Exception {
        this.mockMvc
                .perform(get("/config/devices")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].id").isNotEmpty())
                .andExpect(jsonPath("$[*].name").value(device1.getName()))
                .andExpect(jsonPath("$[*].temperature").value(device1.getTemperature()))
                .andExpect(jsonPath("$[*].status").value(device1.getStatus().toString()))
                .andReturn();
    }

    @Test
    @Transactional
    void configureDevice() throws Exception {
        this.mockMvc
                .perform(put("/config/device/" + device1.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\": 8.5}")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value(device1.getName()))
                .andExpect(jsonPath("$.temperature").value(8.5))
                .andExpect(jsonPath("$.status").value(DeviceStatus.ACTIVE.toString()))
                .andReturn();
    }

    @Test
    @Transactional
    void configureNotExistingDevice() throws Exception {
        this.mockMvc
                .perform(put("/config/device/" + UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"temperature\": 8.5}")
                )
                .andExpect(status().isNotFound())
                .andReturn();
    }
}
