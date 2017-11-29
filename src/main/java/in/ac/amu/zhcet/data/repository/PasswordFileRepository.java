package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.file.PasswordFile;
import org.springframework.data.repository.CrudRepository;

public interface PasswordFileRepository extends CrudRepository<PasswordFile, String> { }
