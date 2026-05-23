package com.fluxengine.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record MeasurementRequest(
        Double t1,
        Double t2,
        Double t3,
        Double t4,
        Double deltaT,

        // New compact protocol fields from ESP32
        Double tec,
        Double flux,

        // Backward-compatible field names
        Double tecMv,
        Double qTec,
        Double fluxMv,
        Double qFlux,

        @DecimalMin("0.001") @DecimalMax("2.0") Double thicknessM,
        Double lambdaTec,
        Double lambdaFlux,
        String deviceId,
        String note
) {}
