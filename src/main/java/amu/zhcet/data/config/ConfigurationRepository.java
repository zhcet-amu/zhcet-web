package amu.zhcet.data.config;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> {

    Optional<Configuration> findById(long id);

}
