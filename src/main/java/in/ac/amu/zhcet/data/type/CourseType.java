package in.ac.amu.zhcet.data.type;

public enum CourseType {
    T, // Theory
    P  // Practical
    ;

    @Override
    public String toString() {
        switch (this) {
            case T:
                return "Theory";
            case P:
                return "Practical";
            default:
                return "Theory";
        }
    }
}
