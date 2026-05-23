package com.fluxengine.service;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SystemSettings {

    @Id
    private Long id = 1L;

    private Double kTec = 4.62;
    private Double kFlux = 13.2;
    private Double thicknessM = 0.03;

    private String materialName = "VIP panel";
    private String lambdaSource = "FLUX"; // TEC або FLUX

    public Double getkTec() {
        return kTec;
    }

    public void setkTec(Double kTec) {
        this.kTec = kTec;
    }

    public Double getkFlux() {
        return kFlux;
    }

    public void setkFlux(Double kFlux) {
        this.kFlux = kFlux;
    }

    public Double getThicknessM() {
        return thicknessM;
    }

    public void setThicknessM(Double thicknessM) {
        this.thicknessM = thicknessM;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}
