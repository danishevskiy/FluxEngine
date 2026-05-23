package com.fluxengine.controller;

import com.fluxengine.model.SystemSettings;
import com.fluxengine.service.SettingsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/settings")
public class SettingsController {
    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public String settings(Model model) {
        model.addAttribute("settings", settingsService.current());
        return "settings";
    }

    @PostMapping
    public String save(@ModelAttribute SystemSettings settings) {
        settingsService.save(settings);
        return "redirect:/admin/settings?saved=true";
    }
}
