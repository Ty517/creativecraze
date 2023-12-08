package com.cc.creativecraze.service;

import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.service.EmailService;
import com.cc.creativecraze.repository.PortfolioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

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
        portfolioDto.setPdf(portfolio.getPdf());
        portfolioDto.setImage(portfolio.getImage());
        portfolioDto.setMessage(portfolio.getMessage());
        return portfolioDto;
    }
    @Override
    public Optional<Portfolio> getPortfolioById(int id) {
        return portfolioRepository.findById(id);
    }

    @Override
    public void savePortfolio(PortfolioDto portfolioDto) {
        Portfolio portfolio= mapToPortfolio(portfolioDto);
        portfolioRepository.save(portfolio);
    }
    private Portfolio mapToPortfolio(PortfolioDto portfolioDto){
        Portfolio portfolio = new Portfolio();
        portfolio.setId(portfolioDto.getId());
        portfolio.setName(portfolioDto.getName());
        portfolio.setOwnerEmail(portfolioDto.getOwnerEmail());
        portfolio.setAge(portfolioDto.getAge());
        portfolio.setNationality(portfolioDto.getNationality());
        portfolio.setPdf(portfolioDto.getPdf());
        portfolio.setImage(portfolioDto.getImage());
        portfolio.setMessage(portfolioDto.getMessage());
        return portfolio;
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
    public void updatePortfolio(PortfolioDto portfolioDto) {
        Optional<Portfolio> existingPortfolioOptional = portfolioRepository.findById(portfolioDto.getId());

        if (existingPortfolioOptional.isPresent()) {
            Portfolio existingPortfolio = existingPortfolioOptional.get();
            Portfolio updatedPortfolio = mapToPortfolio(portfolioDto);
            updatedPortfolio.setId(existingPortfolio.getId());
            portfolioRepository.save(updatedPortfolio);
        } else {
            throw new EntityNotFoundException("Portfolio not found");
        }
    }


    @Override
    public List<Portfolio> getPortfolioByEmail(String email) {
        return portfolioRepository.findPortfolioByOwnerEmail(email);
    }

}
