package com.example.skillforge.service;

import com.example.skillforge.model.User;
import com.example.skillforge.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ─── Utilitaire ───────────────────────────────────────────
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    public User getCurrentUserEntity() { return getCurrentUser(); }

    // ─── Register ─────────────────────────────────────────────
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public void register(String username, String email, String rawPassword) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole("USER");
        userRepository.save(user);
    }

    // ─── Changer username ─────────────────────────────────────
    public String changeUsername(String newUsername) {
        if (newUsername == null || newUsername.isBlank()) return "Le nom d'utilisateur ne peut pas être vide.";
        if (usernameExists(newUsername))                  return "Ce nom d'utilisateur est déjà pris.";
        User user = getCurrentUser();
        user.setUsername(newUsername);
        userRepository.save(user);
        return null;
    }

    // ─── Changer email ────────────────────────────────────────
    public String changeEmail(String newEmail) {
        if (newEmail == null || newEmail.isBlank()) return "L'email ne peut pas être vide.";
        if (emailExists(newEmail))                  return "Cet email est déjà utilisé.";
        User user = getCurrentUser();
        user.setEmail(newEmail);
        userRepository.save(user);
        return null;
    }

    // ─── Changer mot de passe ─────────────────────────────────
    public String changePassword(String currentPassword, String newPassword, String confirm) {
        if (newPassword == null || !newPassword.equals(confirm)) return "Les mots de passe ne correspondent pas.";
        if (newPassword.length() < 8)                            return "Le mot de passe doit faire au moins 8 caractères.";
        User user = getCurrentUser();
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) return "Mot de passe actuel incorrect.";
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return null;
    }

    // ─── Changer avatar (URL externe) ─────────────────────────
    @Transactional
    public String changeAvatarUrl(String avatarUrl) {
        if (avatarUrl == null || avatarUrl.isBlank()) return "L'URL ne peut pas être vide.";
        userRepository.updateAvatarUrl(getCurrentUser().getId(), avatarUrl);
        return null;
    }

    // ─── Changer avatar (upload fichier) ──────────────────────
    // On convertit l'image en base64 pour la stocker directement en BDD
    // Format : "data:image/jpeg;base64,/9j/4AAQ..." utilisable directement dans un <img src="">
    @Transactional
    public String changeAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return "Aucun fichier sélectionné.";
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) return "Le fichier doit être une image.";
        if (file.getSize() > 5 * 1024 * 1024) return "L'image ne doit pas dépasser 5 Mo.";
        try {
            String base64 = Base64.getEncoder().encodeToString(file.getBytes());
            String dataUrl = "data:" + contentType + ";base64," + base64;
            userRepository.updateAvatarData(getCurrentUser().getId(), dataUrl);
            return null;
        } catch (IOException e) {
            return "Erreur lors de la lecture du fichier.";
        }
    }



    // ─── Supprimer le compte ──────────────────────────────────
    public String deleteAccount(String password) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(password, user.getPassword())) return "Mot de passe incorrect.";
        userRepository.delete(user);
        return null;
    }

    // ─── Admin : gestion des utilisateurs ────────────────────
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public String changeUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (!newRole.equals("USER") && !newRole.equals("ADMIN")) return "Rôle invalide.";
        user.setRole(newRole);
        userRepository.save(user);
        return null;
    }

    public String deleteUser(Long userId) {
        User current = getCurrentUser();
        if (current.getId().equals(userId)) return "Vous ne pouvez pas supprimer votre propre compte depuis l'admin.";
        userRepository.deleteById(userId);
        return null;
    }
}
