package amu.zhcet.auth;

import amu.zhcet.common.utils.Utils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class CustomAuthenticationDetails extends WebAuthenticationDetails {
    private final String remoteAddress;
    private final String totpCode;

    public CustomAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.remoteAddress = Utils.getClientIP(request);
        String totpCode = request.getParameter("totp");

        if (totpCode != null) {
            totpCode = Strings.emptyToNull(totpCode.replace(" ", ""));
        }
        this.totpCode = totpCode;
    }

    @Override
    public String getRemoteAddress() {
        return remoteAddress;
    }

    public String getTotpCode() {
        return totpCode;
    }

}