package com.cc.creativecraze.repository;

import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//import static org.hibernate.FetchMode.SELECT;

@Repository
public interface PortfolioRepository extends JpaRepository <Portfolio, Integer> {
    List<Portfolio> findPortfolioByOwnerEmail(String email);

    @Query("SELECT new com.cc.creativecraze.dto.PortfolioDto(p.id, p.name, p.ownerEmail, p.age, p.nationality, p.pdfFilename, p.pdfContentType, p.pdf, p.imageFilename, p.imageContentType, p.image, p.message) FROM Portfolio p WHERE CONCAT(p.id, p.ownerEmail, p.age, p.nationality, p.message, p.name) LIKE %:keyword%")
    List<PortfolioDto> search(@Param("keyword") String keyword);
    Optional<Portfolio> findPdfById(int id);

    Optional<Portfolio> findPdfByOwnerEmail(String email);
    Optional<Portfolio> findPicById(int id);

}
