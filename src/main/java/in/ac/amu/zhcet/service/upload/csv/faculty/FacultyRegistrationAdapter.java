package in.ac.amu.zhcet.service.upload.csv.faculty;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.upload.FacultyUpload;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.data.repository.UserRepository;
import in.ac.amu.zhcet.service.UserService;
import in.ac.amu.zhcet.service.upload.csv.AbstractUploadService;
import in.ac.amu.zhcet.service.upload.csv.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.UploadResult;
import in.ac.amu.zhcet.utils.SecurityUtils;
import in.ac.amu.zhcet.utils.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FacultyRegistrationAdapter {

    private final static int PASS_LENGTH = 16;

    private final DepartmentRepository departmentRepository;
    private final UserService userService;
    private final AbstractUploadService<FacultyUpload, FacultyMember> uploadService;

    @Data
    private static class ErrorConditions {
        private boolean invalidDepartment;
        private boolean duplicateFacultyId;
    }

    @Autowired
    public FacultyRegistrationAdapter(DepartmentRepository departmentRepository, UserService userService, AbstractUploadService<FacultyUpload, FacultyMember> uploadService) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    private String getMappedValue(
            FacultyMember facultyMember,
            List<Department> departments,
            List<UserRepository.Identifier> userIds,
            ErrorConditions conditions
    ) {
        String departmentName = facultyMember.getUser().getDepartment().getName();

        Optional<Department> optional = departments.stream()
                .filter(department -> department.getName().equals(departmentName))
                .findFirst();

        if (!optional.isPresent()) {
            conditions.setInvalidDepartment(true);
            return "No such department: " + departmentName;
        } else if (userIds.parallelStream().anyMatch(identifier -> identifier.getUserId().equals(facultyMember.getFacultyId()))) {
            conditions.setDuplicateFacultyId(true);
            return "Duplicate Faculty ID";
        } else {
            facultyMember.getUser().setDepartment(optional.get());
            return null;
        }
    }

    UploadResult<FacultyUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(FacultyUpload.class, file);
    }

    Confirmation<FacultyMember> uploadToConfirmation(UploadResult<FacultyUpload> uploadResult) {
        ErrorConditions conditions = new ErrorConditions();

        List<Department> departments = departmentRepository.findAll();

        List<String> ids = uploadResult.getUploads()
                .stream()
                .map(FacultyUpload::getFacultyId)
                .collect(Collectors.toList());

        List<UserRepository.Identifier> existingUserIds = userService.getUserIdentifiers(ids);

        if (!existingUserIds.isEmpty())
            log.warn("Duplicate faculty ids : {}", existingUserIds.toString());log.warn("Duplicate enrolments : {}", existingUserIds.toString());

        Confirmation<FacultyMember> facultyConfirmation =
                uploadService.confirmUpload(uploadResult)
                    .convert(FacultyRegistrationAdapter::fromFacultyUpload)
                    .map(facultyMember -> getMappedValue(facultyMember, departments, existingUserIds, conditions))
                    .get();

        if (conditions.isInvalidDepartment())
            facultyConfirmation.getErrors().add("Faculty Member with invalid department found");
        if (conditions.isDuplicateFacultyId())
            facultyConfirmation.getErrors().add("Faculty Member with duplicate Faculty ID found");

        if (!facultyConfirmation.getErrors().isEmpty()) {
            log.warn(facultyConfirmation.getErrors().toString());
        }

        return facultyConfirmation;
    }

    private static FacultyMember fromFacultyUpload(FacultyUpload facultyUpload) {
        String password = SecurityUtils.generatePassword(PASS_LENGTH);
        facultyUpload.setPassword(password);
        FacultyMember facultyMember = new FacultyMember();
        facultyMember.setFacultyId(StringUtils.capitalizeAll(facultyUpload.getFacultyId()));
        facultyMember.setDesignation(StringUtils.capitalizeFirst(facultyUpload.getDesignation()));
        facultyMember.getUser().setName(StringUtils.capitalizeFirst(facultyUpload.getName()));
        facultyMember.getUser().setPassword(password);
        facultyMember.getUser().setDepartment(Department.builder().name(StringUtils.capitalizeFirst(facultyUpload.getDepartment())).build());
        facultyMember.getUser().getDetails().setGender(facultyUpload.getGender());

        return facultyMember;
    }

    static FacultyUpload fromFacultyMember(FacultyMember facultyMember) {
        FacultyUpload facultyUpload = new FacultyUpload();
        facultyUpload.setFacultyId(facultyMember.getFacultyId());
        facultyUpload.setName(facultyMember.getUser().getName());
        facultyUpload.setDesignation(facultyMember.getDesignation());
        facultyUpload.setDepartment(facultyMember.getUser().getDepartment().getName());
        facultyUpload.setPassword(facultyMember.getUser().getPassword());

        return facultyUpload;
    }

}
