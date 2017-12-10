package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.CourseInCharge;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.FloatedCourse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseInChargeRepository extends JpaRepository<CourseInCharge, Long> {
    List<CourseInCharge> findByFacultyMemberAndFloatedCourse_Session(FacultyMember member, String session);
    Optional<CourseInCharge> findByFloatedCourseAndFacultyMemberAndSection(FloatedCourse floatedCourse, FacultyMember facultyMember, String section);
}
