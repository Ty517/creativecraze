package com.cc.creativecraze.model;


import jakarta.persistence.*;
import lombok.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Portfolio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String ownerEmail;
    private String age;
    private String nationality;
    private String pdfFilename;
    private String pdfContentType;
    @Column(name = "pdf",length = 10485760, columnDefinition = "bytea")
    private byte[] pdf;
    private String imageFilename;
    private String imageContentType;
    @Column(name = "image",length = 10485760, columnDefinition = "bytea")
    private byte[] image;
    private String message;


}
