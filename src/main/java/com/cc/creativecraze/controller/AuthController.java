package com.cc.creativecraze.controller;

import com.cc.creativecraze.dto.PortfolioDto;
import com.cc.creativecraze.dto.UserDto;
import com.cc.creativecraze.model.Portfolio;
import com.cc.creativecraze.model.User;
import com.cc.creativecraze.service.EmailService;
import com.cc.creativecraze.service.PortfolioService;
import com.cc.creativecraze.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private final PortfolioService portfolioService;
    private final EmailService emailService;

    public AuthController(UserService userService, PortfolioService portfolioService,  EmailService emailService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.emailService = emailService;
    }

    @GetMapping("/index")
    public String home(){
        return "index";
    }
    @GetMapping("/login")
    public String showLoginForm(@RequestParam(name = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Invalid email or password. Please try again.");
        }
        return "login";
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model){
        UserDto user = new UserDto();
        model.addAttribute("user",user);
        return "signup";
    }

    @GetMapping("/admin")
    public String getDashboard(Model model, @Param("keyword") String keyword){
        List<PortfolioDto> registeredPortfolios = portfolioService.getRegisteredPortfolios();
        model.addAttribute("registeredPortfolios", registeredPortfolios);
        model.addAttribute("keyword", keyword);

        return "dashboard";
    }

    //    @PreAuthorize("hasAuthority('MODEL')")
    @GetMapping("/artist")
    public String getArtistDash(String keyword, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String email = user.getUsername();
        User user1 = userService.findUserByEmail(email);
        model.addAttribute("loggedInUserName", user1.getName());
        model.addAttribute("email", user1.getEmail());
        List<PortfolioDto> registeredPortfolios = portfolioService.getRegisteredPortfolios();
        model.addAttribute("registeredPortfolios", registeredPortfolios);
        List<Portfolio> portfolio = portfolioService.getPortfolioByEmail(email);
        if (!portfolio.isEmpty()) {
            model.addAttribute("portfolio", portfolio);
        }
        if (keyword != null) {
            List<PortfolioDto> searchResults = portfolioService.getAllPortfolios(keyword);
            model.addAttribute("searchResults", searchResults);
        }
        return "artist_dashboard";
    }


    @PostMapping("/register")
    public String registration(@ModelAttribute("user") UserDto userDto, BindingResult result, Model model){
        User existingUser = userService.findUserByEmail(userDto.getEmail());
        if(existingUser != null && existingUser.getEmail() != null &&
                !existingUser.getEmail().isEmpty()
        ){
            result.reject("The email already exists");
        }
        if(result.hasErrors()){
            model.addAttribute("user",userDto);
            return"/signup";
        }
        userService.saveUser(userDto);
        sendWelcomeEmail(userDto.getName(), userDto.getEmail());
        return "redirect:/login";
    }
    @GetMapping("/users")
    public String users (Model model){
        List<UserDto> users = userService.findAllUsers();
        model.addAttribute("users",users);
        return "portfolios";
    }
    private void sendWelcomeEmail(String userName, String userEmail) {
        String subject = "Welcome to Creative Craze";
        try {
            // Read the HTML content from the template file
            String htmlContent = emailService.readHtmlTemplate("welcome_email.html");
            // Replace placeholders with actual values
            htmlContent = htmlContent.replace("[user_name]", userName);
            //send the email
            emailService.sendEmail(userEmail, subject, htmlContent);
        } catch (Exception e) {
            // Handle exceptions (e.g., mail sending failure)
            e.printStackTrace();
        }
    }
}

