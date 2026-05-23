package java.com.fluxengine.controller;

import com.fluxengine.dto.MeasurementRequest;
import com.fluxengine.service.MeasurementService;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@Profile("!prod")
public class SeedController {
    private final MeasurementService service;
    public SeedController(MeasurementService service) { this.service = service; }

    @PostMapping("/api/dev/seed")
    public String seed() {
        Random r = new Random();
        for (int i = 0; i < 50; i++) {
            double t1 = 24 + r.nextDouble();
            double t2 = 5 + r.nextDouble();
            double qFlux = 12 + r.nextDouble() * 8;
            service.save(new MeasurementRequest(t1, t2, null, null, Math.abs(t2-t1),
                    r.nextDouble(), r.nextDouble()*5, r.nextDouble(), qFlux, 0.03, null, null,
                    "ESP32-DEMO", "seed"));
        }
        return "ok";
    }
}
