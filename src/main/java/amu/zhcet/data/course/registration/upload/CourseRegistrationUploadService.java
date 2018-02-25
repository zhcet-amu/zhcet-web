package amu.zhcet.data.course.registration.upload;

import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.registration.CourseRegistration;
import amu.zhcet.data.course.registration.event.CourseRegisterEvent;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CourseRegistrationUploadService {

    private final FloatedCourseService floatedCourseService;
    private final CourseRegistrationUploadAdapter courseRegistrationUploadAdapter;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CourseRegistrationUploadService(FloatedCourseService floatedCourseService, CourseRegistrationUploadAdapter courseRegistrationUploadAdapter, ApplicationEventPublisher eventPublisher) {
        this.floatedCourseService = floatedCourseService;
        this.courseRegistrationUploadAdapter = courseRegistrationUploadAdapter;
        this.eventPublisher = eventPublisher;
    }

    public void upload(Course course, MultipartFile file, RedirectAttributes attributes, HttpSession session) {
        try {
            UploadResult<RegistrationUpload> result = handleUpload(file);

            if (!result.getErrors().isEmpty()) {
                attributes.addFlashAttribute("errors", result.getErrors());
            } else {
                attributes.addFlashAttribute("success", true);
                Confirmation<CourseRegistration> confirmation = confirmUpload(course, result);
                session.setAttribute("confirmRegistration", confirmation);
            }
        } catch (IOException ioe) {
            log.error("Error registering students", ioe);
        }
    }

    public void register(Course course, RedirectAttributes attributes, Confirmation<CourseRegistration> registrations) {
        try {
            List<CourseRegistration> courseRegistrations = registerStudents(course, registrations);
            sendRegistrationEvents(courseRegistrations);
            attributes.addFlashAttribute("registered", true);
        } catch (Exception e) {
            log.error("Error confirming student registrations", e);
            attributes.addFlashAttribute("unknown_error", true);
        }
    }

    private UploadResult<RegistrationUpload> handleUpload(MultipartFile file) throws IOException {
        return courseRegistrationUploadAdapter.fileToUpload(file);
    }

    private Confirmation<CourseRegistration> confirmUpload(Course course, UploadResult<RegistrationUpload> uploadResult) {
        return courseRegistrationUploadAdapter.uploadToConfirmation(course, uploadResult);
    }

    @Transactional
    public List<CourseRegistration> registerStudents(Course course, Confirmation<CourseRegistration> confirmation) {
        Optional<FloatedCourse> floatedCourseOptional = floatedCourseService.getFloatedCourse(course);

        if (!floatedCourseOptional.isPresent())
            return Collections.emptyList();

        FloatedCourse floatedCourse = floatedCourseOptional.get();

        List<CourseRegistration> registrations = new ArrayList<>();

        for (CourseRegistration registration : confirmation.getData()) {
            registration.setFloatedCourse(floatedCourse);
            registration.getAttendance().setId(registration.generateId());
            registrations.add(registration);
        }

        floatedCourse.getCourseRegistrations().addAll(registrations);
        floatedCourseService.save(floatedCourse);

        return registrations;
    }

    private void sendRegistrationEvents(List<CourseRegistration> courseRegistrations) {
        courseRegistrations.stream()
                .map(CourseRegisterEvent::new)
                .forEach(eventPublisher::publishEvent);
    }

}
