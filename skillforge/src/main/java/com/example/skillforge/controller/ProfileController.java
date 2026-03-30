package com.example.skillforge.controller;

import com.example.skillforge.service.AdminService;
import com.example.skillforge.service.SkillService;
import com.example.skillforge.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/profil")
public class ProfileController {

    private final UserService  userService;
    private final SkillService skillService;
    private final AdminService adminService;

    public ProfileController(UserService userService,
                             SkillService skillService,
                             AdminService adminService) {
        this.userService  = userService;
        this.skillService = skillService;
        this.adminService = adminService;
    }

    // ─── Page profil ──────────────────────────────────────────
    @GetMapping
    public String profil(Model model) {
        model.addAttribute("user",    userService.getCurrentUserEntity());
        model.addAttribute("history", skillService.getHistory());
        model.addAttribute("stats",   buildStats());

        // On n'envoie les données admin que si l'utilisateur est ADMIN
        String role = userService.getCurrentUserEntity().getRole();
        if ("ADMIN".equals(role)) {
            model.addAttribute("allUsers",      adminService.getAllUsers());
            model.addAttribute("globalStats",   buildGlobalStats());
            model.addAttribute("topCategories", adminService.getTopCategories());
        }
        return "profil";
    }

    // ─── Avatar URL externe ───────────────────────────────────
    @PostMapping("/avatar/url")
    public String changeAvatarUrl(@RequestParam String avatarUrl, RedirectAttributes ra) {
        String error = userService.changeAvatarUrl(avatarUrl);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Avatar mis à jour !");
        return "redirect:/profil";
    }

    // ─── Avatar upload fichier ────────────────────────────────
    @PostMapping("/avatar/upload")
    public String uploadAvatar(@RequestParam("avatarFile") MultipartFile file, RedirectAttributes ra) {
        String error = userService.changeAvatarFile(file);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Avatar mis à jour !");
        return "redirect:/profil";
    }

    // ─── Changer username ─────────────────────────────────────
    @PostMapping("/username")
    public String changeUsername(@RequestParam String newUsername,
                                @RequestParam String confirmUsername,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes ra) {
        if (!newUsername.equals(confirmUsername)) {
            ra.addFlashAttribute("error", "Les noms d'utilisateur ne correspondent pas.");
            return "redirect:/profil";
        }
        String error = userService.changeUsername(newUsername);
        if (error != null) {
            ra.addFlashAttribute("error", error);
            return "redirect:/profil";
        }
        // On déconnecte pour forcer une reconnexion avec le nouveau username
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) new SecurityContextLogoutHandler().logout(request, response, auth);
        ra.addFlashAttribute("success", "Nom d'utilisateur mis à jour ! Veuillez vous reconnecter.");
        return "redirect:/login";
    }

    // ─── Changer email ────────────────────────────────────────
    @PostMapping("/email")
    public String changeEmail(@RequestParam String newEmail,
                              @RequestParam String confirmEmail,
                              RedirectAttributes ra) {
        if (!newEmail.equals(confirmEmail)) {
            ra.addFlashAttribute("error", "Les adresses email ne correspondent pas.");
            return "redirect:/profil";
        }
        String error = userService.changeEmail(newEmail);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Email mis à jour !");
        return "redirect:/profil";
    }

    // ─── Changer mot de passe ─────────────────────────────────
    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirm,
                                 RedirectAttributes ra) {
        String error = userService.changePassword(currentPassword, newPassword, confirm);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Mot de passe mis à jour !");
        return "redirect:/profil";
    }

    // ─── Supprimer le compte ──────────────────────────────────
    @PostMapping("/delete")
    public String deleteAccount(@RequestParam String password,
                                HttpServletRequest request,
                                HttpServletResponse response,
                                RedirectAttributes ra) {
        String error = userService.deleteAccount(password);
        if (error != null) {
            ra.addFlashAttribute("error", error);
            return "redirect:/profil";
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/register?deleted=true";
    }

    // ─── Admin : changer rôle ─────────────────────────────────
    @PostMapping("/admin/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String changeRole(@RequestParam Long userId,
                             @RequestParam String newRole,
                             RedirectAttributes ra) {
        String error = userService.changeUserRole(userId, newRole);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Rôle mis à jour !");
        return "redirect:/profil";
    }

    // ─── Admin : supprimer un utilisateur ────────────────────
    @PostMapping("/admin/delete-user")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@RequestParam Long userId, RedirectAttributes ra) {
        String error = userService.deleteUser(userId);
        if (error != null) ra.addFlashAttribute("error", error);
        else               ra.addFlashAttribute("success", "Utilisateur supprimé.");
        return "redirect:/profil";
    }

    // ─── Utilitaires ─────────────────────────────────────────
    private Map<String, Object> buildStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total",      skillService.getAll().size());
        stats.put("categories", skillService.countCategories());
        stats.put("avgLevel",   skillService.getAverageLevel());
        stats.put("mastered",   skillService.countMastered());
        return stats;
    }

    private Map<String, Object> buildGlobalStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalUsers",      adminService.countTotalUsers());
        stats.put("totalSkills",     adminService.countTotalSkills());
        stats.put("totalAdmins",     adminService.countAdmins());
        stats.put("avgSkillsPerUser",adminService.avgSkillsPerUser());
        return stats;
    }
}