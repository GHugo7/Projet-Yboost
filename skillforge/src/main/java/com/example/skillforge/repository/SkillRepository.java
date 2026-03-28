package com.example.skillforge.repository;
import com.example.skillforge.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SkillRepository extends JpaRepository<Skill, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Skill s SET s.name = :name, s.category = :category, s.level = :level WHERE s.id = :id")
    void updateSkill(@Param("id") Long id, @Param("name") String name,
                     @Param("category") String category, @Param("level") int level);
}