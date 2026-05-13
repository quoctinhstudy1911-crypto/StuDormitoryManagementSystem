package com.stu.dormitory.modules.application.service;

import com.stu.dormitory.common.exception.AppException;
import com.stu.dormitory.modules.application.entity.Application;
import com.stu.dormitory.modules.application.enums.ApplicationStatus;
import com.stu.dormitory.modules.application.repository.ApplicationRepository;
import com.stu.dormitory.modules.auth.entity.UserAccount;
import com.stu.dormitory.modules.auth.enums.Role;
import com.stu.dormitory.modules.auth.repository.UserAccountRepository;
import com.stu.dormitory.modules.document.entity.ApplicationDocument;
import com.stu.dormitory.modules.document.enums.DocumentStatus;
import com.stu.dormitory.modules.document.repository.ApplicationDocumentRepository;
import com.stu.dormitory.modules.eligible.entity.EligibleStudent;
import com.stu.dormitory.modules.eligible.repository.EligibleStudentRepository;
import com.stu.dormitory.modules.student.entity.Student;
import com.stu.dormitory.modules.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ApplicationService - Xử lý tất cả business logic cho Application
 *
 * Workflows:
 * 1. REGISTRATION: createApplication() - Kiểm tra điều kiện + tạo đơn
 * 2. VALIDATION: validateApplication() - Admin đánh giá hồ sơ (VALID/INVALID)
 * 3. RESUBMISSION: resubmitApplication() - Nếu INVALID, sinh viên gửi lại
 * 4. APPROVAL: approve() - Admin duyệt (APPROVED hoặc WAITING)
 * 5. BED ASSIGNMENT: assignBed() - Cấp giường
 * 6. WAITING PROMOTION: promoteFromWaiting() - Nâng từ WAITING lên APPROVED
 * 7. CHECK-IN: confirmCheckin() - Sinh viên check-in (tạo tài khoản)
 * 8. EXPIRATION: checkAndMarkExpired() - Tự động expire sau 3 ngày
 */
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepo;
    private final StudentRepository studentRepo;
    private final UserAccountRepository userRepo;
    private final ApplicationDocumentRepository documentRepo;
    private final EligibleStudentRepository eligibleRepo;

    // ==================== REGISTRATION ====================
    public Application createApplication(String cccd, Long periodId) {

        // 1. Kiểm tra CCCD hợp lệ
        if (cccd == null || cccd.trim().isEmpty() || cccd.length() < 9) {
            throw new AppException("CCCD không hợp lệ");
        }

        // 2. Kiểm tra điều kiện tuyển sinh
        EligibleStudent eligible = eligibleRepo.findByCccdAndPeriodId(cccd, periodId)
                .orElseThrow(() -> new AppException("Bạn không đủ điều kiện đăng ký kỳ này"));

        if (eligible.getIsActive() == null || !eligible.getIsActive()) {
            throw new AppException("Hồ sơ đủ điều kiện của bạn hiện không hoạt động");
        }

        // 3. Tìm hoặc tạo sinh viên
        Student student = studentRepo.findByCccd(cccd)
                .orElseGet(() -> {
                    Student s = new Student();
                    s.setCccd(cccd);
                    s.setFullName(eligible.getFullName());
                    s.setStudentCode(eligible.getStudentCode());
                    return studentRepo.save(s);
                });

        // 4. Kiểm tra đã đăng ký kỳ này chưa
        if (applicationRepo.existsByStudentIdAndPeriodId(student.getId(), periodId)) {
            throw new AppException("Bạn đã đăng ký rồi");
        }

        // 5. Tạo đơn mới
        Application app = new Application();
        app.setStudent(student);
        app.setPeriodId(periodId);
        app.setStatus(ApplicationStatus.PENDING);
        app.setSubmittedAt(LocalDateTime.now());

        return applicationRepo.save(app);
    }

    // ==================== VALIDATION ====================
    /**
     * Admin review ứng dụng (chuyển PENDING → IN_REVIEW)
     */
    public void reviewApplication(Long appId) {
        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new AppException("Chỉ có thể review đơn PENDING");
        }

        app.setStatus(ApplicationStatus.IN_REVIEW);
        applicationRepo.save(app);
    }

    // VALIDATE - Admin đánh giá hồ sơ (VALID hoặc INVALID)
    public void validateApplication(Long appId, ApplicationStatus status, String reason) {

        if (status != ApplicationStatus.VALID && status != ApplicationStatus.INVALID) {
            throw new AppException("Trạng thái không hợp lệ, chỉ VALID hoặc INVALID");
        }

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn"));

        if (app.getStatus() != ApplicationStatus.IN_REVIEW) {
            throw new AppException("Chỉ có thể validate đơn IN_REVIEW");
        }

        app.setStatus(status);
        app.setValidatedAt(LocalDateTime.now());
        app.setValidationReason(reason);

        applicationRepo.save(app);
    }

    // VERIFY DOCUMENTS (nội bộ - kiểm tra tất cả docs đã valid chưa)
    public void verifyDocuments(Long appId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn"));

        List<ApplicationDocument> docs = documentRepo.findByApplicationId(appId);

        if (docs.isEmpty()) {
            throw new AppException("Chưa có hồ sơ");
        }

        boolean allValid = docs.stream()
                .allMatch(d -> d.getStatus() == DocumentStatus.VALID);

        if (!allValid) {
            throw new AppException("Một số hồ sơ chưa được xác minh");
        }

        app.setVerifiedAt(LocalDateTime.now());
        applicationRepo.save(app);
    }

    // RESUBMIT - Sinh viên gửi lại hồ sơ sau khi bị INVALID
    public void resubmitApplication(Long appId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy đơn"));

        if (app.getStatus() != ApplicationStatus.INVALID) {
            throw new AppException("Chỉ có thể gửi lại đơn INVALID");
        }

        app.setStatus(ApplicationStatus.PENDING);
        app.setValidatedAt(null);
        app.setValidationReason(null);
        app.setSubmittedAt(LocalDateTime.now());

        applicationRepo.save(app);
    }

    // GET NEXT - Lấy đơn tiếp theo trong hàng duyệt
    /**
     * Lấy ứng dụng VALID đầu tiên (sắp xếp theo điểm và thời gian)
     */
    public Application getNext() {

        return applicationRepo.findApprovalQueue(ApplicationStatus.VALID)
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Không còn hồ sơ"));
    }

    // ==================== APPROVAL ====================
    /**
     * Duyệt ứng dụng: VALID → APPROVED (có giường) hoặc WAITING (chờ)
     * Nếu APPROVED: Deadline = 3 ngày để check-in
     */
    public void approve(Long appId, ApplicationStatus status) {

        if (status != ApplicationStatus.APPROVED && status != ApplicationStatus.WAITING) {
            throw new AppException("Trạng thái không hợp lệ, chỉ APPROVED hoặc WAITING");
        }

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.VALID) {
            throw new AppException("Chỉ có thể duyệt đơn VALID");
        }

        app.setStatus(status);
        app.setApprovedAt(LocalDateTime.now());

        if (status == ApplicationStatus.APPROVED) {
            app.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        }

        applicationRepo.save(app);
    }

    // ==================== BED ASSIGNMENT ====================
    /**
     * Cấp giường cho ứng dụng đã APPROVED
     */
    public void assignBed(Long appId, String bedId, String roomId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new AppException("Chỉ có thể cấp giường cho đơn APPROVED");
        }

        app.setBedId(bedId);
        app.setRoomId(roomId);
        applicationRepo.save(app);
    }

    // ==================== WAITING LIST ====================
    /**
     * Nâng sinh viên từ WAITING lên APPROVED khi có giường trống
     * Đặt deadline 3 ngày mới
     */
    public void promoteFromWaiting(Long appId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.WAITING) {
            throw new AppException("Chỉ WAITING mới có thể nâng cấp");
        }

        app.setStatus(ApplicationStatus.APPROVED);
        app.setApprovedAt(LocalDateTime.now());
        app.setPaymentDeadline(LocalDateTime.now().plusDays(3));
        applicationRepo.save(app);
    }

    // ==================== CHECK-IN ====================
    /**
     * Sinh viên check-in:
     * - Kiểm tra deadline 3 ngày
     * - Tạo/kích hoạt tài khoản user
     * - APPROVED → CHECKED_IN
     */
    public void confirmCheckin(Long appId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new AppException("Chỉ có thể check-in đơn APPROVED");
        }

        if (app.getPaymentDeadline() != null && LocalDateTime.now().isAfter(app.getPaymentDeadline())) {
            app.setStatus(ApplicationStatus.EXPIRED);
            applicationRepo.save(app);
            throw new AppException("Đã quá hạn check-in");
        }

        app.setStatus(ApplicationStatus.CHECKED_IN);
        app.setCheckinAt(LocalDateTime.now());

        applicationRepo.save(app);

        handleAccount(app.getStudent());
    }

    // REJECT AFTER CHECKIN - Từ chối sau check-in
    public void rejectAfterCheckin(Long appId, String reason) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.CHECKED_IN) {
            throw new AppException("Chỉ có thể từ chối đơn CHECKED_IN");
        }

        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(reason);
        applicationRepo.save(app);
    }

    // MARK AS EXPIRED - Đánh dấu quá hạn
    public void markAsExpired(Long appId) {

        Application app = applicationRepo.findById(appId)
                .orElseThrow(() -> new AppException("Không tìm thấy"));

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new AppException("Chỉ APPROVED mới có thể hết hạn");
        }

        app.setStatus(ApplicationStatus.EXPIRED);
        applicationRepo.save(app);
    }

    // ==================== ACCOUNT MANAGEMENT ====================
    /**
     * Tạo hoặc kích hoạt tài khoản user khi check-in
     * Username: CCCD, Password: 123456 (mặc định), Role: USER
     */
    private void handleAccount(Student student) {

        Optional<UserAccount> accOpt = userRepo.findByStudentId(student.getId());

        if (accOpt.isPresent()) {
            UserAccount acc = accOpt.get();
            acc.setIsActive(true);
            userRepo.save(acc);
        } else {
            UserAccount acc = new UserAccount();
            acc.setUsername(student.getCccd());
            acc.setPassword("123456");
            acc.setIsActive(true);
            acc.setRole(Role.USER);
            acc.setStudent(student);

            userRepo.save(acc);
        }
    }

    // ==================== QUERIES ====================
    /**
     * Lấy ứng dụng theo ID
     */
    public Optional<Application> getApplicationById(Long id) {
        return applicationRepo.findById(id);
    }

    /**
     * Lấy ứng dụng theo CCCD và Period ID
     */
    public Optional<Application> getApplicationByCccdAndPeriod(String cccd, Long periodId) {
        Optional<Long> studentIdOpt = studentRepo.findByCccd(cccd).map(s -> s.getId());
        if (studentIdOpt.isEmpty()) {
            return Optional.empty();
        }
        return applicationRepo.findByStudentIdAndPeriodId(studentIdOpt.get(), periodId);
    }

    /**
     * Lấy tất cả ứng dụng theo trạng thái
     */
    public List<Application> getByStatus(ApplicationStatus status) {
        return applicationRepo.findByStatus(status);
    }

    /**
     * Lấy tất cả ứng dụng theo Period ID
     */
    public List<Application> getByPeriod(Long periodId) {
        return applicationRepo.findByPeriodId(periodId);
    }

    // ==================== SCHEDULED TASK ====================
    /**
     * Tự động kiểm tra và đánh dấu hết hạn
     * Chạy mỗi 30 phút, tìm APPROVED apps đã quá deadline
     */
    public void checkAndMarkExpired() {
        List<Application> approvedApps = applicationRepo.findByStatus(ApplicationStatus.APPROVED);

        for (Application app : approvedApps) {
            if (app.getPaymentDeadline() != null && LocalDateTime.now().isAfter(app.getPaymentDeadline())) {
                app.setStatus(ApplicationStatus.EXPIRED);
                applicationRepo.save(app);
            }
        }
    }
}


