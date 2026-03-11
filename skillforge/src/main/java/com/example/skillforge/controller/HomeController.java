package com.example.skillforge.controller;

import com.example.skillforge.model.Skill;
import com.example.skillforge.service.SkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final SkillService skillService;

    public HomeController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("skills", skillService.getAll());
        return "index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("skill", new Skill());
        return "add";
    }

    @PostMapping("/add")
    public String addSkill(@ModelAttribute Skill skill) {
        skillService.addSkill(skill);
        return "redirect:/";
    }

    @GetMapping("/test-db")
    public String testDatabase() {
        Skill skill = new Skill("Java", "Programmation backend", 5);
        skillService.addSkill(skill);
        return "redirect:/";
    }
}
