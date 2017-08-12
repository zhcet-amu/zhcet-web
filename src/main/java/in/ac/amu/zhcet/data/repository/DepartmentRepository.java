package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Department;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DepartmentRepository extends CrudRepository<Department, Long>{
    Department findByName(String name);

    List<Department> findAll();
}
