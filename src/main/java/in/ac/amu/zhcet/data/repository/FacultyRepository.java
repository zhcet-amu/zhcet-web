package in.ac.amu.zhcet.data.repository;


import in.ac.amu.zhcet.data.model.FacultyMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FacultyRepository extends CrudRepository<FacultyMember, Long>{
    FacultyMember getByFacultyId(String facultyId);

    List<FacultyMember> getByFacultyIdIn(List<String> facultyIds);

    List<FacultyMember> getByUserDetails_Department_Name(String name);
}
