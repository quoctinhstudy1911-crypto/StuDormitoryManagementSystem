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
}