package in.ac.amu.zhcet.service;

import in.ac.amu.zhcet.data.model.Department;
import in.ac.amu.zhcet.data.repository.DepartmentRepository;
import in.ac.amu.zhcet.utils.DuplicateException;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    public Department findOne(Long id) {
        return departmentRepository.findOne(id);
    }

    public Department findByName(String name) {
        return departmentRepository.findByName(name);
    }

    public boolean exists(String departmentName) {
        return departmentRepository.findByName(Utils.capitalizeFirst(departmentName)) != null;
    }

    public void addDepartment(Department department) {
        if (exists(department.getName())) {
            log.warn("Duplicate Department", department.getName());
            throw new DuplicateException("Department", "name", department.getName(), department);
        }

        department.setName(Utils.capitalizeFirst(department.getName()));
        departmentRepository.save(department);
    }
}
