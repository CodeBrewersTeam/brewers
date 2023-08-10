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

    // Mapping for login page
    @GetMapping("/login")
    public String getLoginPage() {
        return "login.html";
    }

    // Mapping for signup page
//    @GetMapping("/signup")
//    public String getSignUpPage(){
//        return "signup.html";
//    }

    @GetMapping("/signup")
    public String getSignUpPage(Model model){
        List<Residence> residences = residenceRepository.findAll();
//        List<Integer> residences = new ArrayList<>(Arrays.asList(1, 2, 3));
        model.addAttribute("residences", residences);
        return "signup";
    }

    @GetMapping("/aboutUs")
    public String aboutUsPage() {
        return "aboutUs.html"; //
    }

    @GetMapping("/resoluteConflict")
    public String resoluteConflictPage() {
        return "resoluteConflict.html"; //
    }

    // Mapping for signup form submission
    @PostMapping("/signup")
    public RedirectView postSignup(String firstName, String lastName, String username, String password, String email, String householdId, Boolean admin, Long telephone, Long residenceId) {
        ApplicationUser user = new ApplicationUser();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setHouseholdId(householdId);
        user.setAdmin(admin);
        user.setTelephone(telephone);

        Residence selectedResidence = residenceRepository.findById(residenceId).orElse(null);
        user.setResidence(selectedResidence);

        applicationUserRepository.save(user);

        // Authenticate the user
        authWithHttpServletRequest(username, password);

        // Redirect to login page
        //  return new RedirectView("/");
        return new RedirectView("/login");
    }

    // Method to authenticate user by username and password
    // NOTE: we need to figure out how to authenticate by householdId ? current service only does password and username
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

    // myflatmates page

    @GetMapping("/users")
    public String getUsersByHouseholdId(Model m, Principal p) {
        String currentUserUsername = p.getName();
        ApplicationUser currentUser = applicationUserRepository.findByUsername(currentUserUsername);

        if(currentUser != null) {
            List<ApplicationUser> usersInSameHousehold = applicationUserRepository.findByHouseholdId(currentUser.getHouseholdId());
            m.addAttribute("users", usersInSameHousehold);

        }
        return "myFlat.html";
    }

//    public String getUsersByHouseholdId(Model m, Principal p) {
//        String currentUserUsername = p.getName();
//        ApplicationUser currentUser = applicationUserRepository.findByUsername(currentUserUsername);
//
//        if(currentUser != null) {
//            Long currentResidenceId = currentUser.getResidence().getId();
//            List<ApplicationUser> usersInSameHouseholdAndResidence = applicationUserRepository.findByHouseholdIdAndResidenceId(currentUser.getHouseholdId(), currentResidenceId);
//            m.addAttribute("users", usersInSameHouseholdAndResidence);
//        }
//        return "myFlat.html";
//    }

    //handling user's profile

//    @GetMapping("/users/{id}")
//    public String getUserInfo(Model model, Principal p, @PathVariable Long id){
//        ApplicationUser foundUser = applicationUserRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + id));
//        model.addAttribute("foundUser", foundUser);
//
//        return "myFlat.html";
//    }

    // Mapping for user's profile page
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

    //Updating User Profile
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

    //acccounts' application page

    @GetMapping("/users/{id}")
    public String getUserInfo(Model m, Principal p, @PathVariable Long id) {
        if (p != null) {
            String username = p.getName();
            ApplicationUser applicationUser = applicationUserRepository.findByUsername(username);

            m.addAttribute("BrowsingUserUsername", username);
            m.addAttribute("BrowsingLastName", applicationUser.getLastName());
            m.addAttribute("BrowsingFirstName", applicationUser.getFirstName());
            m.addAttribute("applicationUserEmail", applicationUser.getEmail());
            m.addAttribute("applicationUserTelephone", applicationUser.getTelephone());
        }
        ApplicationUser applicationUser = applicationUserRepository.findById(id).orElseThrow();
        m.addAttribute("applicationUserUsername", applicationUser.getUsername());
        m.addAttribute("applicationUserFirstName", applicationUser.getFirstName());
        m.addAttribute("applicationUserLastName", applicationUser.getLastName());
        m.addAttribute("applicationUserEmail", applicationUser.getEmail());
        m.addAttribute("applicationUserTelephone", applicationUser.getTelephone());


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
