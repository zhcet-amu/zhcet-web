package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
}
