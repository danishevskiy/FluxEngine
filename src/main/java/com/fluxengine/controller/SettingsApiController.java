package com.fluxengine.controller;

import com.fluxengine.model.SystemSettings;
import com.fluxengine.service.SettingsService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
public class SettingsApiController {
    private final SettingsService settingsService;

    public SettingsApiController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public SystemSettings current() {
        return settingsService.current();
    }

    /**
     * Compact endpoint for ESP32. It intentionally returns only runtime-control fields.
     */
    @GetMapping("/runtime")
    public Map<String, Object> runtime(@RequestParam(required = false) String deviceId) {
        SystemSettings s = settingsService.current();

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("deviceId", deviceId != null && !deviceId.isBlank() ? deviceId : s.getDeviceId());
        response.put("measurementEnabled", Boolean.TRUE.equals(s.getMeasurementEnabled()));
        response.put("intervalSec", s.getIntervalSec());
        response.put("updatedAt", s.getUpdatedAt());
        return response;
    }

    @PostMapping
    public SystemSettings save(@RequestBody SystemSettings settings) {
        return settingsService.save(settings);
    }
}
