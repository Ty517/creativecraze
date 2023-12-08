package com.cc.creativecraze.controller;

import com.itextpdf.text.DocumentException;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.repository.PortfolioRepository;
import com.cc.creativecraze.service.PdfService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PdfController {
    private final PortfolioRepository portfolioRepository;
    private final PdfService pdfService;


    public PdfController(PortfolioRepository portfolioRepository, PdfService pdfService) {
        this.portfolioRepository = portfolioRepository;
        this.pdfService = pdfService;
    }


    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() throws DocumentException {
        List<Portfolio> portfolios = portfolioRepository.findAll();

        StringBuilder dataBuilder = new StringBuilder();
        for (Portfolio portfolio : portfolios) {
            dataBuilder.append("ID: ").append(portfolio.getId()).append("\n")
                    .append("Owner Email: ").append(portfolio.getOwnerEmail()).append("\n")
                    .append("Age: ").append(portfolio.getAge()).append("\n")
                    .append("Nationality: ").append(portfolio.getNationality()).append("\n")
                    .append("Message: ").append(portfolio.getMessage()).append("\n")
                    .append("Name: ").append(portfolio.getName()).append("\n\n");
        }

        String data = dataBuilder.toString();

        byte[] pdfBytes = pdfService.generatePdf(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "portfolios.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }


    @GetMapping("/download-artistPdf/{email}")
    public ResponseEntity<byte[]> downloadArtistPdf(@PathVariable("email") String email) throws DocumentException {
        List<Portfolio> portfolios = portfolioRepository.findPortfolioByOwnerEmail(email);
        if (portfolios.isEmpty()) {
            // Handle the case when the portfolio does not exist
            // Return an appropriate response or error message
            return ResponseEntity.notFound().build();
        }


        StringBuilder dataBuilder = new StringBuilder();
        for (Portfolio portfolio : portfolios) {
            dataBuilder.append("ID: ").append(portfolio.getId()).append("\n")
                    .append("Owner Email: ").append(portfolio.getOwnerEmail()).append("\n")
                    .append("Age: ").append(portfolio.getAge()).append("\n")
                    .append("Nationality: ").append(portfolio.getNationality()).append("\n")
                    .append("Message: ").append(portfolio.getMessage()).append("\n")
                    .append("Name: ").append(portfolio.getName()).append("\n\n");
        }
        String data = dataBuilder.toString();

        byte[] pdfBytes = pdfService.generatePdf(data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "ArtistPortfolio.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdfBytes);
    }
    @GetMapping("/download-portfolio-pdf/{id}")
    public ResponseEntity<byte[]> downloadPortfolioPdf(@PathVariable int id) {
        try {
            Optional<Portfolio> portfolioOptional = portfolioRepository.findById(id);

            if (portfolioOptional.isEmpty()) {
                throw new EntityNotFoundException("Portfolio not found!");
            }

            Portfolio portfolio = portfolioOptional.get();
            byte[] pdfBytes = portfolio.getPdf();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "portfolio.pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exception and return appropriate response
            return ResponseEntity.badRequest().build();
        }
    }


}

