package com.cc.creativecraze.service;

import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.repository.PortfolioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PortfolioServiceImpl implements PortfolioService {

    private final PortfolioRepository portfolioRepository;

    public PortfolioServiceImpl(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    @Override
    public List<PortfolioDto> getAllPortfolios(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return portfolioRepository.search("%" + keyword + "%");
        } else {
            List<Portfolio> portfolios = portfolioRepository.findAll();
            return portfolios.stream()
                    .map(this::mapToPortfolioDto)
                    .collect(Collectors.toList());
        }
    }
    @Override
    public List<PortfolioDto> getRegisteredPortfolios() {
        // Implement logic to fetch registered portfolios
        List<Portfolio> registeredPortfolios = portfolioRepository.findAll();
        return registeredPortfolios.stream()
                .map(this::mapToPortfolioDto)
                .collect(Collectors.toList());
    }
    private PortfolioDto mapToPortfolioDto(Portfolio portfolio){
        PortfolioDto portfolioDto = new PortfolioDto();
        portfolioDto.setId(portfolio.getId());
        portfolioDto.setName(portfolio.getName());
        portfolioDto.setOwnerEmail(portfolio.getOwnerEmail());
        portfolioDto.setAge(portfolio.getAge());
        portfolioDto.setNationality(portfolio.getNationality());
        portfolioDto.setPdfFilename(portfolio.getPdfFilename());
        portfolioDto.setPdfContentType(portfolio.getPdfContentType());
        portfolioDto.setPdf(portfolio.getPdf());
        portfolioDto.setImageFilename(portfolio.getImageFilename());
        portfolioDto.setImageContentType(portfolio.getImageContentType());
        portfolioDto.setImage(portfolio.getImage());
        portfolioDto.setMessage(portfolio.getMessage());
        return portfolioDto;
    }

    @Override
    public Optional<Portfolio> getPortfolioById(int id) {
        return portfolioRepository.findById(id);
    }

    @Override
    public String savePortfolio(PortfolioDto portfolioDto, MultipartFile pdfFile, MultipartFile imageFile) {
        try {
            Portfolio portfolio = mapToPortfolio(portfolioDto, pdfFile, imageFile);
            portfolioRepository.save(portfolio);
            if (pdfFile!=null){
                return "pdf uploaded successfully"+ pdfFile.getOriginalFilename();
            }
            if (imageFile!=null){
                return "image uploaded successfully"+imageFile.getOriginalFilename();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
        return null;
    }

    private Portfolio mapToPortfolio(PortfolioDto portfolioDto, MultipartFile pdfFile, MultipartFile imageFile) throws IOException {
        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioDto.getId());
        portfolio.setName(portfolioDto.getName());
        portfolio.setOwnerEmail(portfolioDto.getOwnerEmail());
        portfolio.setAge(portfolioDto.getAge());
        portfolio.setNationality(portfolioDto.getNationality());

        if (pdfFile != null && !pdfFile.isEmpty()) {
            portfolio.setPdfFilename(pdfFile.getOriginalFilename());
            portfolio.setPdfContentType(pdfFile.getContentType());
            portfolio.setPdf(Files.compressData(pdfFile.getBytes()));
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            portfolio.setImageFilename(imageFile.getOriginalFilename());
            portfolio.setImageContentType(imageFile.getContentType());
            portfolio.setImage(Files.compressData(imageFile.getBytes()));
        }

        portfolio.setMessage(portfolioDto.getMessage());
        return portfolio;
    }
    @Override
    @Transactional
    public byte[] downloadPicture(int id) {
        byte[] picData = null;

        try {
            Optional<Portfolio> imageData = portfolioRepository.findPicById(id);
            if (imageData.isPresent()) {
                picData = Files.decompressData(imageData.get().getImage());
            }
        } catch (Exception e) {
            // Handle the exception or log it
            e.printStackTrace();
        }
        return picData;
    }
    @Override
    @Transactional
    public byte[] downloadPdf(int id) {
        byte[] pdfData = null;

        try {
            Optional<Portfolio> pdf = portfolioRepository.findPdfById(id);
            if (pdf.isPresent()) {
                pdfData = Files.decompressData(pdf.get().getPdf());
            }
        } catch (Exception e) {
            // Handle the exception or log it
            e.printStackTrace();
        }
        return pdfData;
    }


    @Override
    public void deletePortfolioById(int id) {
        Optional<Portfolio> portfolioOptional = portfolioRepository.findById(id);

        if (portfolioOptional.isPresent()) {
            Portfolio portfolio = portfolioOptional.get();
            portfolioRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Portfolio not found");
        }
    }

    @Override
    public void updatePortfolio(PortfolioDto portfolioDto, MultipartFile pdfFile, MultipartFile imageFile) {
        Optional<Portfolio> existingPortfolioOptional = portfolioRepository.findById(portfolioDto.getId());

        if (existingPortfolioOptional.isPresent()) {
            try {
                Portfolio existingPortfolio = existingPortfolioOptional.get();
                Portfolio updatedPortfolio = mapToPortfolio(portfolioDto, pdfFile, imageFile);
                updatedPortfolio.setId(existingPortfolio.getId());
                portfolioRepository.save(updatedPortfolio);
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        } else {
            throw new EntityNotFoundException("Portfolio not found");
        }
    }


    @Override
    public List<Portfolio> getPortfolioByEmail(String email) {
        return portfolioRepository.findPortfolioByOwnerEmail(email);
    }

}
