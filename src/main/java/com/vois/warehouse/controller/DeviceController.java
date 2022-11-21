package com.vois.warehouse.controller;

import com.vois.warehouse.data.DeviceData;
import com.vois.warehouse.data.DeviceDetailsData;
import com.vois.warehouse.service.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Resource
    private DeviceService deviceService;

    @GetMapping
    public ResponseEntity<List<DeviceDetailsData>> getAllSaleDevices() {
        log.info("Retrieving all devices available for sale");
        return ResponseEntity.ok().body(deviceService.getDevicesForSale());
    }

    @PostMapping
    public ResponseEntity<DeviceDetailsData> addDevice(@Valid @RequestBody DeviceData deviceData) {
        log.info("Adding new device {}", deviceData.getName());
        try {
            final DeviceDetailsData deviceDetailsData = deviceService.createDevice(deviceData);
            return ResponseEntity.ok().body(deviceDetailsData);
        } catch (NonUniqueResultException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDetailsData> updateDevice(@PathVariable String id,
                                                          @Valid @RequestBody DeviceData deviceData) {
        log.info("Updating device {}", deviceData.getName());
        try {
            final DeviceDetailsData deviceDetailsData = deviceService.updateDevice(id, deviceData);
            return ResponseEntity.ok().body(deviceDetailsData);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        } catch (NonUniqueResultException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteDevice(@PathVariable String id) {
        log.info("deleting device {}", id);
        try {
            deviceService.deleteDevice(id);
        } catch (EntityNotFoundException exception) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, exception.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach((error) -> errors.put(((FieldError) error).getField(), error.getDefaultMessage()));
        return errors;
    }

}
