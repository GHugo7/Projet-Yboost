package com.example.skillforge.model;

import jakarta.persistence.*;

@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;

    @Column(name = "skill_name", nullable = false)
    private String name;

    @Column(name = "skill_category")
    private String category;

    @Column(name = "skill_level")
    private int level;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Skill() {}

    public Skill(String name, String category, int level) {
        this.name = name;
        this.category = category;
        this.level = level;
    }
    
    public Long getId()       { return id; }
    public String getName()   { return name; }
    public String getCategory(){ return category; }
    public int getLevel()     { return level; }
    public User getUser()     { return user; }

    public void setId(Long id)          { this.id = id; }
    public void setName(String name)    { this.name = name; }
    public void setCategory(String c)   { this.category = c; }
    public void setLevel(int level)     { this.level = level; }
    public void setUser(User user)      { this.user = user; }
}