package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.BaseUser;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<BaseUser, Long> {

    BaseUser findByUserId(String userId);

}
