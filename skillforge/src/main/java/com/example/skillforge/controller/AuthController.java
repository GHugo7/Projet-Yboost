package com.example.skillforge.controller;

import com.example.skillforge.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ─── Login ────────────────────────────────────────────────

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null)  model.addAttribute("error",  "Identifiants incorrects.");
        if (logout != null) model.addAttribute("logout", "Vous avez été déconnecté.");
        return "login";
    }

    // ─── Register ─────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String confirm,
                           Model model) {

        if (!password.equals(confirm)) {
            model.addAttribute("error", "Les mots de passe ne correspondent pas.");
            return "register";
        }
        if (userService.usernameExists(username)) {
            model.addAttribute("error", "Ce nom d'utilisateur est déjà pris.");
            return "register";
        }
        if (userService.emailExists(email)) {
            model.addAttribute("error", "Cet email est déjà utilisé.");
            return "register";
        }

        userService.register(username, email, password);
        return "redirect:/login?registered=true";
    }
}