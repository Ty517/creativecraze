package com.cc.creativecraze.dto;

import jakarta.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class PortfolioDto {
    private int id;
    private String name;
    private String ownerEmail;
    private String age;
    private String nationality;
    private byte[] pdf;
    private byte[] image;
    private String message;

    public PortfolioDto() {
    }

    public PortfolioDto(int id, String name, String ownerEmail, String age, String nationality, byte[] pdf, byte[] image, String message) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.age = age;
        this.nationality = nationality;
        this.pdf = pdf;
        this.image = image;
        this.message = message;
    }
}
