package com.roomEase.brewersproj.controllers;
import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.models.Residence;
import com.roomEase.brewersproj.repositories.ApplicationUserRepository;


import com.roomEase.brewersproj.repositories.ResidenceRepository;
import org.springframework.ui.Model;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class ApplicationUserController {

    @Autowired
    ApplicationUserRepository applicationUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    HttpServletRequest request;

    @Autowired
    ResidenceRepository residenceRepository;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login.html";
    }

    @GetMapping("/signup")
    public String getSignUpPage(Model model){
        List<Residence> residences = residenceRepository.findAll();
//        List<Integer> residences = new ArrayList<>(Arrays.asList(1, 2, 3));
        model.addAttribute("residences", residences);
        return "signup";
    }
    @GetMapping("/aboutUs")
    public String aboutUsPage() {
        return "aboutUs.html";
    }

    @GetMapping("/resoluteConflict")
    public String resoluteConflictPage() {
        return "resoluteConflict.html";
    }

    @GetMapping("/errorPage")
    public String showErrorPage(Model model) {
        model.addAttribute("errorMessage", "An error occurred");
        return "errorPage.html";
    }

    @PostMapping("/signup")
    public RedirectView postSignup(String firstName, String lastName, String username, String password, String email, String householdId, Boolean admin, Long telephone, Long residenceId) {
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setHouseholdId(householdId);
        user.setTelephone(telephone);

        Residence selectedResidence = residenceRepository.findById(residenceId).orElse(null);
        List<ApplicationUser> usersInResidence = applicationUserRepository.findByResidence(selectedResidence);

        if (usersInResidence == null || usersInResidence.isEmpty()) {
            user.setAdmin(true);  // Set as admin if they're the first in the residence
            user.setIsApproved(true);  // Auto-approve the first user
        } else {
            user.setAdmin(false); // All subsequent users are non-admins until an admin promotes them
            user.setIsApproved(false); // They are not approved upon registration
        }

        user.setResidence(selectedResidence);
        applicationUserRepository.save(user);
//        authWithHttpServletRequest(username, password);
        return new RedirectView("/login");
    }

    @GetMapping("/adminDashboard")
    public String getAdminDashboard(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            ApplicationUser currentUser = applicationUserRepository.findByUsername(username);

            if(currentUser.getAdmin()) {
                Residence userResidence = currentUser.getResidence();
                List<ApplicationUser> nonApprovedUsers = applicationUserRepository.findNonApprovedUsersByResidence(userResidence);
                List<ApplicationUser> nonAdminUsers = applicationUserRepository.findNonAdminUsersByResidence(userResidence);

                model.addAttribute("nonApprovedUsers", nonApprovedUsers);
                model.addAttribute("nonAdminUsers", nonAdminUsers);

                return "adminDashboard.html";
            } else {
                return "redirect:/";
            }
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/users/approve/{id}")
    public RedirectView approveUser(@PathVariable Long id, Principal principal) {
        ApplicationUser currentUser = applicationUserRepository.findByUsername(principal.getName());
        if (currentUser.getAdmin()) { // Ensure that the current user is an admin
            ApplicationUser userToApprove = applicationUserRepository.findById(id).orElseThrow();
            userToApprove.setIsApproved(true);
            applicationUserRepository.save(userToApprove);
        }
        return new RedirectView("/adminDashboard");
    }

    @PostMapping("/users/promote/{id}")
    public RedirectView promoteUserToAdmin(@PathVariable Long id, Principal principal) {
        ApplicationUser currentUser = applicationUserRepository.findByUsername(principal.getName());
        if (currentUser.getAdmin()) { // Ensure that the current user is an admin
            ApplicationUser userToPromote = applicationUserRepository.findById(id).orElseThrow();
            userToPromote.setAdmin(true);
            applicationUserRepository.save(userToPromote);
        }
        return new RedirectView("/adminDashboard");
    }


    public void authWithHttpServletRequest(String username, String password) {
        try {
            System.out.println("Authenticating user: " + username);

            request.login(username, password);
        } catch (ServletException e) {
            System.out.println("Error during authentication");
            e.printStackTrace();
        }
    }

    @GetMapping("/")
    public String getIndexPage(Model model, Principal principal) {

        if(principal != null){
            String username = principal.getName();
            ApplicationUser user = applicationUserRepository.findByUsername(username);
            model.addAttribute("username", username);
        }
        return "index.html";
    }
    @GetMapping("/users")
    public String getUsersByResidence(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            ApplicationUser currentUser = applicationUserRepository.findByUsername(username);
            model.addAttribute("currentUser", currentUser);

            Residence userResidence = currentUser.getResidence();
            List<ApplicationUser> usersInSameResidence = applicationUserRepository.findByResidence(userResidence);
            model.addAttribute("users", usersInSameResidence);
        }
        return "myFlat.html";
    }

    @GetMapping("/myprofile")
    public String getUserProfile(Model model, Principal principal) {
        if(principal != null){
            String username = principal.getName();
            ApplicationUser currentUser = applicationUserRepository.findByUsername(username);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("username", username);
        }
        return "myprofile";
    }

    @PostMapping("/myprofile/update")
    public RedirectView updateUserProfile(Principal principal, String firstName, String lastName, String email, Long telephone) {
        if (principal != null) {
            String username = principal.getName();
            ApplicationUser currentUser = applicationUserRepository.findByUsername(username);

            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);
            currentUser.setEmail(email);
            currentUser.setTelephone(telephone);

            applicationUserRepository.save(currentUser);
        }
        return new RedirectView("/myprofile");
    }

    @GetMapping("/users/{id}")
    public String getUserInfo(Model model, Principal principal, @PathVariable Long id) {
        ApplicationUser applicationUser = applicationUserRepository.findById(id).orElseThrow();

        if (principal != null) {
            String username = principal.getName();
            ApplicationUser currentUser = applicationUserRepository.findByUsername(username);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("username", username);
        }

        model.addAttribute("applicationUser", applicationUser);
        return "/user-info.html";
    }

    @PutMapping("/users/{id}")
    public RedirectView editUserInfo(Principal p, @PathVariable Long id, String username, String firstName, String lastName, String email, Long telephone) {
        if (p != null) {
            ApplicationUser applicationUser = applicationUserRepository.findById(id).orElseThrow();
            applicationUser.setUsername(username);
            applicationUser.setFirstName(firstName);
            applicationUser.setLastName(lastName);
            applicationUser.setEmail(email);
            applicationUser.setTelephone(telephone);
            applicationUserRepository.save(applicationUser);
        }
        return new RedirectView("/users/" + id);
    }

    @DeleteMapping("/users/{id}")
    public RedirectView deleteUser(@PathVariable Long id) {
        applicationUserRepository.deleteById(id);
        return new RedirectView("/users");
    }

}
