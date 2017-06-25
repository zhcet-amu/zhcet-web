package in.ac.amu.zhcet.data.repository;


import in.ac.amu.zhcet.data.model.Faculty;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FacultyRepositiory extends CrudRepository<Faculty, Long>{
    Faculty getByUser_userId(String userId);

    List<Faculty> getByUser_Department_DepartmentName(String name);
}
