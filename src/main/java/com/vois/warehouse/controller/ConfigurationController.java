package com.vois.warehouse.controller;

import com.vois.warehouse.data.ConfigureDeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.service.DeviceConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/config")
public class ConfigurationController {

    @Resource
    private DeviceConfigurationService deviceConfigurationService;

    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDetailsData>> getReadyDevices() {
        log.info("Retrieving all devices needs to be configured");
        return ResponseEntity.ok().body(deviceConfigurationService.getReadyDevices());
    }

    @PutMapping("/device/{id}")
    public ResponseEntity<DeviceDetailsData> configureDevice(@PathVariable String id,
                                                          @Valid @RequestBody ConfigureDeviceData configureDeviceData) {
        log.info("Configure device {}", id);
        try {
            final DeviceDetailsData deviceDetailsData = deviceConfigurationService.configureDevice(id, configureDeviceData);
            return ResponseEntity.ok().body(deviceDetailsData);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return errors;
    }

}
