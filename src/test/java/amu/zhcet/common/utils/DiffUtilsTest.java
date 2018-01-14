package amu.zhcet.common.utils;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.incharge.CourseInCharge;
import amu.zhcet.data.course.incharge.CourseInChargeService;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.user.faculty.FacultyMember;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DiffUtilsTest {

    private static CourseInCharge abuCI, saifCI, saifCI2, deanCI;
    private static FacultyMember dean, saif, abu;
    private static FloatedCourse floatedCourse;

    static {
        Department department = new Department();
        department.setCode("CO");
        department.setName("Computer");

        Course course = new Course();
        course.setDepartment(department);
        course.setCode("CO111");
        course.setTitle("Algorithms");

        floatedCourse = new FloatedCourse();
        floatedCourse.setId(course.getCode());
        floatedCourse.setCourse(course);
        floatedCourse.setSession("A17");

        dean = new FacultyMember();
        dean.setFacultyId("dean");
        dean.getUser().setUserId("dean");

        saif = new FacultyMember();
        saif.setFacultyId("saif");
        saif.getUser().setUserId("saif");

        abu = new FacultyMember();
        abu.setFacultyId("abu");
        abu.getUser().setUserId("abu");

        deanCI = new CourseInCharge();
        deanCI.setId(11L);
        deanCI.setFacultyMember(dean);
        deanCI.setFloatedCourse(floatedCourse);
        deanCI.setSection("A4PE");

        saifCI = new CourseInCharge();
        saifCI.setId(12L);
        saifCI.setFacultyMember(saif);
        saifCI.setFloatedCourse(floatedCourse);

        saifCI2 = new CourseInCharge();
        saifCI2.setId(2L);
        saifCI2.setFacultyMember(saif);
        saifCI2.setFloatedCourse(floatedCourse);
        saifCI2.setSection("89");

        abuCI = new CourseInCharge();
        abuCI.setId(23L);
        abuCI.setFacultyMember(abu);
        abuCI.setFloatedCourse(floatedCourse);
    }

    private static CourseInCharge getCourseInCharge(Long id, FloatedCourse floatedCourse, FacultyMember facultyMember, String section) {
        CourseInCharge courseInCharge = new CourseInCharge();
        courseInCharge.setId(id);
        courseInCharge.setFloatedCourse(floatedCourse);
        courseInCharge.setFacultyMember(facultyMember);
        courseInCharge.setSection(section);

        return courseInCharge;
    }

    private Consumer<CourseInCharge> printing(String tag) {
        return message -> System.out.printf("%s: %s %n", tag, message);
    }

    private BiConsumer<CourseInCharge, CourseInCharge> printingTwice(String tag) {
        return (messageOld, messageNew) -> System.out.printf("%s: %s >> %s %n", tag, messageOld, messageNew);
    }

    @Test
    public void test() {
        Set<CourseInCharge> strings = new HashSet<>(Arrays.asList(saifCI, deanCI));
        Set<CourseInCharge> stringsB = new HashSet<>(Arrays.asList(deanCI, saifCI2, abuCI));

        DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build()
                .sets(strings, stringsB)
                .calculate(printing("Added"), printing("Deleted"), printing("Same"), printingTwice("Changed"));
    }

    @Test
    public void testAllAdded() {
        Set<CourseInCharge> stringsB = new HashSet<>(Arrays.asList(deanCI, saifCI2, abuCI));

        DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build()
                .sets(null, stringsB)
                .calculate(printing("Added"), printing("Deleted"), printing("Same"), printingTwice("Changed"));
    }

    @Test
    public void testAllDeleted() {
        Set<CourseInCharge> stringsB = new HashSet<>(Arrays.asList(deanCI, saifCI2, abuCI));

        DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build()
                .sets(stringsB, null)
                .calculate(printing("Added"), printing("Deleted"), printing("Same"), printingTwice("Changed"));
    }

    @Test
    public void testAllNothing() {
        DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build()
                .sets(null, null)
                .calculate(printing("Added"), printing("Deleted"), printing("Same"), printingTwice("Changed"));
    }

    @Test
    public void testAllSame() {
        Set<CourseInCharge> stringsB = new HashSet<>(Arrays.asList(deanCI, saifCI2, abuCI));

        DiffUtils.of(CourseInCharge.class)
                .areItemsSame(CourseInChargeService::sameCourseInCharge)
                .build()
                .sets(stringsB, stringsB)
                .calculate(printing("Added"), printing("Deleted"), printing("Same"), printingTwice("Changed"));
    }

    @Test
    public void courseInChargeShouldBeSameWithNullId() {
        CourseInCharge courseInCharge1 = getCourseInCharge(23L, floatedCourse, dean, null);
        CourseInCharge courseInCharge2 = getCourseInCharge(null, floatedCourse, dean, null);

        assertTrue(courseInCharge1.equals(courseInCharge2));
        assertTrue(courseInCharge2.equals(courseInCharge1));
    }

    @Test
    public void courseInChargeShouldBeSameWithDifferentId() {
        CourseInCharge courseInCharge1 = getCourseInCharge(23L, floatedCourse, dean, null);
        CourseInCharge courseInCharge2 = getCourseInCharge(54L, floatedCourse, dean, null);

        assertTrue(courseInCharge1.equals(courseInCharge2));
        assertTrue(courseInCharge2.equals(courseInCharge1));
    }

    @Test
    public void courseInChargeShouldBeDifferentWithDifferentSection() {
        CourseInCharge courseInCharge1 = getCourseInCharge(23L, floatedCourse, dean, null);
        CourseInCharge courseInCharge2 = getCourseInCharge(23L, floatedCourse, dean, "A4PE");

        assertFalse(courseInCharge1.equals(courseInCharge2));
        assertFalse(courseInCharge2.equals(courseInCharge1));
    }

    @Test
    public void courseInChargeShouldBeDifferentWithDifferentFaculty() {
        CourseInCharge courseInCharge1 = getCourseInCharge(23L, floatedCourse, saif, "A4PE");
        CourseInCharge courseInCharge2 = getCourseInCharge(23L, floatedCourse, dean, "A4PE");

        assertFalse(courseInCharge1.equals(courseInCharge2));
        assertFalse(courseInCharge2.equals(courseInCharge1));
    }

}
