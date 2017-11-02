package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Configuration findFirstByOrderByIdDesc();

}
