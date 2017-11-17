package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByUserId(String userId);

    User findByEmail(String email);

}
