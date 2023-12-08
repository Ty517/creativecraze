package com.cc.creativecraze.controller;


import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.model.User;
import com.cc.creativecraze.service.EmailService;
import com.cc.creativecraze.service.PortfolioService;
import com.cc.creativecraze.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final EmailService emailService;
    private final UserService userService;

    public PortfolioController(PortfolioService portfolioService, EmailService emailService, UserService userService) {
        this.portfolioService = portfolioService;
        this.emailService = emailService;
        this.userService = userService;
    }

    @GetMapping("/add_portfolio")
    public  String getPortfolioForm(Model model){
        PortfolioDto portfolioDto = new PortfolioDto();
        model.addAttribute("portfolio", portfolioDto);
        return "add_portfolio";
    }


    @PostMapping("/add_portfolio")
    public String addPortfolio(@ModelAttribute("portfolio")PortfolioDto portfolioDto){
        portfolioService.savePortfolio(portfolioDto);
        return "redirect:/admin";
    }

    @GetMapping("/delete_profile/{id}")
    public String deletePortfolio(@PathVariable("id")int id ){
        Portfolio deletedPortfolio = portfolioService.getPortfolioById(id)
                .orElseThrow(() -> new EntityNotFoundException("Portfolio not found"));

        // Delete the portfolio
        portfolioService.deletePortfolioById(id);
        sendRejectionEmail(deletedPortfolio.getName(), deletedPortfolio.getOwnerEmail());

        return "redirect:/admin";
    }

    @GetMapping("/update/{id}")
    public  String updatePortfolio(@PathVariable("id")int id, Model model){
        System.out.println("ID from URL: " + id);
        model.addAttribute("portfolio", portfolioService.getPortfolioById(id).orElse(null));
        return "update_portfolio";

    }
    @PostMapping("/update")
    public String updatePortfolioForm(@ModelAttribute PortfolioDto portfolioDto){
        portfolioService.updatePortfolio(portfolioDto);
        return "redirect:/admin";
    }
    @GetMapping("/update-artist/{id}")
    public  String updateArtist(@PathVariable("id")int id, Model model){
        System.out.println("ID from URL: " + id);
        model.addAttribute("portfolio", portfolioService.getPortfolioById(id).orElse(null));
        return "update_artist";

    }
    @PostMapping("/update_artist")
    public String updateArtistForm(@ModelAttribute PortfolioDto portfolioDto){
        portfolioService.updatePortfolio(portfolioDto);
        System.out.println("Update Artist Form Called");
        return "redirect:/artist";
    }
    @GetMapping("/add_artist")
    public  String getArtistForm(Model model){
        PortfolioDto portfolioDto = new PortfolioDto();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String email = user.getUsername();
        User user1 = userService.findUserByEmail(email);
        model.addAttribute("loggedInUserName", user1.getName());
        model.addAttribute("email", user1.getEmail());
        model.addAttribute("portfolio", portfolioDto);
        return "add_artist";
    }
    @PostMapping("/add_artist")
    public String addArtist(@ModelAttribute("portfolio") PortfolioDto portfolioDto) {
        portfolioService.savePortfolio(portfolioDto);

        // Update user authorities
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(userDetails.getAuthorities());
        updatedAuthorities.add(new SimpleGrantedAuthority("ROLE_ARTIST"));

        // Create a new authentication object with updated authorities
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                userDetails, userDetails.getPassword(), updatedAuthorities);

        // Update the authentication context
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return "redirect:/artist";
    }

    @GetMapping("/delete_artist/{id}")
    public String deleteArtistPortfolio(@PathVariable("id")int id ){
        portfolioService.deletePortfolioById(id);
        return  "redirect:/artist";
    }
    @GetMapping("/search")
    public String searchPortfolios(
            @RequestParam(required = false) String keyword,
            Model model
    ) {
        // Pre-load registered portfolios
        List<PortfolioDto> registeredPortfolios = portfolioService.getRegisteredPortfolios();
        model.addAttribute("registeredPortfolios", registeredPortfolios);

        // Perform search if keyword is provided
        if (keyword != null) {
            List<PortfolioDto> searchResults = portfolioService.getAllPortfolios(keyword);
            model.addAttribute("searchResults", searchResults);
        }
        return "dashboard";
    }

    private void sendRejectionEmail(String userName, String userEmail) {
        String subject = "Portfolio Deleted";
        try {
            // Read the HTML content from the portfolio_deleted_email.html template
            String htmlContent = emailService.readHtmlTemplate("rejection_email.html");
            // Replace placeholders with actual values
            htmlContent = htmlContent.replace("[user_name]", userName);
            // Send the email
            emailService.sendEmail(userEmail, subject, htmlContent);
        } catch (Exception e) {
            // Handle exceptions (e.g., mail sending failure)
            e.printStackTrace();
        }
    }
}


