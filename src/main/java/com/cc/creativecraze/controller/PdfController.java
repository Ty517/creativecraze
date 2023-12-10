package com.cc.creativecraze.controller;

import com.cc.creativecraze.service.Files;
import com.cc.creativecraze.service.PortfolioService;
import com.itextpdf.text.DocumentException;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.repository.PortfolioRepository;
import com.cc.creativecraze.service.PdfService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
public class PdfController {
    private final PortfolioRepository portfolioRepository;
    private final PdfService pdfService;
    private final PortfolioService portfolioService;


    public PdfController(PortfolioRepository portfolioRepository, PdfService pdfService, PortfolioService portfolioService) {
        this.portfolioRepository = portfolioRepository;
        this.pdfService = pdfService;
        this.portfolioService =portfolioService;
    }

    @InitBinder
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws ServletException {

        // Convert multipart object to byte[]
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());

    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf() throws DocumentException {
        List<Portfolio> portfolios = portfolioRepository.findAll();
        try {
            byte[] pdfBytes = pdfService.generatePdfWithTable(portfolios);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ArtistProfiles.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (DocumentException e) {
            e.printStackTrace();
            // Handle the exception appropriately and return an error response
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF".getBytes());
        }
    }


    @GetMapping("/download-artistPdf/{email}")
    public ResponseEntity<byte[]> downloadArtistPdf(@PathVariable("email") String email) throws DocumentException {
        List<Portfolio> portfolios = portfolioRepository.findPortfolioByOwnerEmail(email);
        if (portfolios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] pdfBytes = pdfService.generatePdfWithTable(portfolios);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "YourProfile.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (DocumentException e) {
            e.printStackTrace();
            // Handle the exception appropriately and return an error response
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF".getBytes());
        }
    }
    @GetMapping("/download-picture/{id}")
    public ResponseEntity<?> downloadPicture(@PathVariable int id) {
        byte[] ImageData = portfolioService.downloadPicture(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(ImageData);
    }
    @GetMapping("/get-portfolio/{id}")
    public ResponseEntity<?> downloadPdf(@PathVariable int id) {
        byte[] pdfData = portfolioService.downloadPdf(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.valueOf("application/pdf"))
                .body(pdfData);
    }

}

