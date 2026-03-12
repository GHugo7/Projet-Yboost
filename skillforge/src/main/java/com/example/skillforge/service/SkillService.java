package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import com.example.skillforge.repository.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public void addSkill(Skill skill) {
        skillRepository.save(skill);
    }

    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    public Skill editSkillById(Long id) {
        return skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Skill not found"));
    }

    public List<Skill> getAll() {
        return skillRepository.findAll();
    }
}
