package in.ac.amu.zhcet.service.misc;

import in.ac.amu.zhcet.configuration.ConfigurationComponent;
import in.ac.amu.zhcet.data.model.Configuration;
import in.ac.amu.zhcet.data.repository.ConfigurationRepository;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URL;

@Slf4j
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private static Configuration configuration;

    @Autowired
    public ConfigurationService(ConfigurationComponent configurationComponent) {
        this.configurationRepository = configurationComponent.getConfigurationRepository();
        updateConfiguration(getConfigCache());
        log.info("Static Configuration Set : {}", configuration);
    }

    private void updateConfiguration(Configuration configurationModel) {
        configuration = configurationModel;
    }

    public static String getDefaultSessionCode() {
        return getSessionCode(configuration);
    }

    private static String getSessionCode(Configuration config) {
        if (config.isAutomatic())
            return Utils.getDefaultSessionCode();
        else
            return config.getSession();
    }

    public static Configuration getConfiguration() {
        return configuration;
    }

    @Cacheable("configuration")
    public Configuration getConfigCache() {
        return configurationRepository.findFirstByOrderByIdDesc();
    }

    public int getMaxRetries() {
        return getConfigCache().getMaxRetries();
    }

    public int getBlockDuration() {
        return getConfigCache().getBlockDuration();
    }

    public String getSession() {
        return getSessionCode(getConfigCache());
    }

    public int getThreshold() {
        return getConfigCache().getAttendanceThreshold();
    }

    public String getSessionName() {
        return Utils.getSessionName(getSession());
    }

    private static String getBase(String urlString) {
        if(urlString == null) {
            return null;
        }

        try {
            URL url = new URL(urlString);
            return url.getProtocol() + "://" + url.getAuthority() + "/";
        } catch (Exception e) {
            log.error("Can't parse URL", e);
            return null;
        }
    }

    @Transactional
    @CacheEvict("configuration")
    public void save(Configuration configuration) {
        log.info("Saving configuration: {}", configuration);
        configuration.setId(null); // Set ID to null to save as new object
        configurationRepository.save(configuration);
        updateConfiguration(configuration);
    }

    public String getBaseUrl(String defaultUrl) {
        if ((defaultUrl == null || defaultUrl.contains("127.0.0.1") || defaultUrl.contains("localhost")) && !Utils.isEmpty(configuration.getUrl()))
            return configuration.getUrl();
        return getBase(defaultUrl);
    }

    public String getBaseUrl() {
        return getBaseUrl(null);
    }
}
