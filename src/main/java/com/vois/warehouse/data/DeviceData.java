package com.vois.warehouse.data;

import lombok.*;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@ToString
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DeviceData {
    @NotBlank(message = "Device name should not be empty")
    private String name;
    @Size(min = 7, max = 7, message = "Device PIN should be 7 digit")
    private String pin;
}
