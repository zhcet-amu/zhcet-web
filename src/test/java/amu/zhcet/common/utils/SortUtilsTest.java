package amu.zhcet.common.utils;

import amu.zhcet.core.admin.faculty.attendance.upload.AttendanceUpload;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.user.student.Student;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;


public class SortUtilsTest {

    private static final String AREEB_FACULTY_NO = "14PEB049";
    private static final String DIVY_FACULTY_NO = "14PEB250";
    private static final String SAIM_FACULTY_NO = "11PEB283";

    private static final String SECTION_A4PE = "A4PE";
    private static final String SECTION_A5PE = "A5PE";

    @Test
    public void testSortAttendanceUpload() {
        AttendanceUpload areeb = new AttendanceUpload();
        AttendanceUpload divy = new AttendanceUpload();
        AttendanceUpload saim = new AttendanceUpload();

        areeb.setSection(SECTION_A4PE);
        areeb.setFacultyNo(AREEB_FACULTY_NO);

        divy.setSection(SECTION_A4PE);
        divy.setFacultyNo(DIVY_FACULTY_NO);

        saim.setSection(SECTION_A5PE);
        saim.setFacultyNo(SAIM_FACULTY_NO);

        List<AttendanceUpload> attendanceUpload = new ArrayList<>(Arrays.asList(saim, divy, areeb));

        SortUtils.sortAttendanceUpload(attendanceUpload);

        assertThat(attendanceUpload, contains(areeb, divy, saim));
    }

    @Test
    public void testSortCourseRegistrations() {
        CourseRegistration areebRegistration = new CourseRegistration();
        CourseRegistration divyRegistration = new CourseRegistration();
        CourseRegistration saimRegistration = new CourseRegistration();

        Student areeb = new Student();
        Student divy = new Student();
        Student saim = new Student();

        areebRegistration.setStudent(areeb);
        divyRegistration.setStudent(divy);
        saimRegistration.setStudent(saim);

        areeb.setFacultyNumber(AREEB_FACULTY_NO);
        areeb.setSection(SECTION_A4PE);

        divy.setFacultyNumber(DIVY_FACULTY_NO);
        divy.setSection(SECTION_A4PE);

        saim.setFacultyNumber(SAIM_FACULTY_NO);
        saim.setSection(SECTION_A5PE);

        List<CourseRegistration> courseRegistrations = new ArrayList<>(Arrays.asList(divyRegistration, saimRegistration, areebRegistration));

        SortUtils.sortCourseAttendance(courseRegistrations);

        assertThat(courseRegistrations, contains(areebRegistration, divyRegistration, saimRegistration));
    }

    @Test
    public void testSortCourses() {
        Course co456 = new Course();
        co456.setCode("CO456");
        co456.setSemester(4);

        Course coa1910 = new Course();
        coa1910.setCode("COA1910");
        coa1910.setSemester(0);

        Course coc2910 = new Course();
        coc2910.setCode("COC291");
        coc2910.setSemester(3);

        Course coc2060 = new Course();
        coc2060.setCode("COC2060");
        coc2060.setSemester(3);

        List<Course> courses = new ArrayList<>(Arrays.asList(co456, coa1910, coc2910, coc2060));

        SortUtils.sortCourses(courses);

        assertThat(courses, contains(coa1910, coc2060, coc2910, co456));
    }

}
