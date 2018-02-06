package amu.zhcet.data.department;

import io.micrometer.core.lang.NonNullApi;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

@NonNullApi
public interface DepartmentRepository extends CrudRepository<Department, String>{
    Optional<Department> findByName(String name);

    List<Department> findAll();
}
