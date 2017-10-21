package in.ac.amu.zhcet.data.repository;

import in.ac.amu.zhcet.data.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Configuration findFirstByOrderByIdDesc();

}
