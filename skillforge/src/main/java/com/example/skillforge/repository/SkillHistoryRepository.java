package com.example.skillforge.repository;
 
import com.example.skillforge.model.SkillHistory;
import org.springframework.data.jpa.repository.JpaRepository;
 
import java.util.List;
 
public interface SkillHistoryRepository extends JpaRepository<SkillHistory, Long> {
 
    // Récupère les 20 dernières actions de l'utilisateur, triées du plus récent au plus ancien
    List<SkillHistory> findTop20ByUserIdOrderByCreatedAtDesc(Long userId);
}
 