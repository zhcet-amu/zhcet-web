package amu.zhcet.data.course.incharge;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.user.faculty.FacultyMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface CourseInChargeRepository extends CrudRepository<CourseInCharge, Long> {
    List<CourseInCharge> findByFacultyMemberAndFloatedCourse_Session(FacultyMember member, String session);
    Optional<CourseInCharge> findByFloatedCourseAndFacultyMemberAndSection(FloatedCourse floatedCourse, FacultyMember facultyMember, String section);
}
