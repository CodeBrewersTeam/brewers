package com.roomEase.brewersproj.controllers;

import com.roomEase.brewersproj.models.DateHelper;
import com.roomEase.brewersproj.models.ApplicationUser;
import com.roomEase.brewersproj.models.Chore;
import com.roomEase.brewersproj.models.ChoreForm;
import com.roomEase.brewersproj.repositories.ApplicationUserRepository;
import com.roomEase.brewersproj.repositories.ChoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
public class ChoreTrackerController {

    @Autowired
    private ChoreRepository choreRepository;

    @Autowired
    private ApplicationUserRepository userRepository;


//    @GetMapping("/choresTracker")
//    public String getChoresTrackerPage(Model model, Principal principal) {
//        String currentUserUsername = principal.getName();
//        ApplicationUser currentUser = userRepository.findByUsername(currentUserUsername);
//        if(currentUser == null) {
//            // Handle this situation, maybe redirect to an error page or login
//            return "errorPage.html";
//        }
//        List<ApplicationUser> usersInSameHousehold = userRepository.findByHouseholdId(currentUser.getHouseholdId());
//        List<String> roommates = usersInSameHousehold.stream()
//                .map(ApplicationUser::getUsername)
//                .collect(Collectors.toList());
//        List<Chore> chores = choreRepository.findAll();
//        LocalDate currentDate = LocalDate.now();
//        List<Chore> futureTasks = choreRepository.findAllByDueDateAfter(currentDate);
//        Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//        model.addAttribute("choreForm", new ChoreForm(roommates, chores));
//        model.addAttribute("futureTasks", futureTasks);
//        model.addAttribute("allUsers", usersInSameHousehold);
//        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//        for (String day : days) {
//            List<Chore> dayChores = choreRepository.findAllByDayOfWeek(day);
//            model.addAttribute(day.toLowerCase() + "Chores", dayChores);
//        }
//        return "choresTracker.html";
//    }


        @GetMapping("/choresTracker")
        public String getChoresTrackerPage(Model model, Principal principal) {
            LocalDate currentDate = LocalDate.now();
            LocalDate startOfWeek = DateHelper.getStartOfWeek(currentDate);
            LocalDate endOfWeek = DateHelper.getEndOfWeek(currentDate);
            LocalTime currentTime = LocalTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedTime = currentTime.format(formatter);
            model.addAttribute("currentTime", formattedTime);
            model.addAttribute("startOfWeek", startOfWeek);
            model.addAttribute("endOfWeek", endOfWeek);

            String currentUserUsername = principal.getName();
            ApplicationUser currentUser = userRepository.findByUsername(currentUserUsername);
            if(currentUser == null) {
                // Handle this situation, maybe redirect to an error page or login
                return "errorPage.html";
            }

            List<ApplicationUser> usersInSameHousehold = userRepository.findByHouseholdId(currentUser.getHouseholdId());
            List<String> roommates = usersInSameHousehold.stream()
                    .map(ApplicationUser::getUsername)
                    .collect(Collectors.toList());
            List<Chore> chores = choreRepository.findAll();
            List<Chore> futureTasks = choreRepository.findAllByDueDateAfter(currentDate);
            Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            model.addAttribute("choreForm", new ChoreForm(roommates, chores));
            model.addAttribute("futureTasks", futureTasks);
            model.addAttribute("allUsers", usersInSameHousehold);
            String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            for (String day : days) {
                List<Chore> dayChores = choreRepository.findAllByDayOfWeek(day);
                model.addAttribute(day.toLowerCase() + "Chores", dayChores);
            }
            return "choresTracker.html";
        }



    private String saveChoreForDay(ChoreForm choreForm, String day) {
        Chore chore = new Chore();
        chore.setName(choreForm.getName());
        chore.setDescription(choreForm.getDescription());
        chore.setDayOfWeek(day);

        ApplicationUser user = userRepository.findById(choreForm.getUserId()).orElse(null);

        if(user != null) {
            chore.setAssignedUsers(Set.of(user));
        } else {
            return "redirect:/choresTracker?error=userNotFound";
        }

        choreRepository.save(chore);
        return "redirect:/choresTracker";
    }


    @PostMapping("/addChore")
        public String addChore(@ModelAttribute Chore chore) {
            choreRepository.save(chore);
            return "redirect:/choresTracker";
        }

        @PostMapping("/updateChore")
        public String updateChore(@ModelAttribute Chore chore) {
            if(chore.getId() != null) {
                choreRepository.save(chore);
            }
            return "redirect:/choresTracker";
        }

        @GetMapping("/deleteChore/{choreId}")
        public String deleteChore(@PathVariable Long choreId) {
            choreRepository.deleteById(choreId);
            return "redirect:/choresTracker";
        }


