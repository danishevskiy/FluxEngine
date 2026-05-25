package com.fluxengine.dto;

import java.util.List;

public record UniversalMeasurementRequest(
        String deviceId,
        List<TemperatureItem> temperatures,
        List<FluxItem> flux
) {
    public record TemperatureItem(
            String address,
            Double value
    ) {}

    public record FluxItem(
            String channel,
            Double mv
    ) {}
}