package amu.zhcet.auth.login.persistent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Slf4j
@Service
public class PersistentTokenService implements PersistentTokenRepository {

    private final PersistentLoginRepository persistentLoginRepository;

    @Autowired
    public PersistentTokenService(PersistentLoginRepository persistentLoginRepository) {
        this.persistentLoginRepository = persistentLoginRepository;
    }

    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        persistentLoginRepository.save(getLoginFromToken(token));
    }

    @Override
    @Transactional
    public void updateToken(String series, String tokenValue, Date lastUsed) {
        persistentLoginRepository.findById(series).ifPresent(persistentLogin -> {
            persistentLogin.setToken(tokenValue);
            persistentLogin.setLastUsed(lastUsed);
            persistentLoginRepository.save(persistentLogin);
        });
    }

    @Override
    @Transactional
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        return persistentLoginRepository.findById(seriesId)
                .map(PersistentTokenService::getTokenFromLogin)
                .orElse(null);
    }

    @Override
    @Transactional
    public void removeUserTokens(String username) {
        persistentLoginRepository.deleteByUsername(username);
    }

    private static PersistentLogin getLoginFromToken(PersistentRememberMeToken token) {
        return new PersistentLogin(
                token.getSeries(),
                token.getUsername(),
                token.getTokenValue(),
                token.getDate()
        );
    }

    private static PersistentRememberMeToken getTokenFromLogin(PersistentLogin persistentLogin) {
        return new PersistentRememberMeToken(
                persistentLogin.getUsername(),
                persistentLogin.getSeries(),
                persistentLogin.getToken(),
                persistentLogin.getLastUsed()
        );
    }
}
