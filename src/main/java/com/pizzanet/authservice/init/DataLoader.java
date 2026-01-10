package com.pizzanet.authservice.init;


import com.pizzanet.authservice.model.Role;
import com.pizzanet.authservice.model.User;
import com.pizzanet.authservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Sprawdź, czy użytkownicy już istnieją
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User("admin", passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user created");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User("user", passwordEncoder.encode("user123"));
            user.setRole(Role.USER);
            userRepository.save(user);
            System.out.println("Regular user created");
        }

        if (userRepository.findByUsername("courier").isEmpty()) {
            User courier = new User("courier", passwordEncoder.encode("courier123"));
            courier.setRole(Role.COURIER);
            courier.setEmail("courier@pizzanet.com");
            userRepository.save(courier);
            System.out.println("Courier user created");
        }
    }
}
