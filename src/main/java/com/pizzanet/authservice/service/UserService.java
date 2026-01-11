package com.pizzanet.authservice.service;

import com.pizzanet.authservice.dto.RegisterRequest;
import com.pizzanet.authservice.model.Role;
import com.pizzanet.authservice.model.User;
import com.pizzanet.authservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public List<User> getCouriers() {
        return userRepository.findByRole(Role.COURIER);
    }

    /**
     * Znajduje użytkownika po nazwie.
     * @param username Nazwa użytkownika.
     * @return Użytkownik.
     * @throws UsernameNotFoundException Jeśli użytkownik nie istnieje.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Weryfikuje dane logowania użytkownika.
     * @param username Nazwa użytkownika.
     * @param rawPassword Surowe hasło podane przez użytkownika.
     * @return Zwraca UserDetails, jeśli dane są poprawne.
     * @throws UsernameNotFoundException Jeśli użytkownik nie istnieje.
     * @throws BadCredentialsException Jeśli hasło jest nieprawidłowe.
     */
    public UserDetails authenticate(String username, String rawPassword) {
        UserDetails user = loadUserByUsername(username); // Krok 1: Znajdź użytkownika
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) { // Krok 2: Sprawdź hasło
            throw new BadCredentialsException("Invalid password");
        }
        return user;
    }

    @Transactional
    public void registerUser(RegisterRequest request) {
        // Sprawdź czy użytkownik już istnieje
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Użytkownik o podanym loginie już istnieje");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Użytkownik o podanym emailu już istnieje");
        }

        // Utwórz nowego użytkownika
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
    }

    @Transactional
    public User updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Użytkownik o ID " + userId + " nie istnieje"));
        
        user.setRole(newRole);
        return userRepository.save(user);
    }

}
