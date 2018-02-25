package amu.zhcet.data.course.registration;

import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.registration.event.CourseUnregisterEvent;
import amu.zhcet.data.user.student.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CourseRegistrationService {

    private final CourseRegistrationRepository courseRegistrationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CourseRegistrationService(CourseRegistrationRepository courseRegistrationRepository, ApplicationEventPublisher eventPublisher) {
        this.courseRegistrationRepository = courseRegistrationRepository;
        this.eventPublisher = eventPublisher;
    }

    public void removeRegistration(FloatedCourse floatedCourse, Student student) {
        courseRegistrationRepository
                .findByStudentAndFloatedCourse(student, floatedCourse)
                .ifPresent(courseRegistration -> {
                    eventPublisher.publishEvent(new CourseUnregisterEvent(courseRegistration));
                    courseRegistrationRepository.delete(courseRegistration);
                });
    }

    public void save(CourseRegistration registration) {
        courseRegistrationRepository.save(registration);
    }
}
