package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserAccountRepository repository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // CHECK ADMIN EXIST
        boolean exists =
                repository.findByUsername("admin")
                        .isPresent();

        if (!exists) {

            UserAccount admin =
                    new UserAccount();

            // USERNAME
            admin.setUsername("admin");

            // PASSWORD
            admin.setPassword(
                    passwordEncoder.encode("123456")
            );

            // ROLE
            admin.setRole(Role.ADMIN);

            // ACTIVE
            admin.setIsActive(true);

            // SAVE
            repository.save(admin);

            System.out.println(
                    "Admin account seeded successfully"
            );
        }
    }
}