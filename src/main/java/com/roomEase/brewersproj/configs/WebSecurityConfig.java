package com.roomEase.brewersproj.configs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl siteUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    protected SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/signup").permitAll()
                        .requestMatchers("/residences").permitAll()
                        .requestMatchers("/residences/delete/**").hasAuthority("ADMIN")

                        .requestMatchers("/adminDashboard").hasAuthority("ADMIN")
                        .requestMatchers("/myprofile", "/login", "/logout", "/aboutUs").permitAll()
                        .requestMatchers("/choresTracker", "/users", "/resoluteConflict", "/residences").hasAuthority("APPROVED_USER")

                        .requestMatchers("/css/**").permitAll().anyRequest().authenticated()


                )

                .formLogin()
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/myprofile") // Redirect to myprofile upon successful login
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(siteUserDetailsService)
                .passwordEncoder(passwordEncoder());

        return http.build();
    }
}