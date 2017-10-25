package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.user.UserDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailRepository extends JpaRepository<UserDetail, String> { }
