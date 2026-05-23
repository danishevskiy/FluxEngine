package com.fluxengine.service;

import com.fluxengine.model.SystemSettings;
import com.fluxengine.repository.SystemSettingsRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
    private final SystemSettingsRepository repository;

    public SettingsService(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    public SystemSettings current() {
        return repository.findById(1L).orElseGet(() -> repository.save(new SystemSettings()));
    }

    public SystemSettings save(SystemSettings settings) {
        settings.setId(1L);
        return repository.save(settings);
    }
}
