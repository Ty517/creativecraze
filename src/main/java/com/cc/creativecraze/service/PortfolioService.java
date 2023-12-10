package com.cc.creativecraze.service;

import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.model.Portfolio;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface PortfolioService {
    List<PortfolioDto> getAllPortfolios(String keyword);
    List<PortfolioDto> getRegisteredPortfolios();
    Optional<Portfolio> getPortfolioById(int id);
    String savePortfolio(PortfolioDto portfolioDto, MultipartFile pdfFile, MultipartFile imageFile);
    void deletePortfolioById(int id);
    void updatePortfolio( PortfolioDto portfolioDto, MultipartFile pdfFile, MultipartFile imageFile);

    byte[] downloadPicture(int id);
    byte[] downloadPdf(int id);

    List<Portfolio> getPortfolioByEmail(String email);


}
