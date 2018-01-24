package amu.zhcet.firebase.auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * Authentication Token for Firebase. Only contains Firebase token as credentials
 * The credentials (token) should be parsed by AuthenticationProvider to calculate the principal
 * Returns null for principal when called
 */
class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private String credentials;

    FirebaseAuthenticationToken(String token) {
        super(null);
        this.credentials = token;
        setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - It is to be transformed by some AuthenticationProvider");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        credentials = null;
    }
}
