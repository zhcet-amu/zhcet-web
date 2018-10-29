import { login } from "./google";
import { signOut } from "./authentication";
import { loadImages } from "../app/utils";

export function setupSlideShow() {
    let imageUrls = [];
    firebase.database()
        .ref('login/slides')
        .once('value')
        .then(function (snapshot) {
            imageUrls = snapshot.val();
            return loadImages(imageUrls);
        }).then(function () {
        $('.pattern-bg').hide();
        $.backstretch(imageUrls, { duration: 2000, fade: 750 });
    }).catch(function (error) {
        console.log(error);
    });
}

export function startLoginFlow() {
    signOut();
    const checking = $('#checking');
    const loginPanel = $('#login-panel');
    const loaderMessage = $('#loader-message');

    loginPanel.find('#google-login').click(function () {
        if (showFirebaseUnavailableDialog) {
            $('#firebase-unavailable-modal').modal();
        }

        checking.show();
        loginPanel.hide();
        loaderMessage.html('Redirecting to Google...');
        login().then(function (token) {
            const form = $('.firebase-token-form');
            form.find("input[name='firebase_token']").val(token);
            form.submit();
        }).catch(function (error) {
            loginPanel.show();
            if (error)
                loaderMessage.html(error);
            else
                checking.hide();
        });
    });
}

export default {
    startLoginFlow,
    setupSlideShow
}