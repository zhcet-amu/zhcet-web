package amu.zhcet.core.admin.dean.registration.faculty;

import amu.zhcet.common.utils.StringUtils;
import amu.zhcet.data.department.Department;
import amu.zhcet.data.department.DepartmentRepository;
import amu.zhcet.data.user.UserRepository;
import amu.zhcet.data.user.UserService;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.security.SecurityUtils;
import amu.zhcet.storage.csv.CsvParserService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
class FacultyRegistrationAdapter {

    private final static int PASS_LENGTH = 16;

    private final DepartmentRepository departmentRepository;
    private final UserService userService;
    private final CsvParserService<FacultyUpload, FacultyMember> uploadService;

    @Autowired
    public FacultyRegistrationAdapter(DepartmentRepository departmentRepository, UserService userService, CsvParserService<FacultyUpload, FacultyMember> uploadService) {
        this.departmentRepository = departmentRepository;
        this.userService = userService;
        this.uploadService = uploadService;
    }

    UploadResult<FacultyUpload> fileToUpload(MultipartFile file) throws IOException {
        return uploadService.handleUpload(FacultyUpload.class, file);
    }

    Confirmation<FacultyMember> uploadToConfirmation(UploadResult<FacultyUpload> uploadResult) {
        FacultyIntegrityVerifier verifier = getVerifier(uploadResult);

        Confirmation<FacultyMember> facultyConfirmation = uploadService.confirmUpload(uploadResult)
                    .convert(FacultyRegistrationAdapter::fromFacultyUpload)
                    .map(verifier::getError)
                    .get();

        FacultyIntegrityVerifier.ErrorConditions conditions = verifier.getErrorConditions();

        if (conditions.isInvalidDepartment())
            facultyConfirmation.getErrors().add("Faculty Member with invalid department found");
        if (conditions.isDuplicateFacultyId())
            facultyConfirmation.getErrors().add("Faculty Member with duplicate Faculty ID found");

        if (!facultyConfirmation.getErrors().isEmpty()) {
            log.warn(facultyConfirmation.getErrors().toString());
        }

        return facultyConfirmation;
    }

    private FacultyIntegrityVerifier getVerifier(UploadResult<FacultyUpload> uploadResult) {
        List<Department> departments = departmentRepository.findAll();

        List<String> ids = uploadResult.getUploads()
                .stream()
                .map(FacultyUpload::getFacultyId)
                .collect(Collectors.toList());

        List<UserRepository.Identifier> existingUserIds = userService.getUserIdentifiers(ids);

        return new FacultyIntegrityVerifier(departments, existingUserIds);
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
