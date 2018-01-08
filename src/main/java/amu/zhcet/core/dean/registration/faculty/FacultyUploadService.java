package amu.zhcet.core.dean.registration.faculty;

import amu.zhcet.common.realtime.RealTimeStatus;
import amu.zhcet.common.realtime.RealTimeStatusService;
import amu.zhcet.data.user.faculty.FacultyMember;
import amu.zhcet.data.user.faculty.FacultyService;
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
class FacultyUploadService {

    private final FacultyRegistrationAdapter facultyRegistrationAdapter;
    private final FacultyService facultyService;
    private final PasswordFileService passwordFileService;
    private final RealTimeStatusService realTimeStatusService;

    @Autowired
    public FacultyUploadService(
            FacultyRegistrationAdapter facultyRegistrationAdapter,
            FacultyService facultyService,
            PasswordFileService passwordFileService,
            RealTimeStatusService realTimeStatusService
    ) {
        this.facultyRegistrationAdapter = facultyRegistrationAdapter;
        this.facultyService = facultyService;
        this.realTimeStatusService = realTimeStatusService;
        this.passwordFileService = passwordFileService;
    }

    public UploadResult<FacultyUpload> handleUpload(MultipartFile file) throws IOException {
        return facultyRegistrationAdapter.fileToUpload(file);
    }

    public Confirmation<FacultyMember> confirmUpload(UploadResult<FacultyUpload> uploadResult) {
        return facultyRegistrationAdapter.uploadToConfirmation(uploadResult);
    }

    public RealTimeStatus registerFaculty(Confirmation<FacultyMember> confirmation) throws IOException {
        RealTimeStatus status = realTimeStatusService.install();

        status.setMeta(saveFile(confirmation));
        facultyService.register(confirmation.getData(), status);

        return status;
    }

    private String saveFile(Confirmation<FacultyMember> confirmation) throws IOException {
        List<FacultyUpload> facultyUploads = confirmation.getData().stream()
                .map(FacultyRegistrationAdapter::fromFacultyMember)
                .collect(Collectors.toList());

        return passwordFileService.create(facultyUploads).getId();
    }

}
