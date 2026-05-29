package com.fluxengine.service;

import com.fluxengine.model.SystemSettings;
import com.fluxengine.repository.SystemSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private static final long SINGLE_SETTINGS_ID = 1L;
    private static final int MIN_INTERVAL_SEC = 2;
    private static final int MAX_INTERVAL_SEC = 86_400;

    private final SystemSettingsRepository repository;

    public SettingsService(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    public SystemSettings current() {
        SystemSettings settings = repository.findById(SINGLE_SETTINGS_ID)
                .orElseGet(() -> repository.save(new SystemSettings()));
        return normalize(settings);
    }

    public SystemSettings save(SystemSettings settings) {
        settings.setId(SINGLE_SETTINGS_ID);
        return repository.save(normalize(settings));
    }

    private SystemSettings normalize(SystemSettings settings) {
        if (settings.getMeasurementEnabled() == null) {
            settings.setMeasurementEnabled(true);
        }

        if (settings.getIntervalSec() == null) {
            settings.setIntervalSec(30);
        }

        if (settings.getIntervalSec() < MIN_INTERVAL_SEC) {
            settings.setIntervalSec(MIN_INTERVAL_SEC);
        }

        if (settings.getIntervalSec() > MAX_INTERVAL_SEC) {
            settings.setIntervalSec(MAX_INTERVAL_SEC);
        }

        if (settings.getDeviceId() == null || settings.getDeviceId().isBlank()) {
            settings.setDeviceId("esp32-01");
        }

        if (settings.getThicknessM() == null || settings.getThicknessM() <= 0) {
            settings.setThicknessM(0.03);
        }

        if (settings.getKTec() == null) {
            settings.setKTec(4.62);
        }

        if (settings.getKFlux() == null) {
            settings.setKFlux(13.2);
        }

        if (settings.getLambdaSource() == null || settings.getLambdaSource().isBlank()) {
            settings.setLambdaSource("FLUX");
        }

        return settings;
    }
}
