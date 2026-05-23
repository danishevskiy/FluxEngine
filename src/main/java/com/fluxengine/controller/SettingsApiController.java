package com.fluxengine.controller;

import com.fluxengine.model.SystemSettings;
import com.fluxengine.service.SettingsService;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public SystemSettings save(@RequestBody SystemSettings settings) {
        return settingsService.save(settings);
    }
}
