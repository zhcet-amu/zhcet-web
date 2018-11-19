package amu.zhcet.core.admin.dean.registration.course.floated;

import amu.zhcet.storage.csv.neo.Confirmation;
import amu.zhcet.storage.csv.neo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/admin/dean/api/float")
public class FloatedCourseRegistrationController {

    private final FloatedCourseRegistrationService floatedCourseRegistrationService;

    public FloatedCourseRegistrationController(FloatedCourseRegistrationService floatedCourseRegistrationService) {
        this.floatedCourseRegistrationService = floatedCourseRegistrationService;
    }

    @PostMapping
    public Result<FloatedCourseUpload> test(@RequestParam(required = false) MultipartFile file) throws IOException {
        return floatedCourseRegistrationService.parse(file);
    }

    @PostMapping("confirm")
    public Confirmation testConfirm(@RequestBody ItemState itemState) {
        return floatedCourseRegistrationService.confirm(itemState);
    }

}
