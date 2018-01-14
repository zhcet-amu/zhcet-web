package amu.zhcet.core.dean.datatables.floated;

import amu.zhcet.core.dean.datatables.DataTableUtils;
import amu.zhcet.data.config.ConfigurationService;
import amu.zhcet.data.course.floated.FloatedCourseService;
import amu.zhcet.data.course.floated.FloatedCourse;
import amu.zhcet.data.course.floated.FloatedCourseRepository;
import com.fasterxml.jackson.annotation.JsonView;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class FloatedDataTableController {

    private final ModelMapper modelMapper;
    private final FloatedCourseRepository floatedCourseRepository;

    @Autowired
    public FloatedDataTableController(ModelMapper modelMapper, FloatedCourseRepository floatedCourseRepository) {
        this.modelMapper = modelMapper;
        this.floatedCourseRepository = floatedCourseRepository;
    }

    @JsonView(DataTablesOutput.View.class)
    @PostMapping(value = "/dean/api/floated")
    public DataTablesOutput<FloatedCourseView> getFloatedCourses(@Valid @RequestBody DataTablesInput input) {
        DataTableUtils.convertInput(input);

        return floatedCourseRepository.findAll(input, (root, query, cb) ->
                        cb.equal(root.get("session"), ConfigurationService.getDefaultSessionCode()),
                null, this::fromFloatedCourse);
    }

    private FloatedCourseView fromFloatedCourse(FloatedCourse floatedCourse) {
        FloatedCourseView floatedCourseView = modelMapper.map(floatedCourse, FloatedCourseView.class);
        floatedCourseView.setNumStudents(floatedCourse.getCourseRegistrations().size());
        floatedCourseView.setSections(FloatedCourseService.getSections(floatedCourse).toString());

        return floatedCourseView;
    }

}
