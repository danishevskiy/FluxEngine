package com.fluxengine.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurements", indexes = {
        @Index(name = "idx_measurements_created_at", columnList = "createdAt")
})
public class Measurement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Double t1;
    private Double t2;
    private Double t3;
    private Double t4;
    private Double deltaT;
    private Double tecMv;
    private Double qTec;
    private Double fluxMv;
    private Double qFlux;
    private Double thicknessM;
    private Double lambdaTec;
    private Double lambdaFlux;
    private String deviceId;
    private String note;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Double getT1() { return t1; }
    public void setT1(Double t1) { this.t1 = t1; }
    public Double getT2() { return t2; }
    public void setT2(Double t2) { this.t2 = t2; }
    public Double getT3() { return t3; }
    public void setT3(Double t3) { this.t3 = t3; }
    public Double getT4() { return t4; }
    public void setT4(Double t4) { this.t4 = t4; }
    public Double getDeltaT() { return deltaT; }
    public void setDeltaT(Double deltaT) { this.deltaT = deltaT; }
    public Double getTecMv() { return tecMv; }
    public void setTecMv(Double tecMv) { this.tecMv = tecMv; }
    public Double getQTec() { return qTec; }
    public void setQTec(Double qTec) { this.qTec = qTec; }
    public Double getFluxMv() { return fluxMv; }
    public void setFluxMv(Double fluxMv) { this.fluxMv = fluxMv; }
    public Double getQFlux() { return qFlux; }
    public void setQFlux(Double qFlux) { this.qFlux = qFlux; }
    public Double getThicknessM() { return thicknessM; }
    public void setThicknessM(Double thicknessM) { this.thicknessM = thicknessM; }
    public Double getLambdaTec() { return lambdaTec; }
    public void setLambdaTec(Double lambdaTec) { this.lambdaTec = lambdaTec; }
    public Double getLambdaFlux() { return lambdaFlux; }
    public void setLambdaFlux(Double lambdaFlux) { this.lambdaFlux = lambdaFlux; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
