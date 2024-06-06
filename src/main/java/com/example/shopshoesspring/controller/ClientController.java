package com.example.shopshoesspring.controller;

import com.example.shopshoesspring.entity.*;
import com.example.shopshoesspring.repository.*;
import com.example.shopshoesspring.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/client")
public class ClientController {

    private final UserService userService;
    private final LightRepository lightRepository;
    private final LightTypeRepository lightTypeRepository;
    private final UserLightRepository userLightRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CountRepository countRepository;
    @GetMapping("/home")
    public String homePage() {
        return "сhome";
    }

    @GetMapping("/catalog")
    public String catalogPage(Model model) {
        List<LightType> lightTypes = lightTypeRepository.findAll();
        model.addAttribute("categories", lightTypes);

        return "catalog";
    }
    @PostMapping("/updateUser")
    public String updateUser(@RequestParam("userLogin") String login, @RequestParam("userPassword") String password, Model model, Principal principal) {
        String loginUser = principal.getName();
        Optional<User> userOptional = userService.findUserByUserLogin(loginUser);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUserLogin(login);
            String encryptedPassword = passwordEncoder.encode(password);
            user.setUserPassword(encryptedPassword);
            userRepository.save(user);
            model.addAttribute("message", "Данные пользователя успешно обновлены");
        } else {
            model.addAttribute("message", "Ошибка обновления данных пользователя");
        }
        Optional<User> userOptional1 = userService.findUserByUserLogin(loginUser);
        model.addAttribute("user", userOptional1.get());
        return "cabinet";
    }
    @GetMapping("/cabinet")
    public String cabinetPage(Model model, Principal principal) {
        String login = principal.getName();
        Optional<User> userOptional = userService.findUserByUserLogin(login);
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
            return "cabinet";
        }
        return "cabinet";
    }
    @GetMapping("/category/{id}")
    public String showCategoryDetails(@PathVariable("id") Long id, Model model) {
        LightType category = lightTypeRepository.findById(id).orElse(null);
        if (category == null) {
            return "redirect:/client/catalog";
        }
        List<Light> lights = lightRepository.findByLightTypeId(category.getId());
        model.addAttribute("lights", lights);
        return "lights";
    }

    @GetMapping("/light/{id}")
    public String showLightDetails(@PathVariable("id") Long id, Model model) {
        Light light = lightRepository.findById(id).orElse(null);
        if (light == null) {
            return "redirect:/client/catalog";
        }
        model.addAttribute("light", light);
        return "light_details";
    }

    @PostMapping("/add")
    public String addOrder(HttpServletRequest request, Principal principal) {
        String mobileNumber = request.getParameter("mobileNumber");
        String message = request.getParameter("message");
        Long lightId = Long.valueOf(request.getParameter("lightId"));
        Integer count = Integer.valueOf(request.getParameter("cn"));
        String login = principal.getName();
        Optional<User> userOptional = userService.findUserByUserLogin(login);
        Optional<Light> lightOptional = lightRepository.findById(lightId);
        if (userOptional.isEmpty() || lightOptional.isEmpty()) {
            return "redirect:/client/er";
        }
        UserLight userLight = UserLight.builder()
                .userLightCompleted(false)
                .user(userOptional.get())
                .light(lightOptional.get())
                .phone(mobileNumber)
                .message(message)
                .date(new Date(new java.util.Date().getTime()))
                .build();
        UserLight userLight1 = userLightRepository.save(userLight);
        Count count1 = new Count();
        count1.setVal(count);
        count1.setUserLight(userLight1);
        countRepository.save(count1);
        return "redirect:/client/sc";
    }
    @GetMapping("/sc")
    public String successPage(){
        return "sc";
    }
    @GetMapping("er")
    public String errorPage(){
        return "er";
    }

    @GetMapping("/about")
    public String aboutPage(){
        return "about";
    }
    @GetMapping("/contacts")
    public String contactsPage(){
        return "contacts";
    }
}