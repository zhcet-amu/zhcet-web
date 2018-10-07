package amu.zhcet.data.user.totp;

import org.springframework.data.repository.CrudRepository;

public interface UserTotpRepository extends CrudRepository<UserTotp, String> {

}
