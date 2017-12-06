package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.data.repository.ConfigurationRepository;
import in.ac.amu.zhcet.service.config.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ConfigurationComponent {

    @Autowired
    public ConfigurationComponent(ConfigurationRepository configurationRepository, ApplicationProperties applicationProperties) {
        log.info("Checking default configuration of application");
        Configuration configuration = configurationRepository.findOne(0L);

        if (configuration == null) {
            log.warn("Default configuration absent... Building new config");
            Configuration defaultConfiguration = new Configuration();
            defaultConfiguration.setId(0L);
            defaultConfiguration.setUrl(applicationProperties.getUrl());
            configurationRepository.save(defaultConfiguration);
            log.warn("Saved default configuration : " + defaultConfiguration);
        } else {
            log.info("Configuration already present : " + configuration);
        }
    }

}
