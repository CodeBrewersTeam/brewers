package com.roomEase.brewersproj.controllers;

import com.roomEase.brewersproj.models.Residence;
import com.roomEase.brewersproj.repositories.ResidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

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
    public String createResidence(@ModelAttribute Residence residence) {
        residenceRepository.save(residence);
        return "redirect:/residences"; // Redirect back to the residence list
    }

    @DeleteMapping("/residences/{id}")
    public RedirectView deleteResidence(@PathVariable Long id) {
        residenceRepository.deleteById(id);
        return new RedirectView("/residences");
    }

//    @PostMapping("/residences/delete/{id}")
//    public RedirectView deleteResidence(@PathVariable Long id) {
//        residenceRepository.deleteById(id);
//        return new RedirectView("/residences");
//    }

//    @PostMapping("/residences")
//    public String createResidence(@RequestParam String name, RedirectAttributes redirectAttributes) {
//        Residence residence = new Residence();
//        residence.setName(name);
//        residenceRepository.save(residence);
//
//        redirectAttributes.addFlashAttribute("message", "Residence created successfully!");
//        return "redirect:/residences";  // redirecting to GET request
//    }

}
