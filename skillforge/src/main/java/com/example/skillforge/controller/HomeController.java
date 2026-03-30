package com.example.skillforge.controller;

import com.example.skillforge.model.Skill;
import com.example.skillforge.service.SkillService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private final SkillService skillService;

    public HomeController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("skills",             skillService.getAll());
        model.addAttribute("categories",         skillService.countCategories());
        model.addAttribute("avgLevel",           skillService.getAverageLevel());
        model.addAttribute("mastered",           skillService.countMastered());
        model.addAttribute("progressByCategory", skillService.getProgressByCategory());
        return "index";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("skill", new Skill());
        return "add";
    }

    @PostMapping("/add")
    public String addSkill(@ModelAttribute Skill skill, RedirectAttributes ra) {
        skillService.addSkill(skill);
        ra.addFlashAttribute("success", "Compétence ajoutée avec succès !");
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteSkill(@PathVariable Long id, RedirectAttributes ra) {
        skillService.deleteSkill(id);
        ra.addFlashAttribute("success", "Compétence supprimée.");
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("skill", skillService.editSkillById(id));
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String editSkill(@PathVariable Long id, @ModelAttribute Skill skill, RedirectAttributes ra) {
        skillService.updateSkill(id, skill);
        ra.addFlashAttribute("success", "Compétence modifiée avec succès !");
        return "redirect:/";
    }
}