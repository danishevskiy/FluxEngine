package com.fluxengine.service;

import com.fluxengine.dto.MeasurementRequest;
import com.fluxengine.model.Measurement;
import com.fluxengine.model.SystemSettings;
import com.fluxengine.repository.MeasurementRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.fluxengine.dto.UniversalMeasurementRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeasurementService {
    private final MeasurementRepository repository;
    private final SettingsService settingsService;

    public MeasurementService(MeasurementRepository repository, SettingsService settingsService) {
        this.repository = repository;
        this.settingsService = settingsService;
    }

    public Measurement save(MeasurementRequest r) {
        SystemSettings s = settingsService.current();
        Measurement m = new Measurement();
        m.setT1(r.t1());
        m.setT2(r.t2());
        m.setT3(r.t3());
        m.setT4(r.t4());

        Double tecMv = firstNonNullObj(r.tec(), r.tecMv());
        Double fluxMv = firstNonNullObj(r.flux(), r.fluxMv());
        m.setTecMv(tecMv);
        m.setFluxMv(fluxMv);

        double kTec = nonNull(s.getKTec(), 4.62);
        double kFlux = nonNull(s.getKFlux(), 13.2);
        double thickness = r.thicknessM() != null ? r.thicknessM() : nonNull(s.getThicknessM(), 0.03);
        m.setThicknessM(thickness);
        m.setDeviceId(r.deviceId() != null && !r.deviceId().isBlank() ? r.deviceId() : s.getDeviceId());
        m.setNote(r.note());

        Double qTec = r.qTec() != null ? r.qTec() : (tecMv != null ? Math.abs(tecMv * kTec) : null);
        Double qFlux = r.qFlux() != null ? r.qFlux() : (fluxMv != null ? Math.abs(fluxMv * kFlux) : null);
        m.setQTec(qTec);
        m.setQFlux(qFlux);

        double tWarm = firstNonNull(r.t2(), r.t3(), 0.0);
        double tCold = firstNonNull(r.t1(), r.t4(), 0.0);
        double deltaT = r.deltaT() != null ? r.deltaT() : Math.abs(tWarm - tCold);
        m.setDeltaT(deltaT);

        if (r.lambdaTec() != null) {
            m.setLambdaTec(r.lambdaTec());
        } else if (qTec != null && deltaT > 0.1) {
            m.setLambdaTec(Math.abs(qTec) * thickness / deltaT);
        }
        if (r.lambdaFlux() != null) {
            m.setLambdaFlux(r.lambdaFlux());
        } else if (qFlux != null && deltaT > 0.1) {
            m.setLambdaFlux(Math.abs(qFlux) * thickness / deltaT);
        }
        return repository.save(m);
    }

    public Measurement saveUniversal(UniversalMeasurementRequest request) {
        double t1 = request.temperatures() != null && request.temperatures().size() > 0
                ? request.temperatures().get(0).value()
                : 0.0;

        double t2 = request.temperatures() != null && request.temperatures().size() > 1
                ? request.temperatures().get(1).value()
                : 0.0;

        double tec = request.flux() != null && request.flux().size() > 0
                ? request.flux().get(0).mv()
                : 0.0;

        double flux = request.flux() != null && request.flux().size() > 1
                ? request.flux().get(1).mv()
                : 0.0;

        MeasurementRequest mapped = new MeasurementRequest(
                t1,
                t2,
                null,
                null,
                Math.abs(t2 - t1),
                tec,
                flux,
                tec,
                null,
                flux,
                null,
                null,
                null,
                null,
                request.deviceId(),
                "universal-protocol"
        );

        return save(mapped);
    }
    private double nonNull(Double v, double fallback) { return v != null ? v : fallback; }
    private Double firstNonNullObj(Double a, Double b) { return a != null ? a : b; }
    private double firstNonNull(Double a, Double b, double fallback) {
        if (a != null) return a;
        if (b != null) return b;
        return fallback;
    }

    public Page<Measurement> search(LocalDateTime from, LocalDateTime to, String deviceId,
                                    Double minLambda, Double maxLambda, Double minQ, Double maxQ,
                                    int page, int size) {
        Specification<Measurement> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (from != null) predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), from));
            if (to != null) predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), to));
            if (deviceId != null && !deviceId.isBlank()) predicates.add(cb.like(cb.lower(root.get("deviceId")), "%" + deviceId.toLowerCase() + "%"));
            if (minLambda != null) predicates.add(cb.or(
                    cb.greaterThanOrEqualTo(root.get("lambdaFlux"), minLambda),
                    cb.greaterThanOrEqualTo(root.get("lambdaTec"), minLambda)));
            if (maxLambda != null) predicates.add(cb.or(
                    cb.lessThanOrEqualTo(root.get("lambdaFlux"), maxLambda),
                    cb.lessThanOrEqualTo(root.get("lambdaTec"), maxLambda)));
            if (minQ != null) predicates.add(cb.or(
                    cb.greaterThanOrEqualTo(root.get("qFlux"), minQ),
                    cb.greaterThanOrEqualTo(root.get("qTec"), minQ)));
            if (maxQ != null) predicates.add(cb.or(
                    cb.lessThanOrEqualTo(root.get("qFlux"), maxQ),
                    cb.lessThanOrEqualTo(root.get("qTec"), maxQ)));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(spec, PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 500), Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    public List<Measurement> latest(int limit) {
        return repository.findAll(PageRequest.of(0, Math.min(Math.max(limit, 1), 500), Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
    }

    public List<Measurement> all(int limit) {
        return latest(limit);
    }
}
