package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.user.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, String> {

    interface Identifier {
        String getUserId();
    }

    Optional<User> findByUserId(String userId);

    Optional<User> findByEmail(String email);

    List<Identifier> getByUserIdIn(List<String> userIds);

}
