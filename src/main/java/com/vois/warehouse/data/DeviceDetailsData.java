package com.vois.warehouse.data;

import lombok.*;
import lombok.experimental.FieldNameConstants;

@ToString
@FieldNameConstants
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DeviceDetailsData {
    private String id;
    private String name;
    private String pin;
    private double temperature;
    private String status;
}
