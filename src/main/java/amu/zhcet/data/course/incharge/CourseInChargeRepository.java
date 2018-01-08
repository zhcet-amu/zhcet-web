package amu.zhcet.data.course.incharge;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.faculty.FacultyMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseInChargeRepository extends JpaRepository<CourseInCharge, Long> {
    List<CourseInCharge> findByFacultyMemberAndFloatedCourse_Session(FacultyMember member, String session);
    Optional<CourseInCharge> findByFloatedCourseAndFacultyMemberAndSection(FloatedCourse floatedCourse, FacultyMember facultyMember, String section);
}
