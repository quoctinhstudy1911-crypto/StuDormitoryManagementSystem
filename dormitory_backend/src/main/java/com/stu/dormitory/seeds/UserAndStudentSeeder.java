package com.stu.dormitory.seeds;

import com.stu.dormitory.modules.application.enums.Gender;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import com.stu.dormitory.modules.student.entity.Student;
import com.stu.dormitory.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@Order(4)
@RequiredArgsConstructor
public class UserAndStudentSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Admin account (nếu chưa có)
        if (userAccountRepository.findByUsername("admin").isEmpty()) {
            log.info("Creating admin account...");
            UserAccount admin = new UserAccount();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            admin.setIsDeleted(false);
            userAccountRepository.save(admin);
            log.info("Admin account created: admin / admin123");
        } else {
            log.info("Admin account already exists.");
        }

        // 2. Tạo các sinh viên và tài khoản tương ứng với các CCCD trong EligibilitySeeder
        createStudentAndUser(
                "060204002557",
                "DH52201580",
                "Nguyễn Quốc Tịnh",
                Gender.MALE,
                LocalDate.of(2004, 5, 19),
                "0819281512",
                "quoctinhstudy1911@gmail.com",
                "Information Technology",
                "D22"
        );

        createStudentAndUser(
                "079204001234",
                "DH52201581",
                "Nguyễn Thị B",
                Gender.FEMALE,
                LocalDate.of(2004, 6, 20),
                "0909123456",
                "b.nguyen@example.com",
                "Business",
                "D22"
        );

        createStudentAndUser(
                "079204005678",
                "DH52201582",
                "Trần Văn C",
                Gender.MALE,
                LocalDate.of(2004, 7, 15),
                "0912345678",
                "c.tran@example.com",
                "Information Technology",
                "D22"
        );

        createStudentAndUser(
                "079204009999",
                "DH52201583",
                "Lê Thị D",
                Gender.FEMALE,
                LocalDate.of(2004, 8, 10),
                "0922334455",
                "d.le@example.com",
                "Economics",
                "D22"
        );
    }

    private void createStudentAndUser(String cccd, String studentCode, String fullName, Gender gender,
                                      LocalDate dob, String phone, String email, String faculty, String course) {
        if (studentRepository.findByCccd(cccd).isEmpty()) {
            log.info("Creating student account for CCCD: {}", cccd);
            Student student = new Student();
            student.setStudentCode(studentCode);
            student.setFullName(fullName);
            student.setCccd(cccd);
            student.setGender(gender);
            student.setDateOfBirth(dob);
            student.setPhone(phone);
            student.setEmail(email);
            student.setFaculty(faculty);
            student.setCourse(course);
            studentRepository.save(student);

            UserAccount user = new UserAccount();
            user.setUsername(cccd);
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(Role.USER);
            user.setStudent(student);
            user.setIsActive(true);
            user.setIsDeleted(false);
            userAccountRepository.save(user);
            log.info("Student user created: {} / 123456", cccd);
        } else {
            log.info("Student with CCCD {} already exists, skip.", cccd);
        }
    }
}