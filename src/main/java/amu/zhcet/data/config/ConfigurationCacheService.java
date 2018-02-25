package amu.zhcet.data.config;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@CacheConfig(cacheNames = "configuration")
public class ConfigurationCacheService {

    private final ConfigurationRepository configurationRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ConfigurationCacheService(ConfigurationRepository configurationRepository, ModelMapper modelMapper) {
        this.configurationRepository = configurationRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public Configuration getConfig() {
        return configurationRepository.findById(0L).orElse(null);
    }

    @Cacheable
    public Configuration getConfigCache() {
        return getConfig();
    }

    @CacheEvict(allEntries = true)
    public void save(Configuration configuration) {
        configuration.setId(0L); // Set ID to 0 as there is only 1 config instance
        Configuration saved = getConfig();

        if (saved.equals(configuration)) {
            log.debug("Same configuration saved. Returning...");
            return;
        }

        modelMapper.map(configuration, saved);
        log.debug("Saving configuration: {}", saved);
        configurationRepository.save(saved);
    }

}
