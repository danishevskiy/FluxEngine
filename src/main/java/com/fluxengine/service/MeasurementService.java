package com.fluxengine.service;

import com.fluxengine.dto.MeasurementRequest;
import com.fluxengine.model.Measurement;
import com.fluxengine.repository.MeasurementRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MeasurementService {
    private final MeasurementRepository repository;

    public MeasurementService(MeasurementRepository repository) {
        this.repository = repository;
    }

    public Measurement save(MeasurementRequest r) {
        Measurement m = new Measurement();
        m.setT1(r.t1());
        m.setT2(r.t2());
        m.setT3(r.t3());
        m.setT4(r.t4());
        m.setTecMv(r.tecMv());
        m.setQTec(r.qTec());
        m.setFluxMv(r.fluxMv());
        m.setQFlux(r.qFlux());
        m.setThicknessM(r.thicknessM() != null ? r.thicknessM() : 0.03);
        m.setDeviceId(r.deviceId() != null ? r.deviceId() : "ESP32-01");
        m.setNote(r.note());

        double tWarm = firstNonNull(r.t2(), r.t3(), 0.0);
        double tCold = firstNonNull(r.t1(), r.t4(), 0.0);
        double deltaT = r.deltaT() != null ? r.deltaT() : Math.abs(tWarm - tCold);
        m.setDeltaT(deltaT);

        double thickness = m.getThicknessM();
        if (r.lambdaTec() != null) {
            m.setLambdaTec(r.lambdaTec());
        } else if (r.qTec() != null && deltaT > 0.1) {
            m.setLambdaTec(Math.abs(r.qTec()) * thickness / deltaT);
        }
        if (r.lambdaFlux() != null) {
            m.setLambdaFlux(r.lambdaFlux());
        } else if (r.qFlux() != null && deltaT > 0.1) {
            m.setLambdaFlux(Math.abs(r.qFlux()) * thickness / deltaT);
        }
        return repository.save(m);
    }

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
}
