package in.ac.amu.zhcet.data.repository;


import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.model.FacultyMember;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;
import java.util.Optional;

public interface FacultyRepository extends DataTablesRepository<FacultyMember, String> {
    Optional<FacultyMember> getByFacultyId(String facultyId);

    List<FacultyMember> getByFacultyIdIn(List<String> facultyIds);

    List<FacultyMember> getByUser_DepartmentAndWorking(Department department, boolean working);

    List<FacultyMember> getByUser_Department(Department department);

    List<FacultyMember> findAllByWorking(boolean working);
}
