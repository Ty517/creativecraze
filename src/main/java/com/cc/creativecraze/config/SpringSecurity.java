package com.cc.creativecraze.config;

import com.cc.creativecraze.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import com.cc.creativecraze.config.SecurityConstants;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SpringSecurity {
    @Autowired
    private UserDetailsService userDetailsService;
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .sessionManagement()
                .sessionConcurrency(sessionConcurrency -> sessionConcurrency.maximumSessions(-1))
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/index**","/","/js/*","/css/*","/images/**","/register","/reset-password**")
                .permitAll()
                .requestMatchers("/admin*").hasAuthority(SecurityConstants.ROLE_ADMIN)
                .requestMatchers("/artist*").hasAuthority(SecurityConstants.ROLE_ARTIST)
                .anyRequest()
                .authenticated()
                .and()
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .usernameParameter("email")
                                .successHandler((request, response, authentication) -> {
                                    UserDetails user = (UserDetails) authentication.getPrincipal();
                                    boolean isAdmin = user.getAuthorities().stream()
                                            .anyMatch(auth -> auth.getAuthority().equals(SecurityConstants.ROLE_ADMIN));
                                    boolean isArtist = user.getAuthorities().stream()
                                            .anyMatch(auth -> auth.getAuthority().equals(SecurityConstants.ROLE_ARTIST));
                                    if (isAdmin) {
                                        response.sendRedirect("/admin");
                                    } else if(isArtist){
                                        System.out.println(user.getAuthorities());
                                        response.sendRedirect("/artist");
                                    }else{
                                        response.sendRedirect("/");
                                    }
                                })


                                .permitAll()
                );

        return http.build();

    }
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .inMemoryAuthentication()
                .withUser(SecurityConstants.ADMIN_EMAIL)
                .password(passwordEncoder().encode(SecurityConstants.ADMIN_PASSWORD))
                .authorities(SecurityConstants.ROLE_ADMIN);
        auth.inMemoryAuthentication().getUserDetailsService();
    }
}