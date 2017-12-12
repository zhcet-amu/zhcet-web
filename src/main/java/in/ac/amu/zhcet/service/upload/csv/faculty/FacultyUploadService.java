package in.ac.amu.zhcet.service.upload.csv.faculty;

import in.ac.amu.zhcet.data.model.FacultyMember;
import in.ac.amu.zhcet.data.model.dto.upload.FacultyUpload;
import in.ac.amu.zhcet.service.FacultyService;
import in.ac.amu.zhcet.service.extra.PasswordFileService;
import in.ac.amu.zhcet.service.realtime.RealTimeStatus;
import in.ac.amu.zhcet.service.realtime.RealTimeStatusService;
import in.ac.amu.zhcet.service.upload.csv.base.Confirmation;
import in.ac.amu.zhcet.service.upload.csv.base.UploadResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FacultyUploadService {

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
