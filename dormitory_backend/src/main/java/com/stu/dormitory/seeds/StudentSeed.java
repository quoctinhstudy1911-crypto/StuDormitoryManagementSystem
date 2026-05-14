package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import com.stu.dormitory.modules.student.entity.Student;
import com.stu.dormitory.modules.student.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StudentSeed implements CommandLineRunner {

    private final StudentRepository studentRepository;

    private final UserAccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // CHECK EXIST ACCOUNT
        boolean exists = accountRepository
                .findByUsername("060204002557")
                .isPresent();

        if (exists) {

            return;
        }

        // ==============================
        // CREATE STUDENT
        // ==============================

        Student student = new Student();

        student.setStudentCode("DH52201580");

        student.setFullName("Nguyễn Quốc Tịnh");

        student.setCccd("060204002557");

        student.setGender("Male");

        student.setDateOfBirth(
                LocalDate.of(2004, 5, 19)
        );

        student.setPhone("0819281512");

        student.setEmail("quoctinhstudy1911@gmail.com");

        student.setFaculty("Information Technology");

        student.setCourse("D22");

        studentRepository.save(student);

        // ==============================
        // CREATE ACCOUNT
        // ==============================

        UserAccount account =
                new UserAccount();

        account.setUsername(
                student.getCccd()
        );

        account.setPassword(
                passwordEncoder.encode("123456")
        );

        account.setRole(Role.USER);

        account.setStudent(student);

        account.setIsActive(true);

        account.setIsDeleted(false);

        accountRepository.save(account);

        System.out.println(
                "✅ Student account seeded"
        );
    }
}