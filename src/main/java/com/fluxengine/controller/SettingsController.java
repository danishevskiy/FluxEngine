package com.fluxengine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {

    private final SystemSettingsRepository repository;

    public SettingsController(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String settings(Model model) {
        SystemSettings settings = repository.findById(1L)
                .orElseGet(() -> repository.save(new SystemSettings()));

        model.addAttribute("settings", settings);
        return "settings";
    }

    @PostMapping
    public String save(@ModelAttribute SystemSettings settings) {
        settings.setId(1L);
        repository.save(settings);
        return "redirect:/admin/settings";
    }
}