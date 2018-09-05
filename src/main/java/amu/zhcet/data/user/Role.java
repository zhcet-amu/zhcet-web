package amu.zhcet.data.user;

public enum Role {

    USER(0, "Common role to all authenticated users"),
    VERIFIED_USER(10, "Common role to all authenticated and verified users"),
    STUDENT(20, "Lowest priority role. Allows user to see its attendance"),
    TEACHING_STAFF(50, "Allows user to send notifications"),
    FACULTY(60, "Allows to update attendance of courses taught by user and send notifications"),
    SUPER_FACULTY(70, "Allows faculty privileges for all courses"),
    DEPARTMENT_ADMIN(100, "Allows user to manage (float, add, delete) courses of its department, register students into courses and send notifications"),
    DEPARTMENT_SUPER_ADMIN(110, "Allows Department Admin privileges for all departments"),
    DEAN_ADMIN(200, "Allows to register and manage students, faculty, add departments, manage roles, configurations and floated courses; and send notifications"),
    SUPER_ADMIN(500, "Has privileges of Dean Admin, Department Super Admin and Faculty as well"),
    DEVELOPMENT_ADMIN(1000, "Allows to manage deployment specific settings and send notifications"),
    DEVELOPMENT_SUPER_ADMIN(1010, "Has all privileges");

    private int order;
    private String description;

    Role(int order, String description) {
        this.order = order;
        this.description = description;
    }

    public int getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ROLE_" + super.toString();
    }
}