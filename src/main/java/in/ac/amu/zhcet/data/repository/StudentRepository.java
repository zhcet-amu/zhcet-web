package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Attendance;
import in.ac.amu.zhcet.data.model.Course;
import in.ac.amu.zhcet.data.model.Student;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentRepository {

    private static List<Student> students;


    static {
        Course course1 = new Course();
        course1.setCode("CO313");
        course1.setName("DBMS");

        Course course2 = new Course();
        course2.setCode("CO324");
        course2.setName("Operating Systems");

        Course course3 = new Course();
        course3.setCode("CO316");
        course3.setName("Theory of Computation");

        Course course4 = new Course();
        course4.setCode("CO356");
        course4.setName("Design and Analysis of Algorithms");

        Student student = new Student();
        student.setUserId("14PEB049");
        student.setName("Areeb Jamal");

        Attendance attendance = new Attendance();
        attendance.setCourse(course2);
        attendance.setAttended(20);
        attendance.setDelivered(30);


        Attendance attendance2 = new Attendance();
        attendance2.setCourse(course3);
        attendance2.setAttended(12);
        attendance2.setDelivered(43);

        Attendance attendance3 = new Attendance();
        attendance3.setCourse(course1);
        attendance3.setAttended(32);
        attendance3.setDelivered(40);

        Attendance attendance4 = new Attendance();
        attendance4.setCourse(course4);
        attendance4.setAttended(21);
        attendance4.setDelivered(29);

        student.setAttendances(Arrays.asList(attendance, attendance2, attendance3, attendance4));

        students = new ArrayList<>();
        students.add(student);
    }

    public Student getStudentById(String userId) {
        for (Student student : students)
            if (student.getUserId().equals(userId))
                return student;

        return null;
    }

}
