package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import com.example.skillforge.model.SkillHistory;
import com.example.skillforge.model.User;
import com.example.skillforge.repository.SkillHistoryRepository;
import com.example.skillforge.repository.SkillRepository;
import com.example.skillforge.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository        skillRepository;
    private final UserRepository         userRepository;
    private final SkillHistoryRepository historyRepository;

    public SkillService(SkillRepository skillRepository,
                        UserRepository userRepository,
                        SkillHistoryRepository historyRepository) {
        this.skillRepository   = skillRepository;
        this.userRepository    = userRepository;
        this.historyRepository = historyRepository;
    }

    // ─── Utilitaire ───────────────────────────────────────────
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    // ─── CRUD avec historique ─────────────────────────────────

    public List<Skill> getAll() {
        return skillRepository.findByUserId(getCurrentUser().getId());
    }

    public void addSkill(Skill skill) {
        User user = getCurrentUser();
        skill.setUser(user);
        skillRepository.save(skill);
        // On enregistre l'action dans l'historique
        historyRepository.save(new SkillHistory(user, skill.getName(), "AJOUT"));
    }

    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétence introuvable"));
        User user = getCurrentUser();
        historyRepository.save(new SkillHistory(user, skill.getName(), "SUPPRESSION"));
        skillRepository.deleteById(id);
    }

    public Skill editSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétence introuvable : " + id));
    }

    public void updateSkill(Long id, Skill updated) {
        skillRepository.updateSkill(id, updated.getName(), updated.getCategory(), updated.getLevel());
        User user = getCurrentUser();
        historyRepository.save(new SkillHistory(user, updated.getName(), "MODIFICATION"));
    }

    // ─── Stats ────────────────────────────────────────────────

    public long countCategories() {
        return getAll().stream()
                .map(Skill::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .count();
    }

    public String getAverageLevel() {
        List<Skill> skills = getAll();
        if (skills.isEmpty()) return "0";
        double avg = skills.stream().mapToInt(Skill::getLevel).average().orElse(0);
        return String.format("%.1f", avg);
    }

    public long countMastered() {
        return getAll().stream().filter(s -> s.getLevel() >= 5).count();
    }

    public Map<String, Integer> getProgressByCategory() {
        return getAll().stream()
                .filter(s -> s.getCategory() != null && !s.getCategory().isBlank())
                .collect(Collectors.groupingBy(
                        Skill::getCategory,
                        LinkedHashMap::new,
                        Collectors.averagingInt(Skill::getLevel)
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (int) Math.round(e.getValue() / 5.0 * 100),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
    }

    // ─── Historique ───────────────────────────────────────────
    public List<SkillHistory> getHistory() {
        return historyRepository.findTop20ByUserIdOrderByCreatedAtDesc(getCurrentUser().getId());
    }
}