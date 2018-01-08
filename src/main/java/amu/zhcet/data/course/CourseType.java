package amu.zhcet.data.course;

public enum CourseType {
    T, // Theory
    P, // Practical
    S  // Seminar
    ;

    @Override
    public String toString() {
        switch (this) {
            case T:
                return "Theory";
            case P:
                return "Practical";
            case S:
                return "Seminar";
            default:
                return "Theory";
        }
    }
}
