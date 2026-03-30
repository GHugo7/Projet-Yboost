package com.example.skillforge.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "skill_history")
public class SkillHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    // Lien vers l'utilisateur propriétaire
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "skill_name", nullable = false)
    private String skillName;

    // 'AJOUT', 'MODIFICATION', 'SUPPRESSION'
    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public SkillHistory() {}

    public SkillHistory(User user, String skillName, String action) {
        this.user      = user;
        this.skillName = skillName;
        this.action    = action;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId()               { return id; }
    public User getUser()             { return user; }
    public String getSkillName()      { return skillName; }
    public String getAction()         { return action; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
}