package amu.zhcet.firebase;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("zhcet.firebase")
public class FirebaseProperties {

    /**
     * Firebase Services are disabled if set to true
     */
    private boolean disabled = true;
    /**
     * Firebase config for initialization (Not implemented yet)
     */
    private String config;
    /**
     * Service Account JSON for the Firebase Project
     */
    private String serviceAccount;
    /**
     * Show a sad dialog telling that bad people have banned firebaseapp.com
     */
    private boolean showUnavailableDialog;

}
