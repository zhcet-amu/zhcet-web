package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Department;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends CrudRepository<Department, String>{
    Optional<Department> findByName(String name);

    List<Department> findAll();
}
