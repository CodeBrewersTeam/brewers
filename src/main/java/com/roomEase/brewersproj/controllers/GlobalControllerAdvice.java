package com.roomEase.brewersproj.controllers;

import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.repositories.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @ModelAttribute("loggedInUser")
    public ApplicationUser loggedInUser(Principal principal) {
        if (principal != null) {
            String username = principal.getName();

            return applicationUserRepository.findByUsername(username);
        }

        return null;
    }
}