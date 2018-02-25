package amu.zhcet.core.admin.dean.registration.faculty;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
import amu.zhcet.storage.csv.Confirmation;
import amu.zhcet.storage.csv.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
class FacultyUploadService {

    private final FacultyRegistrationAdapter facultyRegistrationAdapter;
    private final FacultyService facultyService;
    private final PasswordFileService passwordFileService;

    @Autowired
    public FacultyUploadService(
            FacultyRegistrationAdapter facultyRegistrationAdapter,
            FacultyService facultyService,
            PasswordFileService passwordFileService
    ) {
        this.facultyRegistrationAdapter = facultyRegistrationAdapter;
        this.facultyService = facultyService;
        this.passwordFileService = passwordFileService;
    }

    public UploadResult<FacultyUpload> handleUpload(MultipartFile file) throws IOException {
        return facultyRegistrationAdapter.fileToUpload(file);
    }

    public Confirmation<FacultyMember> confirmUpload(UploadResult<FacultyUpload> uploadResult) {
        return facultyRegistrationAdapter.uploadToConfirmation(uploadResult);
    }

    @Async
    public void registerFaculty(Confirmation<FacultyMember> confirmation, RealTimeStatus status) throws IOException {
        Set<FacultyMember> facultyMembers = confirmation.getData();

        long startTime = System.nanoTime();
        status.setContext("Faculty Registration");
        status.setTotal(facultyMembers.size());

        try {
            status.start();
            facultyMembers.stream()
                    .peek(ignore -> status.increment())
                    .forEach(facultyService::register);

            float duration = (System.nanoTime() - startTime)/1000000f;
            status.setDuration(duration);
            status.setFinished(true);
            log.debug("Saved {} Faculty in {} ms", facultyMembers.size(), duration);
        } catch (Exception exception) {
            log.error("Error while saving faculty", exception);
            status.setMessage(exception.getMessage());
            status.setFailed(true);
            throw exception;
        }
    }

    public String savePasswordFile(Confirmation<FacultyMember> confirmation) throws IOException {
        List<FacultyUpload> facultyUploads = confirmation.getData().stream()
                .map(FacultyRegistrationAdapter::fromFacultyMember)
                .collect(Collectors.toList());

        return passwordFileService.create(facultyUploads).getId();
    }

}
