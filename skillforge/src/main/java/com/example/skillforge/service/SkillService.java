package com.example.skillforge.service;

import com.example.skillforge.model.Skill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SkillService {

    private final List<Skill> skills = new ArrayList<>();

    public List<Skill> getAll() {
        return skills;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }
}
