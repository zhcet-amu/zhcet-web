package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.service.config.Configuration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends CrudRepository<Configuration, Long> { }
