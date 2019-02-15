package amu.zhcet.core.admin.dean.registration.course.floated;

import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.Course;
import amu.zhcet.data.course.CourseLite;
import amu.zhcet.data.course.CourseRepository;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseLite;
import amu.zhcet.data.course.floated.FloatedCourseLiteImpl;
import amu.zhcet.data.course.floated.FloatedCourseRepository;
import amu.zhcet.storage.csv.neo.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FloatedCourseRegistrationService {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class CourseResponse {
        private String code;
        private String title;
        private String department;
    }

    private final FloatedCourseRepository floatedCourseRepository;
    private final CourseRepository courseRepository;

    private final FileStorageCsvParser floatedCourseCsvParser;

    private final ModelMapper modelMapper;

    public FloatedCourseRegistrationService(FloatedCourseRepository floatedCourseRepository, CourseRepository courseRepository, FileStorageCsvParser floatedCourseCsvParser, ModelMapper modelMapper) {
        this.floatedCourseRepository = floatedCourseRepository;
        this.courseRepository = courseRepository;
        this.floatedCourseCsvParser = floatedCourseCsvParser;
        this.modelMapper = modelMapper;
    }

    public List<FloatedCourseLite> get(Collection<String> codes) {
        String defaultSessionCode = ConfigurationService.getDefaultSessionCode();

        return floatedCourseRepository.getBySessionAndCourse_CodeIn(defaultSessionCode, codes);
    }

    public Result<FloatedCourseUpload> parse(MultipartFile file) throws IOException {
        Result<FloatedCourseUpload> uploadResult = floatedCourseCsvParser.parse(FloatedCourseUpload.class, file);

        if (uploadResult.isParsed() && uploadResult.getCsv().isSuccessful()) {
            return getWrappedResult(uploadResult);
        }

        return uploadResult;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class FloatedCourseConfirmation extends Confirmation {
        private Collection<FloatedCourseLite> floated;

        public FloatedCourseConfirmation(boolean success, String message, Collection<FloatedCourseLite> floated) {
            super(success, message);
            this.floated = floated;
        }
    }

    @Transactional
    public Confirmation confirm(ItemState itemState) {
        List<Wrapper<CourseResponse>> wrappers = getWrappedCourses(new LinkedHashSet<>(itemState.getItems()));
        State newState = State.fromWrappers(wrappers);

        boolean matchingState = newState.isMatching(itemState.getState());
        log.debug("Is new state same as old state? {}", matchingState);

        if (!matchingState) {
            return new Confirmation(false, "Something went wrong! Please try again");
        } else {
            log.debug("Floating course requests {}", wrappers);
            Set<String> validCodes = wrappers.stream()
                    .filter(item -> item.getMessage() == null || item.getMessage().getType() == Type.SUCCESS)
                    .map(item -> item.getItem().getCode())
                    .collect(Collectors.toCollection(LinkedHashSet::new));

            List<FloatedCourseLite> floatedCourseLites = new ArrayList<>();
            if (!validCodes.isEmpty()) {
                log.debug("Floating courses {}", validCodes);
                List<Course> courses = courseRepository.findAllByCodeIn(validCodes);
                List<FloatedCourse> floatedCourses = courses.stream()
                    .map(course -> new FloatedCourse(ConfigurationService.getDefaultSessionCode(), course))
                    .collect(Collectors.toList());
                Iterable<FloatedCourse> floated = floatedCourseRepository.saveAll(floatedCourses);


                floated.forEach(floatedCourse -> {
                    floatedCourseLites.add(modelMapper.map(floatedCourse, FloatedCourseLiteImpl.class));
                });
            }

            return new FloatedCourseConfirmation(true, "Courses floated successfully", floatedCourseLites);
        }
    }

    private WrappedResult<FloatedCourseUpload, CourseResponse> getWrappedResult(Result<FloatedCourseUpload> uploadResult) {
        Set<String> codes = uploadResult.getCsv().getItems().stream()
                .map(FloatedCourseUpload::getCourse)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        List<Wrapper<CourseResponse>> wrappers = getWrappedCourses(codes);

        State state = State.fromWrappers(wrappers);
        log.debug("Floated Course State {}", state);
        log.debug("Is valid state? {}", state.isValid());
        return new WrappedResult<>(uploadResult, wrappers, state);
    }

    private List<Wrapper<CourseResponse>> getWrappedCourses(Set<String> codes) {
        List<FloatedCourseLite> floatedCourseLites = get(codes);
        List<CourseLite> courses = courseRepository.getByCodeIn(codes);

        return codes.stream().map(item -> {
            Optional<FloatedCourseLite> floatedCourseLiteOptional = floatedCourseLites.stream()
                    .filter(floatedCourse -> floatedCourse.getCourse().getCode().equals(item))
                    .findAny();

            if (floatedCourseLiteOptional.isPresent()) {
                FloatedCourseLite floatedCourseLite = floatedCourseLiteOptional.get();
                CourseResponse courseResponse = new CourseResponse(floatedCourseLite.getCourse().getCode(), floatedCourseLite.getCourse().getTitle(), floatedCourseLite.getCourse().getDepartment().getName());
                return new Wrapper<>(courseResponse, Message.warning("Course is already floated"));
            }

            Optional<CourseLite> courseLiteOptional = courses.stream()
                    .filter(courseLite -> courseLite.getCode().equals(item))
                    .findAny();

            if (courseLiteOptional.isPresent()) {
                CourseLite courseLite = courseLiteOptional.get();


                CourseResponse courseResponse = new CourseResponse(courseLite.getCode(), courseLite.getTitle(), courseLite.getDepartment().getName());
                if (!courseLite.isActive()) {
                    return new Wrapper<>(courseResponse, Message.error("Course is inactive"));
                } else {
                    return new Wrapper<>(courseResponse);
                }
            } else {
                return new Wrapper<>(new CourseResponse(item, null, null), Message.error("Course does not exist"));
            }
        }).collect(Collectors.toList());
    }

}
