package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.base.user.UserAuth;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<UserAuth, Long> {

    UserAuth findByUserId(String userId);

    List<UserAuth> findAllByOrderByCreatedAtDesc();

}
