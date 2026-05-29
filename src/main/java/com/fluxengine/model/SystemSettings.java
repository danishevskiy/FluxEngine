package com.fluxengine.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_settings")
public class SystemSettings {
    @Id
    private Long id = 1L;

    private Double kTec = 4.62;
    private Double kFlux = 13.2;
    private Double thicknessM = 0.03;
    private String materialName = "VIP panel";
    private String deviceId = "esp32-01";
    private String lambdaSource = "FLUX";
    private String t1Name = "T1";
    private String t2Name = "T2";
    private String tecName = "TEC1";
    private String fluxName = "Heat flux sensor";

    /**
     * Remote control for ESP32 measurement cycle.
     * true  - ESP32 measures and sends data;
     * false - ESP32 only checks settings and waits.
     */
    private Boolean measurementEnabled = true;

    /**
     * Measurement interval requested by admin panel, seconds.
     */
    private Integer intervalSec = 30;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void touch() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getKTec() { return kTec; }
    public void setKTec(Double kTec) { this.kTec = kTec; }

    public Double getKFlux() { return kFlux; }
    public void setKFlux(Double kFlux) { this.kFlux = kFlux; }

    public Double getThicknessM() { return thicknessM; }
    public void setThicknessM(Double thicknessM) { this.thicknessM = thicknessM; }

    public String getMaterialName() { return materialName; }
    public void setMaterialName(String materialName) { this.materialName = materialName; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getLambdaSource() { return lambdaSource; }
    public void setLambdaSource(String lambdaSource) { this.lambdaSource = lambdaSource; }

    public String getT1Name() { return t1Name; }
    public void setT1Name(String t1Name) { this.t1Name = t1Name; }

    public String getT2Name() { return t2Name; }
    public void setT2Name(String t2Name) { this.t2Name = t2Name; }

    public String getTecName() { return tecName; }
    public void setTecName(String tecName) { this.tecName = tecName; }

    public String getFluxName() { return fluxName; }
    public void setFluxName(String fluxName) { this.fluxName = fluxName; }

    public Boolean getMeasurementEnabled() { return measurementEnabled; }
    public void setMeasurementEnabled(Boolean measurementEnabled) { this.measurementEnabled = measurementEnabled; }

    public Integer getIntervalSec() { return intervalSec; }
    public void setIntervalSec(Integer intervalSec) { this.intervalSec = intervalSec; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