    @PostMapping("/addSundayChore")
    public String saveSundayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Sunday");
    }

    @PostMapping("/addMondayChore")
    public String saveMondayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Monday");
    }

    @PostMapping("/addTuesdayChore")
    public String saveTuesdayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Tuesday");
    }

    @PostMapping("/addWednesdayChore")
    public String saveWednesdayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Wednesday");
    }

    @PostMapping("/addThursdayChore")
    public String saveThursdayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Thursday");
    }

    @PostMapping("/addFridayChore")
    public String saveFridayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Friday");
    }

    @PostMapping("/addSaturdayChore")
    public String saveSaturdayChores(@ModelAttribute ChoreForm choreForm) {
        return saveChoreForDay(choreForm, "Saturday");
    }


///future task to add future chore to current
    @PostMapping("/moveToDayOfWeek/{choreId}")
    public String moveToDayOfWeek(@PathVariable Long choreId, @RequestParam String dayOfWeek) {
        Chore chore = choreRepository.findById(choreId).orElse(null);

        if (chore != null) {
            chore.setDayOfWeek(dayOfWeek);
            choreRepository.save(chore);
        }

        return "redirect:/choresTracker";
    }

}




//    @PostMapping("/addSundayChore")
//    public String saveSundayChores(@ModelAttribute ChoreForm choreForm) {
//        Chore chore = new Chore();
//        chore.setName(choreForm.getName());
//        chore.setDescription(choreForm.getDescription());
//        chore.setDayOfWeek("Sunday");
//
//        Set<ApplicationUser> users = userRepository.findAllByIdIn(choreForm.getUsersAssignments().keySet());
//        chore.setAssignedUsers(users);
//
//        choreRepository.save(chore);
//
//
//
//        return "redirect:/choresTracker";
//    }




//    @PostMapping("/addSundayChore")
//    public String saveSundayChores(@ModelAttribute ChoreForm choreForm) {
//        Chore chore = new Chore();
//        chore.setName(choreForm.getName());
//        chore.setDescription(choreForm.getDescription());
//        chore.setDayOfWeek("Sunday");
//
//        // Fetch user based on submitted userId
//        ApplicationUser user = userRepository.findById(choreForm.getUserId()).orElse(null);
//
//        if(user != null) {
//            chore.setAssignedUsers(Set.of(user));
//        } else {
//            return "redirect:/choresTracker?error=userNotFound";
//        }
//
//        choreRepository.save(chore);
//
//        return "redirect:/choresTracker";
//    }



//    @GetMapping("/choresTracker")
//    public String getChoresTrackerPage(Model model) {
//        List<ApplicationUser> allUsers = userRepository.findAll();  // Fetch all users
//
//        List<String> roommates = allUsers.stream()
//                .map(ApplicationUser::getUsername)
//                .collect(Collectors.toList());
//
//        List<Chore> chores = choreRepository.findAll();
//        LocalDate currentDate = LocalDate.now();
//        List<Chore> futureTasks = choreRepository.findAllByDueDateAfter(currentDate);
//
//        Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        // Fetch Sunday chores
//        List<Chore> sundayChores = choreRepository.findAllByDayOfWeek("Sunday");
//
//        ChoreForm choreForm = new ChoreForm();
//        choreForm.setRoommates(roommates);
//        choreForm.setChores(chores);
//
//        model.addAttribute("choreForm", choreForm);
//        model.addAttribute("futureTasks", futureTasks);
//        model.addAttribute("sundayChores", sundayChores);
//        model.addAttribute("allUsers", allUsers);  // Add all users to your model
//
//        return "choresTracker.html";
//    }


//    String currentUserUsername = principal.getName();
//    ApplicationUserRepository applicationUserRepository;
//    ApplicationUser currentUser = applicationUserRepository.findByUsername(currentUserUsername);
//
//        if(currentUser != null) {
//        List<ApplicationUser> usersInSameHousehold = applicationUserRepository.findByHouseholdId(currentUser.getHouseholdId());
//        model.addAttribute("users", usersInSameHousehold);




//    @GetMapping("/choresTracker")
//    public String getChoresTrackerPage(Model model, Principal principal) {
//        List<ApplicationUser> allUsers = userRepository.findAll();
//        List<String> roommates = allUsers.stream()
//                .map(ApplicationUser::getUsername)
//                .collect(Collectors.toList());
//        List<Chore> chores = choreRepository.findAll();
//        LocalDate currentDate = LocalDate.now();
//        List<Chore> futureTasks = choreRepository.findAllByDueDateAfter(currentDate);
//        Date currentDateAsDate = Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        model.addAttribute("choreForm", new ChoreForm(roommates, chores));
//        model.addAttribute("futureTasks", futureTasks);
//        model.addAttribute("allUsers", allUsers);
//
//        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
//        for (String day : days) {
//            List<Chore> dayChores = choreRepository.findAllByDayOfWeek(day);
//            model.addAttribute(day.toLowerCase() + "Chores", dayChores);
//        }
//
//        return "choresTracker.html";
//    }