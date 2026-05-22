package com.fluxengine.controller;

import com.fluxengine.dto.MeasurementRequest;
import com.fluxengine.dto.MeasurementResponse;
import com.fluxengine.model.Measurement;
import com.fluxengine.service.MeasurementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementApiController {
    private final MeasurementService service;

    public MeasurementApiController(MeasurementService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<MeasurementResponse> create(@Valid @RequestBody MeasurementRequest request) {
        Measurement saved = service.save(request);
        return ResponseEntity.created(URI.create("/api/measurements/" + saved.getId()))
                .body(MeasurementResponse.from(saved));
    }

    @GetMapping("/latest")
    public List<MeasurementResponse> latest(@RequestParam(defaultValue = "100") int limit) {
        return service.latest(limit).stream().map(MeasurementResponse::from).toList();
    }

    @GetMapping("/search")
    public Page<MeasurementResponse> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) Double minLambda,
            @RequestParam(required = false) Double maxLambda,
            @RequestParam(required = false) Double minQ,
            @RequestParam(required = false) Double maxQ,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return service.search(from, to, deviceId, minLambda, maxLambda, minQ, maxQ, page, size)
                .map(MeasurementResponse::from);
    }
}
