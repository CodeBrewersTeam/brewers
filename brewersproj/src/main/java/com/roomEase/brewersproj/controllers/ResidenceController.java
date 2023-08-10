package com.roomEase.brewersproj.controllers;

import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.models.Residence;
import com.roomEase.brewersproj.repositories.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ResidenceController {

    @Autowired
    private ResidenceRepository residenceRepository;

    @GetMapping("/residences")
    public String getResidences(Model model) {
        List<Residence> residences = residenceRepository.findAll();
        model.addAttribute("residences", residences);
        return "residence-list"; // Create a Thymeleaf template for displaying the list
    }

    @PostMapping("/residences")
    public String createResidence(@ModelAttribute Residence residence, Model model) {
        if (!residence.getName().matches("\\d+")) {
            model.addAttribute("error", "Residence should be a number");
            return "residence-list";  // Return to the list with an error message
        }
        residenceRepository.save(residence);
        return "redirect:/residences"; // Redirect back to the residence list
    }

    @PostMapping("/residences/delete/{id}")
    public RedirectView deleteResidence(@PathVariable Long id) {
        System.out.println("Deleting residence with ID: " + id);

        if (residenceRepository.existsById(id)) {
            Residence residence = residenceRepository.getById(id);
            System.out.println("Residence found: " + residence.getName());

            // Print related ApplicationUser records
            List<ApplicationUser> users = residence.getUsers();
            for (ApplicationUser user : users) {
                System.out.println("User associated with residence: " + user.getUsername());
            }

            residenceRepository.deleteById(id);
        }
        return new RedirectView("/residences");
    }

    private static final Logger logger = LoggerFactory.getLogger(ResidenceController.class);

}
