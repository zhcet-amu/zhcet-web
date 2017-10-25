package in.ac.amu.zhcet.data.type;

public enum Gender {
    M, // Male
    F; // Female


    @Override
    public String toString() {
        switch (this) {
            case M:
                return "Male";
            case F:
                return "Female";
            default:
                return super.toString();
        }
    }
}
