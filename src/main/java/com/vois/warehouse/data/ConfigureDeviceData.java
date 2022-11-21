package com.vois.warehouse.data;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ToString
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ConfigureDeviceData {
    @Min(value = 0, message = "Device temperature should be between 0\'C and 10\'C")
    @Max(value = 10, message = "Device temperature should be between 0\'C and 10\'C")
    private double temperature;
}
