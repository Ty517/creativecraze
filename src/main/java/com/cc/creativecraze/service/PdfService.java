package com.cc.creativecraze.service;

import com.cc.creativecraze.model.Portfolio;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfService {

    public byte[] generatePdfWithTable(List<Portfolio> portfolios) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        Font headingFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph heading = new Paragraph("Creative Craze Profiles", headingFont);
        heading.setAlignment(Element.ALIGN_CENTER);
        document.add(heading);

        // Add an empty line for spacing
        document.add(new Paragraph(" "));

        // Add table headers
        PdfPTable table = new PdfPTable(5); // Adjust the number of columns as per your requirement
        addTableHeader(table);

        // Add data rows to the table
        for (Portfolio portfolio : portfolios) {
            addRows(table, portfolio);
        }

        document.add(table);
        document.close();

        return baos.toByteArray();
    }

    private void addTableHeader(PdfPTable table) {
        // Add table headers
        table.addCell("ID");
        table.addCell("Owner Email");
        table.addCell("Age");
        table.addCell("Nationality");
        table.addCell("Description");
        // Add more headers as needed
    }

    private void addRows(PdfPTable table, Portfolio portfolio) {
        // Add data rows
        table.addCell(String.valueOf(portfolio.getId()));
        table.addCell(portfolio.getOwnerEmail());
        table.addCell(portfolio.getAge());
        table.addCell(portfolio.getNationality());
        table.addCell(portfolio.getMessage());
        // Add more cell data as needed
    }
}