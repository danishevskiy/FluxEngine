package com.fluxengine.dto;

import com.fluxengine.model.Measurement;

import java.time.LocalDateTime;

public record MeasurementResponse(
        Long id, LocalDateTime createdAt,
        Double t1, Double t2, Double t3, Double t4, Double deltaT,
        Double tecMv, Double qTec, Double fluxMv, Double qFlux,
        Double thicknessM, Double lambdaTec, Double lambdaFlux,
        String deviceId, String note
) {
    public static MeasurementResponse from(Measurement m) {
        return new MeasurementResponse(m.getId(), m.getCreatedAt(), m.getT1(), m.getT2(), m.getT3(), m.getT4(),
                m.getDeltaT(), m.getTecMv(), m.getQTec(), m.getFluxMv(), m.getQFlux(), m.getThicknessM(),
                m.getLambdaTec(), m.getLambdaFlux(), m.getDeviceId(), m.getNote());
    }
}
