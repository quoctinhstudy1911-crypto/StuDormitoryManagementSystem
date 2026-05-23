package com.stu.dormitory.modules.application.enums;

public enum PriorityCategory {

    MARTYR_CHILD(100),
    WOUNDED_SOLDIER_CHILD(95),
    DISABLED_STUDENT(90),
    ORPHAN(85),
    POOR_HOUSEHOLD(80),
    ETHNIC_MINORITY(70),
    REMOTE_AREA(60),
    PARTY_MEMBER(50);

    private final int score;

    PriorityCategory(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public String getDisplayName() {
        return switch (this) {
            case POOR_HOUSEHOLD -> "Hộ nghèo";
            case MARTYR_CHILD -> "Con liệt sĩ";
            case ETHNIC_MINORITY -> "Dân tộc thiểu số";
            case DISABLED_STUDENT -> "Khuyết tật";
            case ORPHAN -> "Mồ côi";
            case REMOTE_AREA -> "Khu vực khó khăn";
            case WOUNDED_SOLDIER_CHILD -> "Con thương binh";
            case PARTY_MEMBER -> "Đảng viên";
            default -> this.name();
        };
    }

    /**
     * Loại giấy tờ cần thiết cho danh mục ưu tiên này.
     */
    public PriorityDocumentType getRequiredDocumentType() {
        return switch (this) {
            case POOR_HOUSEHOLD -> PriorityDocumentType.POVERTY_CERTIFICATE;
            case MARTYR_CHILD -> PriorityDocumentType.MARTYR_CERTIFICATE;
            case WOUNDED_SOLDIER_CHILD -> PriorityDocumentType.WOUNDED_SOLDIER_CERTIFICATE;
            case ETHNIC_MINORITY -> PriorityDocumentType.ETHNIC_CERTIFICATE;
            case DISABLED_STUDENT -> PriorityDocumentType.DISABILITY_CERTIFICATE;
            case ORPHAN -> PriorityDocumentType.ORPHAN_CERTIFICATE;
            case REMOTE_AREA -> PriorityDocumentType.REMOTE_AREA_CERTIFICATE;
            case PARTY_MEMBER -> PriorityDocumentType.PARTY_MEMBER_CERTIFICATE;
            default -> null;
        };
    }

    /**
     * Danh mục này có yêu cầu giấy tờ chứng minh hay không.
     * Mặc định true, nếu có ngoại lệ thì ghi đè.
     */
    public boolean requiresDocument() {
        return true; // tất cả đều cần giấy tờ
    }
}