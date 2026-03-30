package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import com.example.skillforge.model.User;
import com.example.skillforge.repository.SkillRepository;
import com.example.skillforge.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserRepository  userRepository;
    private final SkillRepository skillRepository;

    public AdminService(UserRepository userRepository, SkillRepository skillRepository) {
        this.userRepository  = userRepository;
        this.skillRepository = skillRepository;
    }

    // ─── Stats globales ───────────────────────────────────────

    public long countTotalUsers() {
        return userRepository.count();
    }

    public long countTotalSkills() {
        return skillRepository.count();
    }

    public long countAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole()))
                .count();
    }

    // Nombre moyen de skills par utilisateur
    public String avgSkillsPerUser() {
        long users  = userRepository.count();
        long skills = skillRepository.count();
        if (users == 0) return "0";
        return String.format("%.1f", (double) skills / users);
    }

    // Top 5 catégories les plus utilisées sur toute la plateforme
    public Map<String, Long> getTopCategories() {
        return skillRepository.findAll().stream()
                .filter(s -> s.getCategory() != null && !s.getCategory().isBlank())
                .collect(Collectors.groupingBy(Skill::getCategory, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // ─── Données pour le tableau utilisateurs ─────────────────

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Nombre de skills pour un utilisateur donné
    public long countSkillsForUser(Long userId) {
        return skillRepository.findByUserId(userId).size();
    }
}