package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import com.example.skillforge.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    // ─── CRUD ────────────────────────────────────────────────

    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    public void addSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public Skill editSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compétence introuvable : " + id));
    }

    public void updateSkill(Long id, Skill updated) {
        skillRepository.updateSkill(id, updated.getName(), updated.getCategory(), updated.getLevel());
    }

    // ─── Stats pour le tableau de bord ───────────────────────

    /** Nombre de catégories distinctes (catégories non nulles/vides) */
    public long countCategories() {
        return getAll().stream()
                .map(Skill::getCategory)
                .filter(c -> c != null && !c.isBlank())
                .distinct()
                .count();
    }

    /** Niveau moyen arrondi à 1 décimale, ex : 3.4 */
    public String getAverageLevel() {
        List<Skill> skills = getAll();
        if (skills.isEmpty()) return "0";
        double avg = skills.stream()
                .mapToInt(Skill::getLevel)
                .average()
                .orElse(0);
        return String.format("%.1f", avg);
    }

    /** Compétences au niveau maximum (5) */
    public long countMastered() {
        return getAll().stream()
                .filter(s -> s.getLevel() >= 5)
                .count();
    }

    /**
     * Progression par catégorie en pourcentage (niveau moyen / 5 * 100).
     * Retourne une map triée par catégorie : { "Backend" -> 80, "Design" -> 60, … }
     */
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
}