package in.ac.amu.zhcet.service.config;

import com.google.common.base.Strings;
import in.ac.amu.zhcet.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class ConfigurationService {

    private final ConfigurationCacheService configurationCacheService;
    private static Configuration configuration;

    @Autowired
    public ConfigurationService(ConfigurationCacheService configurationCacheService) {
        this.configurationCacheService = configurationCacheService;
        updateConfiguration(getConfigCache());
    }

    public static void updateConfiguration(Configuration configurationModel) {
        configuration = configurationModel;
        log.info("Static Configuration Set : {}", configuration);
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

    public Configuration getConfigCache() {
        Configuration cached = configurationCacheService.getConfigCache();
        return cached == null ? configuration : cached;
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

    @Transactional
    public void save(Configuration configuration) {
        configurationCacheService.save(configuration);
        updateConfiguration(configuration);
    }

    public String getBaseUrl(String defaultUrl) {
        if ((defaultUrl == null || defaultUrl.contains("127.0.0.1") || defaultUrl.contains("localhost")) &&
                !Strings.isNullOrEmpty(configuration.getUrl()))
            return configuration.getUrl();
        return Utils.getBaseUrl(defaultUrl);
    }

    public String getBaseUrl() {
        return getBaseUrl(null);
    }
}
