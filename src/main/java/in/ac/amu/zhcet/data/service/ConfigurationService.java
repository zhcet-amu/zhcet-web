package in.ac.amu.zhcet.data.service;

import in.ac.amu.zhcet.data.model.configuration.Configuration;
import in.ac.amu.zhcet.data.model.configuration.ConfigurationModel;
import in.ac.amu.zhcet.data.repository.ConfigurationRepository;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private static ConfigurationModel configuration;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
        updateConfiguration(configurationRepository.findFirstByOrderByIdDesc().getConfig());
        log.info("Static Configuration Set : " + configuration.toString());
    }

    private void updateConfiguration(ConfigurationModel configurationModel) {
        configuration = configurationModel;
    }

    public static String getDefaultSessionCode() {
        return getSessionCode(configuration);
    }

    private static String getSessionCode(ConfigurationModel config) {
        if (config.isAutomatic())
            return Utils.getDefaultSessionCode();
        else
            return config.getSession();
    }

    public ConfigurationModel getConfig() {
        return configurationRepository.findFirstByOrderByIdDesc().getConfig();
    }

    @Cacheable("session")
    public String getSession() {
        ConfigurationModel config = getConfig();
        return getSessionCode(config);
    }

    public String getSessionName() {
        return Utils.getSessionName(getSession());
    }

    @Transactional
    @CacheEvict("session")
    public void save(ConfigurationModel config) {
        Configuration configuration = new Configuration();
        configuration.setConfig(config);
        configurationRepository.save(configuration);
        updateConfiguration(config);
    }

}
