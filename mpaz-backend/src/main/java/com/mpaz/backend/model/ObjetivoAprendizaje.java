package com.mpaz.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "objetivos_aprendizaje")
public class ObjetivoAprendizaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false)
    private String oaCode;

    @Column(nullable = false)
    private String oaTitle;

    private String unit;
    private String description;
    private String status;

    public ObjetivoAprendizaje() {}

    public ObjetivoAprendizaje(String subject, String oaCode, String oaTitle, String unit, String description, String status) {
        this.subject = subject;
        this.oaCode = oaCode;
        this.oaTitle = oaTitle;
        this.unit = unit;
        this.description = description;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getOaCode() { return oaCode; }
    public void setOaCode(String oaCode) { this.oaCode = oaCode; }
    public String getOaTitle() { return oaTitle; }
    public void setOaTitle(String oaTitle) { this.oaTitle = oaTitle; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}