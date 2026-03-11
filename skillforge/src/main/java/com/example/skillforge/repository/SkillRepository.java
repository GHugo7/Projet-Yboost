package com.example.skillforge.repository;

import com.example.skillforge.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

}