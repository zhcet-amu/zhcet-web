package in.ac.amu.zhcet.data.repository;


import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FacultyRepository extends CrudRepository<FacultyMember, Long>{
    FacultyMember getByFacultyId(String facultyId);

    List<FacultyMember> getByFacultyIdIn(List<String> facultyIds);

    List<FacultyMember> getByUser_Department(Department department);

    FacultyMember getByUser_Email(String email);
}
