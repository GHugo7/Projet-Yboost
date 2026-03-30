package com.example.skillforge.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "USER";

    // URL externe (ex: https://exemple.com/photo.jpg)
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    // Image uploadée depuis le PC, stockée en base64
    // On utilise @Lob car le texte peut être très long
    @Lob
    @Column(name = "avatar_data", columnDefinition = "TEXT")
    private String avatarData;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills;

    public User() {}

    // Retourne l'avatar à afficher : priorité à l'image uploadée, sinon l'URL externe
    public String getDisplayAvatar() {
        if (avatarData != null && !avatarData.isBlank()) return avatarData;
        if (avatarUrl  != null && !avatarUrl.isBlank())  return avatarUrl;
        return null;
    }

    public Long getId()            { return id; }
    public String getUsername()    { return username; }
    public String getEmail()       { return email; }
    public String getPassword()    { return password; }
    public String getRole()        { return role; }
    public String getAvatarUrl()   { return avatarUrl; }
    public String getAvatarData()  { return avatarData; }
    public List<Skill> getSkills() { return skills; }

    public void setId(Long id)           { this.id = id; }
    public void setUsername(String u)    { this.username = u; }
    public void setEmail(String e)       { this.email = e; }
    public void setPassword(String p)    { this.password = p; }
    public void setRole(String r)        { this.role = r; }
    public void setAvatarUrl(String a)   { this.avatarUrl = a; }
    public void setAvatarData(String a)  { this.avatarData = a; }
    public void setSkills(List<Skill> s) { this.skills = s; }
}