var Login = (function ($) {

    function setupSlideShow() {
        var imageUrls = [];
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

    function setupLoginFlow() {
        Authentication.auth.signOut();
        var checking = $('#checking');
        var loginPanel = $('#login-panel');
        var loaderMessage = $('#loader-message');

        loginPanel.find('#google-login').click(function () {
            checking.show();
            loginPanel.hide();
            loaderMessage.html('Redirecting to Google...');
            Authentication.google.login().then(function (token) {
                var form = $('.firebase-token-form');
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

    setupSlideShow();

    return {
        startLoginFlow: setupLoginFlow
    }
}(jQuery));